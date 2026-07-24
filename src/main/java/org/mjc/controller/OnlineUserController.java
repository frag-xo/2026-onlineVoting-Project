package org.mjc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mjc.dto.DTO;
import org.mjc.service.OnlineUserService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Map;
import java.util.Set;

/**
 * 在线用户管理接口
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Tag(name = "在线用户管理", description = "在线用户状态相关接口")
@RestController
@RequestMapping("/api/online")
public class OnlineUserController {

    @Resource
    private OnlineUserService onlineUserService;

    @Operation(summary = "获取在线用户数量", description = "获取当前在线用户数量")
    @GetMapping("/count")
    public DTO<Integer> getOnlineCount() {
        int count = onlineUserService.getOnlineCount();
        DTO<Integer> dto = new DTO<>(200, "查询成功");
        dto.setT(count);
        return dto;
    }

    @Operation(summary = "获取在线用户列表", description = "获取所有在线用户信息")
    @GetMapping("/users")
    public DTO<Map<Long, String>> getOnlineUsers() {
        Map<Long, String> users = onlineUserService.getOnlineUsers();
        DTO<Map<Long, String>> dto = new DTO<>(200, "查询成功");
        dto.setT(users);
        return dto;
    }

    @Operation(summary = "检查用户是否在线", description = "检查指定用户是否在线")
    @GetMapping("/status/{userId}")
    public DTO<Boolean> isOnline(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        boolean online = onlineUserService.isOnline(userId);
        DTO<Boolean> dto = new DTO<>(200, "查询成功");
        dto.setT(online);
        return dto;
    }

    @Operation(summary = "用户上线", description = "标记用户为在线状态")
    @PostMapping("/online/{userId}")
    public DTO<Void> userOnline(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "用户名", required = true)
            @RequestParam String username) {
        onlineUserService.userOnline(userId, username);
        return new DTO<>(200, "用户已上线");
    }

    @Operation(summary = "用户下线", description = "标记用户为离线状态")
    @PostMapping("/offline/{userId}")
    public DTO<Void> userOffline(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        onlineUserService.userOffline(userId);
        return new DTO<>(200, "用户已下线");
    }

    @Operation(summary = "心跳检测", description = "更新用户活跃状态")
    @PostMapping("/heartbeat/{userId}")
    public DTO<Void> heartbeat(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        onlineUserService.heartbeat(userId);
        return new DTO<>(200, "心跳成功");
    }

    @Operation(summary = "清理超时用户", description = "清理超过5分钟未活跃的用户")
    @PostMapping("/clean")
    public DTO<Void> cleanTimeoutUsers() {
        onlineUserService.cleanTimeoutUsers();
        return new DTO<>(200, "清理完成");
    }
}
