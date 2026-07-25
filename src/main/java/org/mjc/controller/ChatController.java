package org.mjc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mjc.dto.DTO;
import org.mjc.entity.ChatMessage;
import org.mjc.service.ChatMessageService;
import org.mjc.service.UserFriendService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 聊天消息接口
 *
 * @author Online_Voting
 * @since 2026-07-25
 */
@Tag(name = "聊天管理", description = "聊天消息、历史记录等接口")
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Resource
    private ChatMessageService chatMessageService;

    @Resource
    private UserFriendService userFriendService;

    @Operation(summary = "获取历史消息", description = "获取与某个好友的聊天历史（分页倒序）")
    @GetMapping("/messages/{friendId}")
    public DTO<List<ChatMessage>> getHistoryMessages(
            @Parameter(description = "好友用户ID", required = true)
            @PathVariable Long friendId,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数", example = "50")
            @RequestParam(defaultValue = "50") Integer pageSize,
            jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        List<ChatMessage> messages = chatMessageService.getHistoryMessages(userId, friendId, pageNum, pageSize);
        DTO<List<ChatMessage>> dto = new DTO<>(200, "查询成功");
        dto.setT(messages);
        return dto;
    }

    @Operation(summary = "获取未读消息数", description = "获取当前用户的总未读消息数")
    @GetMapping("/unread-count")
    public DTO<Long> getUnreadCount(jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        long count = chatMessageService.getUnreadCount(userId);
        DTO<Long> dto = new DTO<>(200, "查询成功");
        dto.setT(count);
        return dto;
    }

    @Operation(summary = "标记消息已读", description = "标记与某个好友的所有消息为已读")
    @PutMapping("/read/{friendId}")
    public DTO<Void> markAsRead(
            @Parameter(description = "好友用户ID", required = true)
            @PathVariable Long friendId,
            jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        chatMessageService.markAsRead(userId, friendId);
        return new DTO<>(200, "已标记已读");
    }
}
