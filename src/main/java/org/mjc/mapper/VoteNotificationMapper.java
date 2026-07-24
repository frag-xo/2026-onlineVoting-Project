package org.mjc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.mjc.entity.VoteNotification;

import java.util.List;

/**
 * 通知 Mapper 接口
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Mapper
public interface VoteNotificationMapper extends BaseMapper<VoteNotification> {

    /**
     * 统计用户未读通知数
     */
    @Select("SELECT COUNT(*) FROM vote_notification WHERE user_id = #{userId} AND is_read = 0")
    int countUnreadByUserId(@Param("userId") Long userId);

    /**
     * 查询用户的所有通知
     */
    @Select("SELECT * FROM vote_notification WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<VoteNotification> selectByUserId(@Param("userId") Long userId);

    /**
     * 标记用户所有通知为已读
     */
    @Update("UPDATE vote_notification SET is_read = 1 WHERE user_id = #{userId} AND is_read = 0")
    int markAllAsRead(@Param("userId") Long userId);
}
