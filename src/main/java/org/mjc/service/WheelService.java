package org.mjc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mjc.entity.WheelPrize;
import org.mjc.entity.WheelRecord;

import java.util.List;
import java.util.Map;

/**
 * 转盘服务接口
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
public interface WheelService extends IService<WheelPrize> {

    /**
     * 获取所有启用的奖品
     */
    List<WheelPrize> getActivePrizes();

    /**
     * 用户抽奖
     *
     * @param userId 用户ID
     * @param voteId 投票ID
     * @return 中奖信息
     */
    Map<String, Object> draw(Long userId, Long voteId);

    /**
     * 检查用户是否已对该投票抽奖
     */
    boolean hasDrawn(Long userId, Long voteId);
}
