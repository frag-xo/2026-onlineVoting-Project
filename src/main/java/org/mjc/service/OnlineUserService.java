package org.mjc.service;

import java.util.Set;

/**
 * 在线用户服务接口
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
public interface OnlineUserService {

    /**
     * 用户上线
     *
     * @param userId 用户ID
     * @param username 用户名
     */
    void userOnline(Long userId, String username);

    /**
     * 用户下线
     *
     * @param userId 用户ID
     */
    void userOffline(Long userId);

    /**
     * 判断用户是否在线
     *
     * @param userId 用户ID
     * @return 是否在线
     */
    boolean isOnline(Long userId);

    /**
     * 获取在线用户数量
     *
     * @return 在线用户数
     */
    int getOnlineCount();

    /**
     * 获取所有在线用户ID
     *
     * @return 在线用户ID集合
     */
    Set<Long> getOnlineUserIds();

    /**
     * 获取所有在线用户信息
     *
     * @return 用户ID -> 用户名 的映射
     */
    java.util.Map<Long, String> getOnlineUsers();

    /**
     * 心跳检测，更新用户最后活跃时间
     *
     * @param userId 用户ID
     */
    void heartbeat(Long userId);

    /**
     * 清理超时用户（超过5分钟未活跃）
     */
    void cleanTimeoutUsers();
}
