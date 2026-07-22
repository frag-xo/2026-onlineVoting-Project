package org.mjc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.mjc.entity.Vote;

import java.util.List;

/**
 * 投票 Mapper 接口
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Mapper
public interface VoteMapper extends BaseMapper<Vote> {

    /**
     * 定义结果映射
     */
    @Results(id = "voteResultMap", value = {
            @Result(column = "id", property = "id", id = true),
            @Result(column = "title", property = "title"),
            @Result(column = "description", property = "description"),
            @Result(column = "status", property = "status"),
            @Result(column = "start_time", property = "startTime"),
            @Result(column = "end_time", property = "endTime"),
            @Result(column = "creator_id", property = "creatorId"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "update_time", property = "updateTime"),
            @Result(column = "deleted", property = "deleted")
    })

    /**
     * 根据ID查询投票
     */
    @Select("SELECT * FROM vote WHERE id = #{id} AND deleted = 0")
    Vote selectVoteById(@Param("id") Long id);

    /**
     * 根据状态查询投票列表
     */
    @ResultMap("voteResultMap")
    @Select("SELECT * FROM vote WHERE status = #{status} AND deleted = 0 ORDER BY create_time DESC")
    List<Vote> selectByStatus(@Param("status") Integer status);

    /**
     * 根据创建者查询投票列表
     */
    @ResultMap("voteResultMap")
    @Select("SELECT * FROM vote WHERE creator_id = #{creatorId} AND deleted = 0 ORDER BY create_time DESC")
    List<Vote> selectByCreatorId(@Param("creatorId") Long creatorId);

    /**
     * 根据标题模糊查询
     */
    @ResultMap("voteResultMap")
    @Select("SELECT * FROM vote WHERE title LIKE CONCAT('%', #{title}, '%') AND deleted = 0 ORDER BY create_time DESC")
    List<Vote> selectByTitle(@Param("title") String title);
}
