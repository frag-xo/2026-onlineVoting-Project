package org.mjc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mjc.entity.VoteOption;

import java.util.List;

/**
 * 投票选项服务接口
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
public interface VoteOptionService extends IService<VoteOption> {

    /**
     * 根据投票ID查询所有选项
     *
     * @param voteId 投票ID
     * @return 选项列表
     */
    List<VoteOption> getOptionsByVoteId(Long voteId);

    /**
     * 根据投票ID删除所有选项
     *
     * @param voteId 投票ID
     * @return 是否成功
     */
    boolean deleteByVoteId(Long voteId);

    /**
     * 批量新增选项
     *
     * @param optionList 选项列表
     * @return 是否成功
     */
    boolean addOptionsBatch(List<VoteOption> optionList);

    /**
     * 根据投票ID查询选项（含票数）
     *
     * @param voteId 投票ID
     * @return 选项列表（含票数）
     */
    List<java.util.Map<String, Object>> getOptionsWithCountByVoteId(Long voteId);

    /**
     * 修改选项
     *
     * @param option 选项实体
     * @return 是否成功
     */
    boolean updateOption(VoteOption option);

    /**
     * 根据ID删除选项
     *
     * @param id 选项ID
     * @return 是否成功
     */
    boolean deleteOptionById(Long id);

    /**
     * 根据ID查询选项
     *
     * @param id 选项ID
     * @return 选项实体
     */
    VoteOption getOptionById(Long id);
}
