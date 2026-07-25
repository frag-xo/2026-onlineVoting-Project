package org.mjc.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.mjc.service.AIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * AI 小助手服务实现
 * <p>
 * 支持两种模式：
 * 1. 真实模式 — 调用 GLM-4-Flash API（ai.enabled=true）
 * 2. 模拟模式 — 关键词匹配回复（ai.enabled=false）
 *
 * @author Online_Voting
 * @since 2026-07-25
 */
@Slf4j
@Service
public class AIServiceImpl implements AIService {

    @Resource
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ai.enabled:false}")
    private boolean aiEnabled;

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.api-url:}")
    private String apiUrl;

    @Value("${ai.model:glm-4-flash}")
    private String model;

    /** 系统提示词 — 告诉 AI 它是谁 */
    private static final String SYSTEM_PROMPT = """
            你是在线投票系统"Online_Voting"的AI小助手，功能包括：

            1. 投票管理：用户可发布投票、参与投票、查看结果
            2. 积分系统：注册+20、每日登录+10、投票+5、评论+2、收藏+3、被推荐+50
            3. 等级系统：Lv1-普通、Lv2-50分、Lv3-100分、Lv4-200分、Lv5-500分
            4. 收藏/评论/点赞功能
            5. 转盘抽奖：投票后可抽奖
            6. 审核机制：普通用户发布需管理员审核
            7. 好友系统：加好友聊天
            8. 排行榜：积分排行、投票排行

            请简短、友好地回答问题（50字以内），不要说你是AI模型，说自己是"小助手"。
            """;

    @Override
    public String chat(Long userId, String message) {
        if (message == null || message.isBlank()) {
            return "请说点什么吧 👀";
        }

        if (aiEnabled && apiKey != null && !apiKey.isEmpty()) {
            try {
                return callGLM(message);
            } catch (Exception e) {
                log.error("GLM API 调用失败，降级到模拟模式", e);
                return mockReply(message);
            }
        } else {
            return mockReply(message);
        }
    }

    /**
     * 调用 GLM-4-Flash API
     */
    private String callGLM(String message) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));
        messages.add(Map.of("role", "user", "content", message));
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("choices").get(0).path("message").path("content").asText("嗯，我没想好怎么回答😅");
        }
        return "小助手暂时没反应过来，稍后试试？😅";
    }

    /**
     * 模拟模式 — 关键词回复
     */
    private String mockReply(String message) {
        String msg = message.toLowerCase();

        if (msg.contains("投票") && (msg.contains("怎么") || msg.contains("如何") || msg.contains("发布") || msg.contains("创建"))) {
            return "点击首页右上角的「发布投票」按钮，填写标题和选项就能发布啦！普通用户的投票需要管理员审核通过后才可见 📝";
        }
        if (msg.contains("积分") && (msg.contains("怎么") || msg.contains("如何") || msg.contains("获得"))) {
            return "赚积分的方式有：注册+20、每日登录+10、投票+5、评论+2、收藏+3、被推荐+50。去个人中心看完整规则吧！💰";
        }
        if (msg.contains("等级") || msg.contains("升级")) {
            return "等级由总积分决定：Lv1(0分)、Lv2(50分)、Lv3(100分)、Lv4(200分)、Lv5(500分)。多参与投票攒积分吧！🏆";
        }
        if (msg.contains("好友") && (msg.contains("怎么") || msg.contains("如何") || msg.contains("添加"))) {
            return "去个人中心的「好友管理」可以搜索用户并发送好友申请，对方同意后就能聊天啦！👫";
        }
        if (msg.contains("抽奖") || msg.contains("转盘")) {
            return "参与投票后，在投票详情页会弹出转盘抽奖，有机会获得积分奖励哦！🎡";
        }
        if (msg.contains("审核") || msg.contains("通过")) {
            return "普通用户发布的投票需要管理员审核，一般在24小时内会有结果，审核通过后就会展示给大家 ✅";
        }
        if (msg.contains("收藏") && (msg.contains("怎么") || msg.contains("如何"))) {
            return "在投票详情页点击「收藏」按钮就能收藏啦，可以在个人中心的「我的收藏」里查看 ⭐";
        }
        if (msg.contains("你好") || msg.contains("嗨") || msg.contains("hello") || msg.contains("在吗")) {
            return "你好呀！我是 Online_Voting 小助手，有什么需要帮忙的吗？😊";
        }
        if (msg.contains("排行") || msg.contains("排名")) {
            return "首页有「排行榜」页面，可以查看热门投票排行和积分排行 📊";
        }
        if (msg.contains("谢谢") || msg.contains("感谢")) {
            return "不客气，有什么问题随时找我！😄";
        }

        return "这个问题我还在学习中 🤔 你可以试试：\n- 「怎么发布投票？」\n- 「积分怎么获得？」\n- 「怎么添加好友？」";
    }
}
