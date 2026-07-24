package org.mjc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.mjc.entity.VoteComment;
import org.mjc.mapper.VoteCommentMapper;
import org.mjc.service.VoteCommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 投票评论服务实现类
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class VoteCommentServiceImpl extends ServiceImpl<VoteCommentMapper, VoteComment> implements VoteCommentService {

    @Override
    public VoteComment addComment(Long voteId, Long userId, String content, Long parentId) {
        if (voteId == null || userId == null || content == null || content.trim().isEmpty()) {
            log.warn("添加评论失败：参数不完整");
            return null;
        }

        VoteComment comment = new VoteComment();
        comment.setVoteId(voteId);
        comment.setUserId(userId);
        comment.setContent(content.trim());
        comment.setParentId(parentId);
        comment.setCreateTime(LocalDateTime.now());

        boolean result = this.save(comment);
        if (result) {
            log.info("添加评论成功: voteId={}, userId={}", voteId, userId);
            return comment;
        }
        return null;
    }

    @Override
    public boolean deleteComment(Long commentId, Long userId) {
        if (commentId == null || userId == null) {
            return false;
        }

        VoteComment comment = this.getById(commentId);
        if (comment == null) {
            log.warn("删除评论失败：评论不存在 - id={}", commentId);
            return false;
        }

        // 只能删自己的评论
        if (!comment.getUserId().equals(userId)) {
            log.warn("删除评论失败：无权删除 - commentId={}, userId={}", commentId, userId);
            return false;
        }

        boolean result = this.removeById(commentId);
        if (result) {
            log.info("删除评论成功: id={}", commentId);
        }
        return result;
    }

    @Override
    public List<VoteComment> getCommentsByVoteId(Long voteId) {
        if (voteId == null) {
            return new java.util.ArrayList<>();
        }
        return baseMapper.selectByVoteId(voteId);
    }

    @Override
    public int countByVoteId(Long voteId) {
        if (voteId == null) {
            return 0;
        }
        return baseMapper.countByVoteId(voteId);
    }
}
