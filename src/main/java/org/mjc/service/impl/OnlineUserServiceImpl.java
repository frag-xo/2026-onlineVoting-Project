package org.mjc.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.mjc.service.OnlineUserService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 在线用户服务实现类（使用Redis存储）
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Slf4j
@Service
public class OnlineUserServiceImpl implements OnlineUserService {

    private static final String ONLINE_USERS_KEY = "online:users";
    private static final String ONLINE_USER_PREFIX = "online:user:";
    private static final long USER_TIMEOUT_MINUTES = 5;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void userOnline(Long userId, String username) {
        // 存储用户在线状态（5分钟过期）
        stringRedisTemplate.opsForValue().set(
                ONLINE_USER_PREFIX + userId,
                username,
                USER_TIMEOUT_MINUTES,
                TimeUnit.MINUTES
        );

        // 添加到在线用户集合
        stringRedisTemplate.opsForSet().add(ONLINE_USERS_KEY, String.valueOf(userId));

        log.info("用户上线: {} (ID: {})", username, userId);
    }

    @Override
    public void userOffline(Long userId) {
        // 删除用户在线状态
        stringRedisTemplate.delete(ONLINE_USER_PREFIX + userId);

        // 从在线用户集合中移除
        stringRedisTemplate.opsForSet().remove(ONLINE_USERS_KEY, String.valueOf(userId));

        log.info("用户下线: ID {}", userId);
    }

    @Override
    public boolean isOnline(Long userId) {
        return Boolean.TRUE.equals(
                stringRedisTemplate.hasKey(ONLINE_USER_PREFIX + userId)
        );
    }

    @Override
    public int getOnlineCount() {
        Long count = stringRedisTemplate.opsForSet().size(ONLINE_USERS_KEY);
        return count != null ? count.intValue() : 0;
    }

    @Override
    public Set<Long> getOnlineUserIds() {
        Set<String> userIds = stringRedisTemplate.opsForSet().members(ONLINE_USERS_KEY);
        if (userIds == null) {
            return new HashSet<>();
        }

        Set<Long> result = new HashSet<>();
        for (String userId : userIds) {
            try {
                result.add(Long.parseLong(userId));
            } catch (NumberFormatException e) {
                log.warn("无效的用户ID: {}", userId);
            }
        }
        return result;
    }

    @Override
    public Map<Long, String> getOnlineUsers() {
        Set<Long> userIds = getOnlineUserIds();
        Map<Long, String> result = new HashMap<>();

        for (Long userId : userIds) {
            String username = stringRedisTemplate.opsForValue().get(ONLINE_USER_PREFIX + userId);
            if (username != null) {
                result.put(userId, username);
            }
        }
        return result;
    }

    @Override
    public void heartbeat(Long userId) {
        // 刷新用户在线状态的过期时间
        String username = stringRedisTemplate.opsForValue().get(ONLINE_USER_PREFIX + userId);
        if (username != null) {
            stringRedisTemplate.opsForValue().set(
                    ONLINE_USER_PREFIX + userId,
                    username,
                    USER_TIMEOUT_MINUTES,
                    TimeUnit.MINUTES
            );
            log.debug("用户心跳: ID {}", userId);
        }
    }

    @Override
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void cleanTimeoutUsers() {
        Set<Long> userIds = getOnlineUserIds();
        int cleanedCount = 0;

        for (Long userId : userIds) {
            if (!isOnline(userId)) {
                // 用户已过期，从集合中移除
                stringRedisTemplate.opsForSet().remove(ONLINE_USERS_KEY, String.valueOf(userId));
                cleanedCount++;
            }
        }

        if (cleanedCount > 0) {
            log.info("清理超时用户: {} 个", cleanedCount);
        }
    }
}
