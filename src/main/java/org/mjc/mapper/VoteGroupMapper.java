package org.mjc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.mjc.entity.VoteGroup;

/**
 * 投票分组 Mapper 接口
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Mapper
public interface VoteGroupMapper extends BaseMapper<VoteGroup> {
}
