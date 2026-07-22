package org.mjc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.mjc.entity.VoteOption;
import org.mjc.entity.VoteRecord;
import org.mjc.mapper.VoteOptionMapper;
import org.mjc.service.VoteOptionService;
import org.mjc.service.VoteRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 投票选项服务实现类
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class VoteOptionServiceImpl extends ServiceImpl<VoteOptionMapper, VoteOption> implements VoteOptionService {

    @Resource
    private VoteRecordService voteRecordService;

    @Override
    public List<VoteOption> getOptionsByVoteId(Long voteId) {
        if (voteId == null) {
            return new ArrayList<>();
        }
        return baseMapper.selectByVoteId(voteId);
    }

    @Override
    public boolean deleteByVoteId(Long voteId) {
        if (voteId == null) {
            log.warn("删除投票选项失败：投票ID为空");
            return false;
        }

        int result = baseMapper.deleteByVoteId(voteId);
        log.info("删除投票选项成功: voteId={}, 删除{}条", voteId, result);
        return result > 0;
    }

    @Override
    public boolean addOptionsBatch(List<VoteOption> optionList) {
        if (optionList == null || optionList.isEmpty()) {
            log.warn("批量新增选项失败：选项列表为空");
            return false;
        }

        // 设置创建时间
        LocalDateTime now = LocalDateTime.now();
        for (VoteOption option : optionList) {
            option.setCreateTime(now);
        }

        boolean result = this.saveBatch(optionList);
        if (result) {
            log.info("批量新增选项成功: {} 条", optionList.size());
        } else {
            log.warn("批量新增选项失败");
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getOptionsWithCountByVoteId(Long voteId) {
        if (voteId == null) {
            return new ArrayList<>();
        }

        // 查询所有选项
        List<VoteOption> options = baseMapper.selectByVoteId(voteId);

        // 查询每个选项的票数
        Map<Long, Long> countMap = voteRecordService.countByOptionId(voteId);

        // 组装返回数据
        List<Map<String, Object>> result = new ArrayList<>();
        for (VoteOption option : options) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", option.getId());
            map.put("optionText", option.getOptionText());
            map.put("sortOrder", option.getSortOrder());
            map.put("count", countMap.getOrDefault(option.getId(), 0L));
            result.add(map);
        }

        return result;
    }

    @Override
    public boolean updateOption(VoteOption option) {
        if (option == null || option.getId() == null) {
            log.warn("修改选项失败：选项对象为空或ID为空");
            return false;
        }

        boolean result = this.updateById(option);
        if (result) {
            log.info("修改选项成功: ID: {}", option.getId());
        } else {
            log.warn("修改选项失败: ID: {}", option.getId());
        }
        return result;
    }

    @Override
    public boolean deleteOptionById(Long id) {
        if (id == null) {
            log.warn("删除选项失败：选项ID为空");
            return false;
        }

        boolean result = this.removeById(id);
        if (result) {
            log.info("删除选项成功: ID: {}", id);
        } else {
            log.warn("删除选项失败: ID: {}", id);
        }
        return result;
    }

    @Override
    public VoteOption getOptionById(Long id) {
        if (id == null) {
            return null;
        }
        return this.getById(id);
    }
}
