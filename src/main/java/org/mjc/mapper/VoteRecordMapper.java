package org.mjc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.mjc.entity.VoteRecord;

import java.util.List;

/**
 * 投票记录 Mapper 接口
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Mapper
public interface VoteRecordMapper extends BaseMapper<VoteRecord> {

    /**
     * 查询用户是否已投票（必须在@Results之前，避免被影响）
     */
    @Select("SELECT COUNT(*) FROM vote_record WHERE vote_id = #{voteId} AND user_id = #{userId}")
    int countByVoteIdAndUserId(@Param("voteId") Long voteId, @Param("userId") Long userId);

    /**
     * 定义结果映射
     */
    @Results(id = "voteRecordResultMap", value = {
            @Result(column = "id", property = "id", id = true),
            @Result(column = "vote_id", property = "voteId"),
            @Result(column = "option_id", property = "optionId"),
            @Result(column = "user_id", property = "userId"),
            @Result(column = "create_time", property = "createTime")
    })
    @Select("SELECT * FROM vote_record WHERE id = #{id}")
    VoteRecord selectById(@Param("id") Long id);

    /**
     * 根据投票ID查询所有投票记录
     */
    @ResultMap("voteRecordResultMap")
    @Select("SELECT * FROM vote_record WHERE vote_id = #{voteId}")
    List<VoteRecord> selectByVoteId(@Param("voteId") Long voteId);

    /**
     * 统计每个选项的投票数
     */
    @Select("SELECT option_id, COUNT(*) as count FROM vote_record WHERE vote_id = #{voteId} GROUP BY option_id")
    List<Object> countByOptionId(@Param("voteId") Long voteId);
}
