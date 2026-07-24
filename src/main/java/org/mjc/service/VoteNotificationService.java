package org.mjc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mjc.entity.VoteNotification;

import java.util.List;

/**
 * 通知服务接口
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
public interface VoteNotificationService extends IService<VoteNotification> {

    /**
     * 发送通知
     */
    boolean sendNotification(Long userId, String type, String title, String content, Long voteId);

    /**
     * 获取用户的所有通知
     */
    List<VoteNotification> getNotifications(Long userId);

    /**
     * 统计用户未读通知数
     */
    int countUnread(Long userId);

    /**
     * 标记通知为已读
     */
    boolean markAsRead(Long notificationId);

    /**
     * 标记用户所有通知为已读
     */
    boolean markAllAsRead(Long userId);
}
