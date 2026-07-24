package org.mjc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mjc.dto.DTO;
import org.mjc.entity.VoteComment;
import org.mjc.entity.VoteFavorite;
import org.mjc.entity.VoteNotification;
import org.mjc.exception.BusinessException;
import org.mjc.exception.ErrorCode;
import org.mjc.service.VoteCommentService;
import org.mjc.service.VoteFavoriteService;
import org.mjc.service.VoteNotificationService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 用户中心接口（收藏、评论、通知）
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Tag(name = "用户中心", description = "用户收藏、评论、通知相关接口")
@RestController
@RequestMapping("/api/user")
public class UserCenterController {

    @Resource
    private VoteFavoriteService voteFavoriteService;

    @Resource
    private VoteCommentService voteCommentService;

    @Resource
    private VoteNotificationService voteNotificationService;

    // ==================== 收藏功能 ====================

    @Operation(summary = "收藏投票", description = "收藏指定投票")
    @PostMapping("/favorite/{voteId}")
    public DTO<Void> favorite(
            @Parameter(description = "投票ID", required = true)
            @PathVariable Long voteId,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        Long userId = (Long) request.getAttribute("currentUserId");
        boolean result = voteFavoriteService.favorite(voteId, userId);
        if (!result) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "收藏失败，可能已收藏");
        }
        return new DTO<>(200, "收藏成功");
    }

    @Operation(summary = "取消收藏", description = "取消收藏指定投票")
    @DeleteMapping("/favorite/{voteId}")
    public DTO<Void> unfavorite(
            @Parameter(description = "投票ID", required = true)
            @PathVariable Long voteId,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        Long userId = (Long) request.getAttribute("currentUserId");
        boolean result = voteFavoriteService.unfavorite(voteId, userId);
        if (!result) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "取消收藏失败");
        }
        return new DTO<>(200, "取消收藏成功");
    }

    @Operation(summary = "检查是否已收藏", description = "检查用户是否已收藏指定投票")
    @GetMapping("/favorite/check/{voteId}")
    public DTO<Boolean> isFavorited(
            @Parameter(description = "投票ID", required = true)
            @PathVariable Long voteId,
            jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        boolean result = voteFavoriteService.isFavorited(voteId, userId);
        DTO<Boolean> dto = new DTO<>(200, "查询成功");
        dto.setT(result);
        return dto;
    }

    @Operation(summary = "获取收藏列表", description = "获取用户收藏的投票ID列表")
    @GetMapping("/favorites")
    public DTO<List<Long>> getFavorites(jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        List<Long> voteIds = voteFavoriteService.getFavoriteVoteIds(userId);
        DTO<List<Long>> dto = new DTO<>(200, "查询成功");
        dto.setT(voteIds);
        return dto;
    }

    // ==================== 评论功能 ====================

    @Operation(summary = "添加评论", description = "对投票发表评论")
    @PostMapping("/comment")
    public DTO<VoteComment> addComment(
            @Parameter(description = "投票ID", required = true)
            @RequestParam Long voteId,
            @Parameter(description = "评论内容", required = true)
            @RequestParam String content,
            @Parameter(description = "父评论ID（回复时使用）")
            @RequestParam(required = false) Long parentId,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        Long userId = (Long) request.getAttribute("currentUserId");
        VoteComment comment = voteCommentService.addComment(voteId, userId, content, parentId);
        if (comment == null) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "评论失败");
        }
        DTO<VoteComment> dto = new DTO<>(200, "评论成功");
        dto.setT(comment);
        return dto;
    }

    @Operation(summary = "删除评论", description = "删除自己的评论")
    @DeleteMapping("/comment/{id}")
    public DTO<Void> deleteComment(
            @Parameter(description = "评论ID", required = true)
            @PathVariable Long id,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        Long userId = (Long) request.getAttribute("currentUserId");
        boolean result = voteCommentService.deleteComment(id, userId);
        if (!result) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "删除评论失败，只能删除自己的评论");
        }
        return new DTO<>(200, "删除成功");
    }

    @Operation(summary = "获取投票评论", description = "获取指定投票的所有评论")
    @GetMapping("/comments/{voteId}")
    public DTO<List<VoteComment>> getComments(
            @Parameter(description = "投票ID", required = true)
            @PathVariable Long voteId) {
        List<VoteComment> comments = voteCommentService.getCommentsByVoteId(voteId);
        DTO<List<VoteComment>> dto = new DTO<>(200, "查询成功");
        dto.setT(comments);
        return dto;
    }

    // ==================== 通知功能 ====================

    @Operation(summary = "获取通知列表", description = "获取当前用户的所有通知")
    @GetMapping("/notifications")
    public DTO<List<VoteNotification>> getNotifications(jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        List<VoteNotification> notifications = voteNotificationService.getNotifications(userId);
        DTO<List<VoteNotification>> dto = new DTO<>(200, "查询成功");
        dto.setT(notifications);
        return dto;
    }

    @Operation(summary = "未读通知数", description = "获取当前用户未读通知数量")
    @GetMapping("/notifications/unread-count")
    public DTO<Integer> getUnreadCount(jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        int count = voteNotificationService.countUnread(userId);
        DTO<Integer> dto = new DTO<>(200, "查询成功");
        dto.setT(count);
        return dto;
    }

    @Operation(summary = "标记通知已读", description = "标记指定通知为已读")
    @PutMapping("/notifications/{id}/read")
    public DTO<Void> markAsRead(
            @Parameter(description = "通知ID", required = true)
            @PathVariable Long id) throws BusinessException {
        // 先检查通知是否存在
        org.mjc.entity.VoteNotification notification = voteNotificationService.getById(id);
        if (notification == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "通知不存在");
        }
        boolean result = voteNotificationService.markAsRead(id);
        if (!result) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "标记失败");
        }
        return new DTO<>(200, "标记成功");
    }

    @Operation(summary = "全部标记已读", description = "标记当前用户所有通知为已读")
    @PutMapping("/notifications/read-all")
    public DTO<Void> markAllAsRead(jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        voteNotificationService.markAllAsRead(userId);
        return new DTO<>(200, "全部标记成功");
    }
}
