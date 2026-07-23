package org.mjc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.mjc.entity.VoteOption;

import java.util.List;

/**
 * 投票选项 Mapper 接口
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Mapper
public interface VoteOptionMapper extends BaseMapper<VoteOption> {

    /**
     * 定义结果映射
     */
    @Results(id = "voteOptionResultMap", value = {
            @Result(column = "id", property = "id", id = true),
            @Result(column = "vote_id", property = "voteId"),
            @Result(column = "option_text", property = "optionText"),
            @Result(column = "sort_order", property = "sortOrder"),
            @Result(column = "create_time", property = "createTime")
    })

    /**
     * 根据投票ID查询所有选项（仅查询未删除的）
     */
    @Select("SELECT * FROM vote_option WHERE vote_id = #{voteId} AND deleted = 0 ORDER BY sort_order ASC")
    List<VoteOption> selectByVoteId(@Param("voteId") Long voteId);

    /**
     * 根据投票ID逻辑删除所有选项
     */
    @Update("UPDATE vote_option SET deleted = 1 WHERE vote_id = #{voteId}")
    int deleteByVoteId(@Param("voteId") Long voteId);
}
