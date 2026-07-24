package org.mjc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mjc.dto.DTO;
import org.mjc.entity.Vote;
import org.mjc.entity.VoteLike;
import org.mjc.exception.BusinessException;
import org.mjc.exception.ErrorCode;
import org.mjc.mapper.VoteLikeMapper;
import org.mjc.service.VoteService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

@Tag(name = "推荐与点赞", description = "投票推荐和点赞相关接口")
@RestController
@RequestMapping("/api")
public class LikeController {

    @Resource
    private VoteService voteService;

    @Resource
    private VoteLikeMapper voteLikeMapper;

    @Operation(summary = "获取推荐投票", description = "获取管理员推荐的投票列表")
    @GetMapping("/vote/recommended")
    public DTO<java.util.List<Vote>> getRecommended() {
        LambdaQueryWrapper<Vote> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Vote::getIsRecommended, 1);
        wrapper.eq(Vote::getDeleted, 0);
        wrapper.orderByDesc(Vote::getCreateTime);
        java.util.List<Vote> list = voteService.list(wrapper);
        DTO<java.util.List<Vote>> dto = new DTO<>(200, "查询成功");
        dto.setT(list);
        return dto;
    }

    @Operation(summary = "推荐/取消推荐", description = "管理员推荐或取消推荐投票")
    @PutMapping("/vote/recommend/{id}")
    public DTO<Void> recommend(
            @PathVariable Long id) throws BusinessException {
        Vote vote = voteService.getVoteById(id);
        if (vote == null) throw new BusinessException(ErrorCode.VOTE_NOT_FOUND);

        vote.setIsRecommended(vote.getIsRecommended() == null || vote.getIsRecommended() == 0 ? 1 : 0);
        vote.setUpdateTime(LocalDateTime.now());
        voteService.updateById(vote);
        return new DTO<>(200, vote.getIsRecommended() == 1 ? "推荐成功" : "取消推荐");
    }

    @Operation(summary = "点赞/取消点赞", description = "对投票点赞或取消点赞")
    @PostMapping("/like")
    public DTO<Void> like(
            @RequestParam Long voteId,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        Long userId = (Long) request.getAttribute("currentUserId");

        LambdaQueryWrapper<VoteLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VoteLike::getVoteId, voteId);
        wrapper.eq(VoteLike::getUserId, userId);

        VoteLike existing = voteLikeMapper.selectOne(wrapper);
        if (existing != null) {
            voteLikeMapper.deleteById(existing.getId());
            return new DTO<>(200, "取消点赞");
        } else {
            VoteLike like = new VoteLike();
            like.setVoteId(voteId);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            voteLikeMapper.insert(like);
            return new DTO<>(200, "点赞成功");
        }
    }

    @Operation(summary = "获取点赞数", description = "获取投票的点赞数")
    @GetMapping("/like/count/{id}")
    public DTO<Long> getLikeCount(@PathVariable Long id) {
        LambdaQueryWrapper<VoteLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VoteLike::getVoteId, id);
        long count = voteLikeMapper.selectCount(wrapper);
        DTO<Long> dto = new DTO<>(200, "查询成功");
        dto.setT(count);
        return dto;
    }
}
