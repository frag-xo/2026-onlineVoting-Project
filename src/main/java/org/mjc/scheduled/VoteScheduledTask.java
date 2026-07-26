package org.mjc.scheduled;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.mjc.entity.Vote;
import org.mjc.service.VoteService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 投票定时任务
 *
 * @author Online_Voting
 * @since 2026-07-25
 */
@Slf4j
@Component
public class VoteScheduledTask {

    @Resource
    private VoteService voteService;

    /**
     * 每分钟执行一次，检查定时发布的投票
     */
    @Scheduled(fixedDelay = 60000)
    public void publishScheduledVotes() {
        try {
            LocalDateTime now = LocalDateTime.now();

            LambdaQueryWrapper<Vote> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Vote::getStatus, 0);                    // 未开始
            wrapper.eq(Vote::getAuditStatus, 1);               // 已审核通过
            wrapper.le(Vote::getPublishTime, now);             // 发布时间已到
            wrapper.isNotNull(Vote::getPublishTime);           // 有定时发布时间

            List<Vote> votes = voteService.list(wrapper);

            if (votes.isEmpty()) {
                return;
            }

            for (Vote vote : votes) {
                vote.setStatus(1);                             // 变为进行中
                vote.setUpdateTime(LocalDateTime.now());
                voteService.updateById(vote);
                log.info("✅ 定时投票已发布: id={}, title={}", vote.getId(), vote.getTitle());
            }
        } catch (Exception e) {
            log.error("定时发布投票失败", e);
        }
    }
}