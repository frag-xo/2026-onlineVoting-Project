package org.mjc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.mjc.entity.VoteRecord;
import org.mjc.mapper.VoteRecordMapper;
import org.mjc.service.VoteRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 投票记录服务实现类
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class VoteRecordServiceImpl extends ServiceImpl<VoteRecordMapper, VoteRecord> implements VoteRecordService {

    @Override
    public boolean hasVoted(Long voteId, Long userId) {
        if (voteId == null || userId == null) {
            return false;
        }
        int count = baseMapper.countByVoteIdAndUserId(voteId, userId);
        return count > 0;
    }

    @Override
    public boolean vote(Long voteId, Long optionId, Long userId) {
        if (voteId == null || optionId == null || userId == null) {
            log.warn("投票失败：参数不完整");
            return false;
        }

        // 检查是否已投票
        if (hasVoted(voteId, userId)) {
            log.warn("投票失败：用户已投票 - voteId={}, userId={}", voteId, userId);
            return false;
        }

        // 创建投票记录
        VoteRecord record = new VoteRecord();
        record.setVoteId(voteId);
        record.setOptionId(optionId);
        record.setUserId(userId);
        record.setCreateTime(LocalDateTime.now());

        boolean result = this.save(record);
        if (result) {
            log.info("投票成功: voteId={}, optionId={}, userId={}", voteId, optionId, userId);
        } else {
            log.warn("投票失败: voteId={}, optionId={}, userId={}", voteId, optionId, userId);
        }
        return result;
    }

    @Override
    public List<VoteRecord> getRecordsByVoteId(Long voteId) {
        if (voteId == null) {
            return new java.util.ArrayList<>();
        }
        return baseMapper.selectByVoteId(voteId);
    }

    @Override
    public List<VoteRecord> getRecordsByUserId(Long userId) {
        if (userId == null) {
            return new java.util.ArrayList<>();
        }
        LambdaQueryWrapper<VoteRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VoteRecord::getUserId, userId);
        wrapper.orderByDesc(VoteRecord::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public Map<Long, Long> countByOptionId(Long voteId) {
        if (voteId == null) {
            return new HashMap<>();
        }

        // 使用SQL GROUP BY统计每个选项的票数（避免内存遍历）
        QueryWrapper<VoteRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("vote_id", voteId);
        wrapper.select("option_id", "COUNT(*) as count");
        wrapper.groupBy("option_id");

        List<Map<String, Object>> results = baseMapper.selectMaps(wrapper);
        return results.stream().collect(Collectors.toMap(
                row -> (Long) row.get("option_id"),
                row -> (Long) row.get("count")
        ));
    }

    @Override
    public boolean deleteByVoteId(Long voteId) {
        if (voteId == null) {
            log.warn("删除投票记录失败：投票ID为空");
            return false;
        }

        LambdaQueryWrapper<VoteRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VoteRecord::getVoteId, voteId);
        boolean result = this.remove(wrapper);
        if (result) {
            log.info("删除投票记录成功: voteId={}", voteId);
        }
        return result;
    }

    @Override
    public boolean deleteRecordById(Long id) {
        if (id == null) {
            log.warn("删除投票记录失败：记录ID为空");
            return false;
        }

        boolean result = this.removeById(id);
        if (result) {
            log.info("删除投票记录成功: id={}", id);
        }
        return result;
    }
}
