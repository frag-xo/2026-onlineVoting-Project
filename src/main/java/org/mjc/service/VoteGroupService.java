package org.mjc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mjc.entity.VoteGroup;

import java.util.List;

/**
 * 投票分组服务接口
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
public interface VoteGroupService extends IService<VoteGroup> {

    /**
     * 获取所有分组
     */
    List<VoteGroup> getAllGroups();

    /**
     * 创建分组
     */
    VoteGroup createGroup(String name, String description, Long creatorId);

    /**
     * 更新分组
     */
    VoteGroup updateGroup(Long id, String name, String description);

    /**
     * 删除分组
     */
    boolean deleteGroup(Long id);
}
