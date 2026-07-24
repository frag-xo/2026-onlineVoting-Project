package org.mjc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.mjc.entity.VoteAudit;

/**
 * 审核记录 Mapper 接口
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Mapper
public interface VoteAuditMapper extends BaseMapper<VoteAudit> {

    /**
     * 根据投票ID查询审核记录
     */
    @Select("SELECT * FROM vote_audit WHERE vote_id = #{voteId}")
    VoteAudit selectByVoteId(@Param("voteId") Long voteId);
}
