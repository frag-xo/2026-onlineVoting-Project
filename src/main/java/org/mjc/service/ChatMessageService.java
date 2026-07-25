package org.mjc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mjc.entity.ChatMessage;

import java.util.List;

/**
 * 聊天消息服务接口
 *
 * @author Online_Voting
 * @since 2026-07-25
 */
public interface ChatMessageService extends IService<ChatMessage> {

    /**
     * 发送消息（WebSocket和HTTP通用）
     */
    ChatMessage sendMessage(Long fromUserId, Long toUserId, String content);

    /**
     * 获取与某个好友的历史消息（分页倒序）
     */
    List<ChatMessage> getHistoryMessages(Long userId, Long friendId, int pageNum, int pageSize);

    /**
     * 获取未读消息数
     */
    long getUnreadCount(Long userId);

    /**
     * 标记与某好友的消息为已读
     */
    void markAsRead(Long userId, Long friendId);

    /**
     * 获取与某好友的未读消息数
     */
    long getUnreadCountWithFriend(Long userId, Long friendId);
}
