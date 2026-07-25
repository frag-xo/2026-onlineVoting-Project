package org.mjc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.mjc.entity.UserFriend;

/**
 * 好友关系 Mapper 接口
 *
 * @author Online_Voting
 * @since 2026-07-25
 */
@Mapper
public interface UserFriendMapper extends BaseMapper<UserFriend> {
}
