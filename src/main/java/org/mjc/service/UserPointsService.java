package org.mjc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mjc.entity.UserPoints;

/**
 * 用户积分服务接口
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
public interface UserPointsService extends IService<UserPoints> {

    /**
     * 获取用户积分
     */
    UserPoints getByUserId(Long userId);

    /**
     * 增加积分
     */
    boolean addPoints(Long userId, int points, String type, String description);

    /**
     * 扣除积分
     */
    boolean deductPoints(Long userId, int points, String type, String description);

    /**
     * 初始化用户积分（注册时+20）
     */
    void initUserPoints(Long userId);

    /**
     * 每日登录积分（+10，每天一次）
     */
    boolean addDailyLoginPoints(Long userId);

    /**
     * 投票积分（+5，每天最多20次）
     */
    boolean addVotePoints(Long userId);

    /**
     * 评论积分（+2，每天最多10次）
     */
    boolean addCommentPoints(Long userId);

    /**
     * 评论被点赞积分（+1）
     */
    boolean addCommentLikePoints(Long userId);

    /**
     * 收藏积分（+3，最多10次）
     */
    boolean addFavoritePoints(Long userId);

    /**
     * 发布的投票被参与积分（+1）
     */
    boolean addPublishVotePoints(Long userId);

    /**
     * 投票被推荐积分（+50）
     */
    boolean addRecommendedPoints(Long userId);

    /**
     * 发布投票消费积分（-10）
     */
    boolean deductPublishPoints(Long userId);

    /**
     * 置顶投票消费积分（-50）
     */
    boolean deductTopPoints(Long userId);

    /**
     * 匿名投票消费积分（-5）
     */
    boolean deductAnonymousPoints(Long userId);

    /**
     * 计算用户等级
     */
    int calculateLevel(int totalPoints);

    /**
     * 检查等级是否满足要求
     */
    boolean checkLevelRequirement(Long userId, int requiredLevel);
}
