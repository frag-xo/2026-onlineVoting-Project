package org.mjc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.mjc.entity.VoteComment;

import java.util.List;

/**
 * 投票评论 Mapper 接口
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Mapper
public interface VoteCommentMapper extends BaseMapper<VoteComment> {

    /**
     * 定义结果映射
     */
    @Results(id = "voteCommentResultMap", value = {
            @Result(column = "id", property = "id", id = true),
            @Result(column = "vote_id", property = "voteId"),
            @Result(column = "user_id", property = "userId"),
            @Result(column = "content", property = "content"),
            @Result(column = "parent_id", property = "parentId"),
            @Result(column = "create_time", property = "createTime")
    })

    /**
     * 根据投票ID查询所有评论
     */
    @Select("SELECT * FROM vote_comment WHERE vote_id = #{voteId} AND deleted = 0 ORDER BY create_time DESC")
    List<VoteComment> selectByVoteId(@Param("voteId") Long voteId);

    /**
     * 统计投票的评论数
     */
    @Select("SELECT COUNT(*) FROM vote_comment WHERE vote_id = #{voteId} AND deleted = 0")
    int countByVoteId(@Param("voteId") Long voteId);
}
