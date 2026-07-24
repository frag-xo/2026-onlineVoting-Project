package org.mjc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mjc.entity.VoteComment;

import java.util.List;

/**
 * 投票评论服务接口
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
public interface VoteCommentService extends IService<VoteComment> {

    /**
     * 添加评论
     */
    VoteComment addComment(Long voteId, Long userId, String content, Long parentId);

    /**
     * 删除评论（只能删自己的）
     */
    boolean deleteComment(Long commentId, Long userId);

    /**
     * 获取投票的所有评论
     */
    List<VoteComment> getCommentsByVoteId(Long voteId);

    /**
     * 统计投票的评论数
     */
    int countByVoteId(Long voteId);
}
