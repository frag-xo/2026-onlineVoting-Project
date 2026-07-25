package org.mjc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.mjc.entity.WheelPrize;
import org.mjc.entity.WheelRecord;
import org.mjc.mapper.WheelPrizeMapper;
import org.mjc.mapper.WheelRecordMapper;
import org.mjc.service.UserPointsService;
import org.mjc.service.WheelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 转盘服务实现类
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class WheelServiceImpl extends ServiceImpl<WheelPrizeMapper, WheelPrize> implements WheelService {

    @Resource
    private WheelRecordMapper wheelRecordMapper;

    @Resource
    private UserPointsService userPointsService;

    private final java.security.SecureRandom random = new java.security.SecureRandom();

    @Override
    public List<WheelPrize> getActivePrizes() {
        LambdaQueryWrapper<WheelPrize> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WheelPrize::getIsActive, true);
        wrapper.orderByAsc(WheelPrize::getId);
        return this.list(wrapper);
    }

    @Override
    public Map<String, Object> draw(Long userId, Long voteId) {
        // ===== 检查是否已抽奖 =====
        if (hasDrawn(userId, voteId)) {
            log.warn("用户已抽奖: userId={}, voteId={}", userId, voteId);
            // ✅ 不再返回 null，而是返回一个表示已抽奖的结果
            Map<String, Object> result = new HashMap<>();
            result.put("alreadyDrawn", true);
            result.put("points", 0);
            result.put("prizeName", "已抽过奖");
            result.put("message", "您已抽过奖");
            return result;
        }

        // 获取所有奖品
        List<WheelPrize> prizes = getActivePrizes();
        if (prizes.isEmpty()) {
            log.warn("没有可用的奖品");
            // ✅ 返回默认结果，不返回 null
            Map<String, Object> result = new HashMap<>();
            result.put("alreadyDrawn", false);
            result.put("points", 0);
            result.put("prizeName", "暂无奖品");
            result.put("message", "暂无可用奖品");
            return result;
        }

        // 根据概率抽奖
        WheelPrize selectedPrize = drawPrize(prizes);

        // 记录抽奖记录
        WheelRecord record = new WheelRecord();
        record.setUserId(userId);
        record.setVoteId(voteId);
        record.setPrizeId(selectedPrize.getId());
        record.setPoints(selectedPrize.getPoints());
        record.setCreateTime(LocalDateTime.now());
        wheelRecordMapper.insert(record);

        // 如果有积分奖励，添加积分
        if (selectedPrize.getPoints() > 0) {
            userPointsService.addPoints(userId, selectedPrize.getPoints(), "wheel", "转盘抽奖获得");
        }

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("alreadyDrawn", false);
        result.put("prizeId", selectedPrize.getId());
        result.put("prizeName", selectedPrize.getName());
        result.put("points", selectedPrize.getPoints());
        result.put("icon", selectedPrize.getIcon());

        log.info("用户抽奖成功: userId={}, prize={}, points={}", userId, selectedPrize.getName(), selectedPrize.getPoints());
        return result;
    }

    @Override
    public boolean hasDrawn(Long userId, Long voteId) {
        LambdaQueryWrapper<WheelRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WheelRecord::getUserId, userId);
        wrapper.eq(WheelRecord::getVoteId, voteId);
        return wheelRecordMapper.selectCount(wrapper) > 0;
    }

    /**
     * 根据概率抽奖
     */
    private WheelPrize drawPrize(List<WheelPrize> prizes) {
        // 计算总概率
        BigDecimal totalProbability = prizes.stream()
                .map(WheelPrize::getProbability)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 生成随机数
        double randomValue = random.nextDouble() * totalProbability.doubleValue();

        // 根据概率选择奖品
        double cumulative = 0;
        for (WheelPrize prize : prizes) {
            cumulative += prize.getProbability().doubleValue();
            if (randomValue <= cumulative) {
                return prize;
            }
        }

        // 默认返回最后一个
        return prizes.get(prizes.size() - 1);
    }
}
