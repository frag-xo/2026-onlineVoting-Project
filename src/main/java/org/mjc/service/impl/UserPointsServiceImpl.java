package org.mjc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

    @Override
    public boolean addDailyLoginPoints(Long userId) {
        // 检查今天是否已经领取过登录积分
        String today = LocalDateTime.now().toLocalDate().toString();
        LambdaQueryWrapper<PointsLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsLog::getUserId, userId);
        wrapper.eq(PointsLog::getType, "daily_login");
        wrapper.like(PointsLog::getDescription, today);
        if (pointsLogMapper.selectCount(wrapper) > 0) {
            log.debug("今日已领取登录积分: userId={}", userId);
            return false;
        }
        return addPoints(userId, 10, "daily_login", "每日登录奖励 " + today);
    }

    @Override
    public boolean addVotePoints(Long userId) {
        // 检查今天投票积分是否已达上限（20次）
        String today = LocalDateTime.now().toLocalDate().toString();
        LambdaQueryWrapper<PointsLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsLog::getUserId, userId);
        wrapper.eq(PointsLog::getType, "vote");
        wrapper.like(PointsLog::getDescription, today);
        if (pointsLogMapper.selectCount(wrapper) >= 20) {
            log.debug("今日投票积分已达上限: userId={}", userId);
            return false;
        }
        return addPoints(userId, 5, "vote", "参与投票奖励 " + today);
    }

    @Override
    public boolean addCommentPoints(Long userId) {
        // 检查今天评论积分是否已达上限（10次）
        String today = LocalDateTime.now().toLocalDate().toString();
        LambdaQueryWrapper<PointsLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsLog::getUserId, userId);
        wrapper.eq(PointsLog::getType, "comment");
        wrapper.like(PointsLog::getDescription, today);
        if (pointsLogMapper.selectCount(wrapper) >= 10) {
            log.debug("今日评论积分已达上限: userId={}", userId);
            return false;
        }
        return addPoints(userId, 2, "comment", "评论奖励 " + today);
    }

    @Override
    public boolean addCommentLikePoints(Long userId) {
        return addPoints(userId, 1, "comment_like", "评论被点赞");
    }

    @Override
    public boolean addFavoritePoints(Long userId) {
        // 检查收藏积分是否已达上限（10次）
        LambdaQueryWrapper<PointsLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsLog::getUserId, userId);
        wrapper.eq(PointsLog::getType, "favorite");
        if (pointsLogMapper.selectCount(wrapper) >= 10) {
            log.debug("收藏积分已达上限: userId={}", userId);
            return false;
        }
        return addPoints(userId, 3, "favorite", "收藏投票奖励");
    }

    @Override
    public boolean addPublishVotePoints(Long userId) {
        return addPoints(userId, 1, "publish_vote", "发布的投票被参与");
    }

    @Override
    public boolean addRecommendedPoints(Long userId) {
        return addPoints(userId, 50, "recommended", "投票被管理员推荐");
    }

    @Override
    public int calculateLevel(int totalPoints) {
        if (totalPoints >= 10000) return 5; // 大师
        if (totalPoints >= 5000) return 4;  // 专家
        if (totalPoints >= 2000) return 3;  // 资深用户
        if (totalPoints >= 500) return 2;   // 活跃用户
        return 1;                            // 普通用户
    }

    @Override
    public boolean deductPublishPoints(Long userId) {
        return deductPoints(userId, 10, "publish", "发布投票消费");
    }

    @Override
    public boolean deductTopPoints(Long userId) {
        return deductPoints(userId, 50, "top", "置顶投票消费");
    }

    @Override
    public boolean deductAnonymousPoints(Long userId) {
        return deductPoints(userId, 5, "anonymous", "匿名投票消费");
    }

    @Override
    public boolean checkLevelRequirement(Long userId, int requiredLevel) {
        UserPoints userPoints = getByUserId(userId);
        if (userPoints == null) {
            return false;
        }
        int userLevel = calculateLevel(userPoints.getTotalEarned());
        return userLevel >= requiredLevel;
    }
}
