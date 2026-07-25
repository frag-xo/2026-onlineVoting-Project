package org.mjc.websocket;

import jakarta.annotation.Resource;
import org.mjc.utils.JwtUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

/**
 * WebSocket 握手拦截器 — JWT 鉴权
 *
 * @author Online_Voting
 * @since 2026-07-25
 */
@Component
public class ChatWebSocketInterceptor implements HandshakeInterceptor {

    @Resource
    private JwtUtils jwtUtils;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // 从 URL 参数中获取 token
        URI uri = request.getURI();
        String query = uri.getQuery();
        if (query == null) return false;

        String token = null;
        for (String param : query.split("&")) {
            String[] pair = param.split("=", 2);
            if ("token".equals(pair[0]) && pair.length == 2) {
                token = pair[1];
                break;
            }
        }

        if (token == null || !jwtUtils.validateToken(token)) {
            return false;
        }

        Long userId = jwtUtils.getUserId(token);
        if (userId == null) {
            return false;
        }

        // 将 userId 存入 WebSocket session 的 attributes 中
        attributes.put("userId", userId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 无需处理
    }
}
