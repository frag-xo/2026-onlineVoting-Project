package org.mjc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.mjc.entity.UserPoints;

/**
 * 用户积分 Mapper 接口
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Mapper
public interface UserPointsMapper extends BaseMapper<UserPoints> {

    /**
     * 根据用户ID查询积分
     */
    @Select("SELECT * FROM user_points WHERE user_id = #{userId}")
    UserPoints selectByUserId(@Param("userId") Long userId);
}
