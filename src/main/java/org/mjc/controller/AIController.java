package org.mjc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mjc.dto.DTO;
import org.mjc.service.AIService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * AI 小助手接口
 *
 * @author Online_Voting
 * @since 2026-07-25
 */
@Tag(name = "AI 小助手", description = "AI 小助手聊天接口")
@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Resource
    private AIService aiService;

    @Operation(summary = "AI 聊天", description = "向AI小助手发送消息并获取回复")
    @PostMapping("/chat")
    public DTO<Map<String, String>> chat(
            @RequestParam String message,
            jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        String reply = aiService.chat(userId, message);
        DTO<Map<String, String>> dto = new DTO<>(200, "成功");
        dto.setT(Map.of("reply", reply));
        return dto;
    }
}
