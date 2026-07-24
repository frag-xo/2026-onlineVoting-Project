package org.mjc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.mjc.entity.VoteNotification;
import org.mjc.mapper.VoteNotificationMapper;
import org.mjc.service.VoteNotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知服务实现类
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class VoteNotificationServiceImpl extends ServiceImpl<VoteNotificationMapper, VoteNotification> implements VoteNotificationService {

    @Override
    public boolean sendNotification(Long userId, String type, String title, String content, Long voteId) {
        if (userId == null || type == null || title == null) {
            log.warn("发送通知失败：参数不完整");
            return false;
        }

        VoteNotification notification = new VoteNotification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setVoteId(voteId);
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());

        boolean result = this.save(notification);
        if (result) {
            log.info("发送通知成功: userId={}, type={}, title={}", userId, type, title);
        }
        return result;
    }

    @Override
    public List<VoteNotification> getNotifications(Long userId) {
        if (userId == null) {
            return new java.util.ArrayList<>();
        }
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public int countUnread(Long userId) {
        if (userId == null) {
            return 0;
        }
        return baseMapper.countUnreadByUserId(userId);
    }

    @Override
    public boolean markAsRead(Long notificationId) {
        if (notificationId == null) {
            return false;
        }

        VoteNotification notification = this.getById(notificationId);
        if (notification == null) {
            return false;
        }

        notification.setIsRead(1);
        boolean result = this.updateById(notification);
        if (result) {
            log.info("标记通知已读: id={}", notificationId);
        }
        return result;
    }

    @Override
    public boolean markAllAsRead(Long userId) {
        if (userId == null) {
            return false;
        }

        int count = baseMapper.markAllAsRead(userId);
        log.info("标记用户所有通知已读: userId={}, count={}", userId, count);
        return true;
    }
}
