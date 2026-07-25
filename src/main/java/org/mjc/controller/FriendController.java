package org.mjc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mjc.dto.DTO;
import org.mjc.exception.BusinessException;
import org.mjc.exception.ErrorCode;
import org.mjc.service.AccountService;
import org.mjc.service.ChatMessageService;
import org.mjc.service.UserFriendService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 好友关系接口
 *
 * @author Online_Voting
 * @since 2026-07-25
 */
@Tag(name = "好友管理", description = "好友添加、删除、列表等接口")
@RestController
@RequestMapping("/api/friend")
public class FriendController {

    @Resource
    private UserFriendService userFriendService;

    @Resource
    private AccountService accountService;

    @Resource
    private ChatMessageService chatMessageService;

    @Operation(summary = "发送好友申请", description = "向指定用户发送好友申请")
    @PostMapping("/request/{friendId}")
    public DTO<Void> requestFriend(
            @Parameter(description = "目标用户ID", required = true)
            @PathVariable Long friendId,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        Long userId = (Long) request.getAttribute("currentUserId");

        // 检查目标用户是否存在
        if (accountService.getById(friendId) == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        boolean result = userFriendService.requestFriend(userId, friendId);
        if (!result) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "申请失败，可能已是好友或已发送过申请");
        }
        return new DTO<>(200, "申请已发送");
    }

    @Operation(summary = "同意好友申请", description = "同意收到的好友申请")
    @PutMapping("/request/{id}/accept")
    public DTO<Void> acceptFriend(
            @Parameter(description = "好友关系ID", required = true)
            @PathVariable Long id,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        Long userId = (Long) request.getAttribute("currentUserId");
        boolean result = userFriendService.acceptFriend(id, userId);
        if (!result) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "操作失败");
        }
        return new DTO<>(200, "已同意好友申请");
    }

    @Operation(summary = "拒绝好友申请", description = "拒绝收到的好友申请")
    @PutMapping("/request/{id}/reject")
    public DTO<Void> rejectFriend(
            @Parameter(description = "好友关系ID", required = true)
            @PathVariable Long id,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        Long userId = (Long) request.getAttribute("currentUserId");
        boolean result = userFriendService.rejectFriend(id, userId);
        if (!result) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "操作失败");
        }
        return new DTO<>(200, "已拒绝好友申请");
    }

    @Operation(summary = "获取好友列表", description = "获取当前用户的所有好友")
    @GetMapping("/list")
    public DTO<List<Map<String, Object>>> getFriendList(jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        List<Map<String, Object>> list = userFriendService.getFriendList(userId);
        // 补充未读消息数
        for (Map<String, Object> friend : list) {
            Long friendId = (Long) friend.get("friendId");
            long unread = chatMessageService.getUnreadCountWithFriend(userId, friendId);
            friend.put("unreadCount", unread);
        }
        DTO<List<Map<String, Object>>> dto = new DTO<>(200, "查询成功");
        dto.setT(list);
        return dto;
    }

    @Operation(summary = "获取好友申请列表", description = "获取当前用户收到的好友申请")
    @GetMapping("/requests")
    public DTO<List<Map<String, Object>>> getPendingRequests(jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        List<Map<String, Object>> list = userFriendService.getPendingRequests(userId);
        DTO<List<Map<String, Object>>> dto = new DTO<>(200, "查询成功");
        dto.setT(list);
        return dto;
    }

    @Operation(summary = "删除好友", description = "删除指定好友")
    @DeleteMapping("/{friendId}")
    public DTO<Void> deleteFriend(
            @Parameter(description = "好友用户ID", required = true)
            @PathVariable Long friendId,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        Long userId = (Long) request.getAttribute("currentUserId");
        boolean result = userFriendService.deleteFriend(userId, friendId);
        if (!result) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "删除好友失败");
        }
        return new DTO<>(200, "已删除好友");
    }
}
