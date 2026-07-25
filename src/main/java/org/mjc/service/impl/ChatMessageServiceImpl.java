package org.mjc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.mjc.entity.ChatMessage;
import org.mjc.mapper.ChatMessageMapper;
import org.mjc.service.ChatMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天消息服务实现类
 *
 * @author Online_Voting
 * @since 2026-07-25
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {

    @Override
    public ChatMessage sendMessage(Long fromUserId, Long toUserId, String content) {
        if (fromUserId == null || toUserId == null || content == null || content.isBlank()) {
            log.warn("发送消息失败：参数不完整");
            return null;
        }
        ChatMessage message = new ChatMessage();
        message.setFromUserId(fromUserId);
        message.setToUserId(toUserId);
        message.setContent(content);
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        boolean result = this.save(message);
        if (result) {
            log.info("消息发送成功: from={}, to={}, contentLength={}", fromUserId, toUserId, content.length());
            return message;
        }
        return null;
    }

    @Override
    public List<ChatMessage> getHistoryMessages(Long userId, Long friendId, int pageNum, int pageSize) {
        Page<ChatMessage> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        // 查询两人之间的所有消息（双向）
        wrapper.and(w -> w
                .and(w1 -> w1.eq(ChatMessage::getFromUserId, userId).eq(ChatMessage::getToUserId, friendId))
                .or(w2 -> w2.eq(ChatMessage::getFromUserId, friendId).eq(ChatMessage::getToUserId, userId))
        );
        wrapper.orderByDesc(ChatMessage::getCreateTime);
        return this.page(page, wrapper).getRecords();
    }

    @Override
    public long getUnreadCount(Long userId) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getToUserId, userId);
        wrapper.eq(ChatMessage::getIsRead, 0);
        return this.count(wrapper);
    }

    @Override
    public void markAsRead(Long userId, Long friendId) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getFromUserId, friendId);
        wrapper.eq(ChatMessage::getToUserId, userId);
        wrapper.eq(ChatMessage::getIsRead, 0);

        ChatMessage update = new ChatMessage();
        update.setIsRead(1);
        this.update(update, wrapper);
        log.debug("消息已读标记: userId={}, friendId={}", userId, friendId);
    }

    @Override
    public long getUnreadCountWithFriend(Long userId, Long friendId) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getFromUserId, friendId);
        wrapper.eq(ChatMessage::getToUserId, userId);
        wrapper.eq(ChatMessage::getIsRead, 0);
        return this.count(wrapper);
    }
}
