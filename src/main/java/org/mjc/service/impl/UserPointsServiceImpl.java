package org.mjc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.mjc.entity.PointsLog;
import org.mjc.entity.UserPoints;
import org.mjc.mapper.PointsLogMapper;
import org.mjc.mapper.UserPointsMapper;
import org.mjc.service.UserPointsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 用户积分服务实现类
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserPointsServiceImpl extends ServiceImpl<UserPointsMapper, UserPoints> implements UserPointsService {

    @Resource
    private PointsLogMapper pointsLogMapper;

    @Override
    public UserPoints getByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public boolean addPoints(Long userId, int points, String type, String description) {
        if (userId == null || points <= 0) {
            return false;
        }

        // 获取或创建用户积分
        UserPoints userPoints = getByUserId(userId);
        if (userPoints == null) {
            initUserPoints(userId);
            userPoints = getByUserId(userId);
        }

        // 更新积分
        userPoints.setPoints(userPoints.getPoints() + points);
        userPoints.setTotalEarned(userPoints.getTotalEarned() + points);
        userPoints.setUpdateTime(LocalDateTime.now());
        this.updateById(userPoints);

        // 记录积分日志
        PointsLog pointsLog = new PointsLog();
        pointsLog.setUserId(userId);
        pointsLog.setPoints(points);
        pointsLog.setType(type);
        pointsLog.setDescription(description);
        pointsLog.setCreateTime(LocalDateTime.now());
        pointsLogMapper.insert(pointsLog);

        log.info("用户积分增加: userId={}, points={}, type={}", userId, points, type);
        return true;
    }

    @Override
    public boolean deductPoints(Long userId, int points, String type, String description) {
        if (userId == null || points <= 0) {
            return false;
        }

        UserPoints userPoints = getByUserId(userId);
        if (userPoints == null || userPoints.getPoints() < points) {
            log.warn("积分不足: userId={}, required={}, available={}", userId, points,
                    userPoints != null ? userPoints.getPoints() : 0);
            return false;
        }

        // 更新积分
        userPoints.setPoints(userPoints.getPoints() - points);
        userPoints.setTotalSpent(userPoints.getTotalSpent() + points);
        userPoints.setUpdateTime(LocalDateTime.now());
        this.updateById(userPoints);

        // 记录积分日志
        PointsLog pointsLog = new PointsLog();
        pointsLog.setUserId(userId);
        pointsLog.setPoints(-points);
        pointsLog.setType(type);
        pointsLog.setDescription(description);
        pointsLog.setCreateTime(LocalDateTime.now());
        pointsLogMapper.insert(pointsLog);

        log.info("用户积分扣除: userId={}, points={}, type={}", userId, points, type);
        return true;
    }

    @Override
    public void initUserPoints(Long userId) {
        UserPoints userPoints = new UserPoints();
        userPoints.setUserId(userId);
        userPoints.setPoints(0);
        userPoints.setTotalEarned(0);
        userPoints.setTotalSpent(0);
        userPoints.setCreateTime(LocalDateTime.now());
        userPoints.setUpdateTime(LocalDateTime.now());
        this.save(userPoints);
        log.info("初始化用户积分: userId={}", userId);
    }
}
