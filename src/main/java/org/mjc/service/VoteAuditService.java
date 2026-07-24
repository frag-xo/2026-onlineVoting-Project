package org.mjc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mjc.entity.VoteAudit;

import java.util.List;

/**
 * 审核服务接口
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
public interface VoteAuditService extends IService<VoteAudit> {

    /**
     * 创建审核记录
     */
    VoteAudit createAudit(Long voteId);

    /**
     * 审核投票
     */
    boolean audit(Long voteId, Long auditorId, Integer status, String remark);

    /**
     * 获取待审核列表
     */
    List<VoteAudit> getPendingAudits();

    /**
     * 根据投票ID查询审核记录
     */
    VoteAudit getByVoteId(Long voteId);
}
