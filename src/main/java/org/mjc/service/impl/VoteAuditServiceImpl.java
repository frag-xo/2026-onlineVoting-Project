package org.mjc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.mjc.entity.Vote;
import org.mjc.entity.VoteAudit;
import org.mjc.mapper.VoteAuditMapper;
import org.mjc.service.VoteAuditService;
import org.mjc.service.VoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 审核服务实现类
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class VoteAuditServiceImpl extends ServiceImpl<VoteAuditMapper, VoteAudit> implements VoteAuditService {

    @Resource
    private VoteService voteService;

    @Override
    public VoteAudit createAudit(Long voteId) {
        VoteAudit audit = new VoteAudit();
        audit.setVoteId(voteId);
        audit.setStatus(0); // 待审核
        audit.setCreateTime(LocalDateTime.now());
        audit.setUpdateTime(LocalDateTime.now());

        boolean result = this.save(audit);
        if (result) {
            log.info("创建审核记录成功: voteId={}", voteId);
            return audit;
        }
        return null;
    }

    @Override
    public boolean audit(Long voteId, Long auditorId, Integer status, String remark) {
        VoteAudit audit = getByVoteId(voteId);
        if (audit == null) {
            log.warn("审核失败：审核记录不存在 - voteId={}", voteId);
            return false;
        }

        // 更新审核记录
        audit.setAuditorId(auditorId);
        audit.setStatus(status);
        audit.setRemark(remark);
        audit.setUpdateTime(LocalDateTime.now());
        this.updateById(audit);

        // 更新投票状态
        Vote vote = voteService.getVoteById(voteId);
        if (vote != null) {
            vote.setAuditStatus(status);
            vote.setAuditMsg(remark);
            if (status == 1) {
                vote.setStatus(1); // 审核通过，设为进行中
            }
            vote.setUpdateTime(LocalDateTime.now());
            voteService.updateById(vote);
        }

        log.info("审核完成: voteId={}, status={}, auditorId={}", voteId, status, auditorId);
        return true;
    }

    @Override
    public List<VoteAudit> getPendingAudits() {
        LambdaQueryWrapper<VoteAudit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VoteAudit::getStatus, 0);
        wrapper.orderByDesc(VoteAudit::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public VoteAudit getByVoteId(Long voteId) {
        return baseMapper.selectByVoteId(voteId);
    }
}
