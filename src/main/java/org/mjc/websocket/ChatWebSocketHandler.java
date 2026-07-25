package org.mjc.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.mjc.entity.ChatMessage;
import org.mjc.service.ChatMessageService;
import org.mjc.service.UserFriendService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天 WebSocket 处理器
 * <p>
 * 客户端连接：ws://host:port/ws/chat?token=xxx
 * 消息格式（JSON）：
 * - 发送：{"toUserId": 123, "content": "你好"}
 * - 接收：{"fromUserId": 456, "content": "你好", "createTime": "..."}
 *
 * @author Online_Voting
 * @since 2026-07-25
 */
@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    /** 在线用户池：userId → WebSocketSession */
    private static final Map<Long, WebSocketSession> onlineSessions = new ConcurrentHashMap<>();

    @Resource
    private ChatMessageService chatMessageService;

    @Resource
    private UserFriendService userFriendService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            onlineSessions.put(userId, session);
            log.info("WebSocket 连接建立: userId={}, sessionId={}", userId, session.getId());
            // 发送在线用户数（可选的扩展）
            broadcastOnlineCount();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        Long fromUserId = (Long) session.getAttributes().get("userId");
        if (fromUserId == null) return;

        try {
            // 解析消息
            @SuppressWarnings("unchecked")
            Map<String, Object> msgMap = objectMapper.readValue(message.getPayload(), Map.class);
            Long toUserId = Long.valueOf(msgMap.get("toUserId").toString());
            String content = (String) msgMap.get("content");

            if (toUserId == null || content == null || content.isBlank()) {
                sendError(session, "参数不完整");
                return;
            }

            // 校验是否好友关系
            if (!userFriendService.isFriend(fromUserId, toUserId)) {
                sendError(session, "不是好友关系，无法发送消息");
                return;
            }

            // 保存消息到数据库
            ChatMessage saved = chatMessageService.sendMessage(fromUserId, toUserId, content);
            if (saved == null) {
                sendError(session, "消息保存失败");
                return;
            }

            // 构建转发消息
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("fromUserId", fromUserId);
            response.put("content", content);
            response.put("createTime", saved.getCreateTime().toString());

            String responseJson = objectMapper.writeValueAsString(response);

            // 如果接收方在线，实时推送
            WebSocketSession toSession = onlineSessions.get(toUserId);
            if (toSession != null && toSession.isOpen()) {
                toSession.sendMessage(new TextMessage(responseJson));
            }

            // 给发送方回执确认
            Map<String, Object> ack = new java.util.HashMap<>(response);
            ack.put("type", "ack");
            ack.put("messageId", saved.getId());
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(ack)));

        } catch (Exception e) {
            log.error("处理WebSocket消息异常", e);
            sendError(session, "消息处理异常");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            onlineSessions.remove(userId);
            log.info("WebSocket 连接关闭: userId={}, sessionId={}", userId, session.getId());
            broadcastOnlineCount();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        Long userId = (Long) session.getAttributes().get("userId");
        log.error("WebSocket 传输错误: userId={}", userId, exception);
        try {
            if (session.isOpen()) {
                session.close(CloseStatus.SERVER_ERROR);
            }
        } catch (IOException e) {
            log.error("关闭WebSocket会话失败", e);
        }
    }

    private void sendError(WebSocketSession session, String msg) {
        try {
            Map<String, Object> error = new java.util.HashMap<>();
            error.put("type", "error");
            error.put("message", msg);
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(error)));
        } catch (IOException e) {
            log.error("发送错误消息失败", e);
        }
    }

    private void broadcastOnlineCount() {
        // 可选的：广播在线人数（后续可扩展）
        log.debug("当前在线用户数: {}", onlineSessions.size());
    }

    /**
     * 获取在线用户数
     */
    public static int getOnlineCount() {
        return onlineSessions.size();
    }
}
