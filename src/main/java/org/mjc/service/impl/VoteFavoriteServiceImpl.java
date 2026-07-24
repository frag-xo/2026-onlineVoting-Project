package org.mjc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.mjc.entity.VoteFavorite;
import org.mjc.mapper.VoteFavoriteMapper;
import org.mjc.service.VoteFavoriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 投票收藏服务实现类
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class VoteFavoriteServiceImpl extends ServiceImpl<VoteFavoriteMapper, VoteFavorite> implements VoteFavoriteService {

    @Override
    public boolean favorite(Long voteId, Long userId) {
        if (voteId == null || userId == null) {
            return false;
        }

        // 检查是否已收藏
        if (isFavorited(voteId, userId)) {
            log.warn("收藏失败：已收藏 - voteId={}, userId={}", voteId, userId);
            return false;
        }

        VoteFavorite favorite = new VoteFavorite();
        favorite.setVoteId(voteId);
        favorite.setUserId(userId);
        favorite.setCreateTime(LocalDateTime.now());

        boolean result = this.save(favorite);
        if (result) {
            log.info("收藏成功: voteId={}, userId={}", voteId, userId);
        }
        return result;
    }

    @Override
    public boolean unfavorite(Long voteId, Long userId) {
        if (voteId == null || userId == null) {
            return false;
        }

        LambdaQueryWrapper<VoteFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VoteFavorite::getVoteId, voteId);
        wrapper.eq(VoteFavorite::getUserId, userId);

        boolean result = this.remove(wrapper);
        if (result) {
            log.info("取消收藏成功: voteId={}, userId={}", voteId, userId);
        }
        return result;
    }

    @Override
    public boolean isFavorited(Long voteId, Long userId) {
        if (voteId == null || userId == null) {
            return false;
        }
        return baseMapper.countByVoteIdAndUserId(voteId, userId) > 0;
    }

    @Override
    public List<Long> getFavoriteVoteIds(Long userId) {
        if (userId == null) {
            return new java.util.ArrayList<>();
        }

        LambdaQueryWrapper<VoteFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VoteFavorite::getUserId, userId);
        wrapper.orderByDesc(VoteFavorite::getCreateTime);

        return this.list(wrapper).stream()
                .map(VoteFavorite::getVoteId)
                .collect(Collectors.toList());
    }
}
