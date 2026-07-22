package org.mjc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.mjc.dto.vote.VoteQueryDTO;
import org.mjc.dto.vote.VoteResponseDTO;
import org.mjc.dto.vote.VoteSaveDTO;
import org.mjc.entity.Vote;

import java.util.List;

/**
 * 投票服务接口
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
public interface VoteService extends IService<Vote> {

    // ==================== 分页查询 ====================

    /**
     * 分页查询投票（返回DTO）
     *
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    Page<VoteResponseDTO> queryPageDTO(VoteQueryDTO queryDTO);

    /**
     * 简单分页查询（返回DTO）
     *
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    Page<VoteResponseDTO> queryPageSimpleDTO(Integer pageNum, Integer pageSize);

    // ==================== 按主键查询 ====================

    /**
     * 根据ID查询投票
     *
     * @param id 投票ID
     * @return 投票实体
     */
    Vote getVoteById(Long id);

    /**
     * 根据ID查询投票（返回DTO）
     *
     * @param id 投票ID
     * @return 投票响应DTO
     */
    VoteResponseDTO getVoteDTOById(Long id);

    // ==================== 新增投票 ====================

    /**
     * 新增投票（使用DTO）
     *
     * @param saveDTO 保存参数
     * @return 保存后的投票
     */
    Vote addVote(VoteSaveDTO saveDTO);

    // ==================== 修改投票 ====================

    /**
     * 修改投票（使用DTO）
     *
     * @param saveDTO 保存参数
     * @return 修改后的投票
     */
    Vote updateVote(VoteSaveDTO saveDTO);

    // ==================== 删除投票 ====================

    /**
     * 根据ID删除投票（逻辑删除）
     *
     * @param id 投票ID
     * @return 是否成功
     */
    boolean deleteVoteById(Long id);

    /**
     * 批量删除投票（逻辑删除）
     *
     * @param ids 投票ID列表
     * @return 是否成功
     */
    boolean deleteVoteBatch(List<Long> ids);

    // ==================== 其他方法 ====================

    /**
     * 查询所有投票
     *
     * @return 投票列表
     */
    List<Vote> getAllVotes();

    /**
     * 检查投票是否存在
     *
     * @param id 投票ID
     * @return 是否存在
     */
    boolean exists(Long id);

    /**
     * 结束投票
     *
     * @param id 投票ID
     * @return 是否成功
     */
    boolean endVote(Long id);

    /**
     * 生成随机投票数据
     *
     * @param count 生成数量
     * @return 生成的投票列表
     */
    List<Vote> generateRandomVotes(int count);
}
