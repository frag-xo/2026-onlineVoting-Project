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
     * 初始化用户积分
     */
    void initUserPoints(Long userId);
}
