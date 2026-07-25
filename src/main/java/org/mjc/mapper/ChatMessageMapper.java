package org.mjc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.mjc.entity.ChatMessage;

/**
 * 聊天消息 Mapper 接口
 *
 * @author Online_Voting
 * @since 2026-07-25
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}
