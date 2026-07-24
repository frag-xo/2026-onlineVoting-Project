package org.mjc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mjc.entity.VoteFavorite;

import java.util.List;

/**
 * 投票收藏服务接口
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
public interface VoteFavoriteService extends IService<VoteFavorite> {

    /**
     * 收藏投票
     */
    boolean favorite(Long voteId, Long userId);

    /**
     * 取消收藏
     */
    boolean unfavorite(Long voteId, Long userId);

    /**
     * 检查是否已收藏
     */
    boolean isFavorited(Long voteId, Long userId);

    /**
     * 获取用户收藏的投票ID列表
     */
    List<Long> getFavoriteVoteIds(Long userId);
}
