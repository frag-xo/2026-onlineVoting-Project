package org.mjc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mjc.entity.VoteRecord;

import java.util.List;
import java.util.Map;

/**
 * 投票记录服务接口
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
public interface VoteRecordService extends IService<VoteRecord> {

    /**
     * 检查用户是否已投票
     *
     * @param voteId 投票ID
     * @param userId 用户ID
     * @return 是否已投票
     */
    boolean hasVoted(Long voteId, Long userId);

    /**
     * 用户投票
     *
     * @param voteId 投票ID
     * @param optionId 选项ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean vote(Long voteId, Long optionId, Long userId);

    /**
     * 根据投票ID查询所有投票记录
     *
     * @param voteId 投票ID
     * @return 投票记录列表
     */
    List<VoteRecord> getRecordsByVoteId(Long voteId);

    /**
     * 统计每个选项的投票数
     *
     * @param voteId 投票ID
     * @return 选项ID -> 票数 的映射
     */
    Map<Long, Long> countByOptionId(Long voteId);

    /**
     * 根据投票ID删除所有投票记录
     *
     * @param voteId 投票ID
     * @return 是否成功
     */
    boolean deleteByVoteId(Long voteId);

    /**
     * 根据ID删除投票记录
     *
     * @param id 记录ID
     * @return 是否成功
     */
    boolean deleteRecordById(Long id);
}
