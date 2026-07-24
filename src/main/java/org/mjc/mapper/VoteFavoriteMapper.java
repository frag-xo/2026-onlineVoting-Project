package org.mjc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.mjc.entity.VoteFavorite;

/**
 * 投票收藏 Mapper 接口
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Mapper
public interface VoteFavoriteMapper extends BaseMapper<VoteFavorite> {

    /**
     * 检查用户是否已收藏
     */
    @Select("SELECT COUNT(*) FROM vote_favorite WHERE vote_id = #{voteId} AND user_id = #{userId}")
    int countByVoteIdAndUserId(@Param("voteId") Long voteId, @Param("userId") Long userId);
}
