package org.mjc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.mjc.entity.VoteGroup;
import org.mjc.mapper.VoteGroupMapper;
import org.mjc.service.VoteGroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 投票分组服务实现类
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class VoteGroupServiceImpl extends ServiceImpl<VoteGroupMapper, VoteGroup> implements VoteGroupService {

    @Override
    public List<VoteGroup> getAllGroups() {
        LambdaQueryWrapper<VoteGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(VoteGroup::getSortOrder);
        return this.list(wrapper);
    }

    @Override
    public VoteGroup createGroup(String name, String description, Long creatorId) {
        VoteGroup group = new VoteGroup();
        group.setName(name);
        group.setDescription(description);
        group.setCreatorId(creatorId);
        group.setSortOrder(0);
        group.setCreateTime(LocalDateTime.now());

        boolean result = this.save(group);
        if (result) {
            log.info("创建分组成功: name={}, creatorId={}", name, creatorId);
            return group;
        }
        return null;
    }

    @Override
    public VoteGroup updateGroup(Long id, String name, String description) {
        VoteGroup group = this.getById(id);
        if (group == null) {
            log.warn("更新分组失败：分组不存在 - id={}", id);
            return null;
        }

        group.setName(name);
        group.setDescription(description);

        boolean result = this.updateById(group);
        if (result) {
            log.info("更新分组成功: id={}", id);
            return group;
        }
        return null;
    }

    @Override
    public boolean deleteGroup(Long id) {
        boolean result = this.removeById(id);
        if (result) {
            log.info("删除分组成功: id={}", id);
        }
        return result;
    }
}
