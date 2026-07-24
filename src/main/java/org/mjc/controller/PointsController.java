package org.mjc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mjc.dto.DTO;
import org.mjc.entity.Account;
import org.mjc.entity.UserPoints;
import org.mjc.exception.BusinessException;
import org.mjc.exception.ErrorCode;
import org.mjc.service.AccountService;
import org.mjc.service.UserPointsService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 积分管理接口
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Tag(name = "积分管理", description = "用户积分相关接口")
@RestController
@RequestMapping("/api/points")
public class PointsController {

    @Resource
    private UserPointsService userPointsService;

    @Resource
    private AccountService accountService;

    @Operation(summary = "获取用户积分", description = "获取当前用户的积分信息")
    @GetMapping("/my")
    public DTO<UserPoints> getMyPoints(jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        UserPoints points = userPointsService.getByUserId(userId);
        if (points == null) {
            // 初始化积分
            userPointsService.initUserPoints(userId);
            points = userPointsService.getByUserId(userId);
        }
        DTO<UserPoints> dto = new DTO<>(200, "查询成功");
        dto.setT(points);
        return dto;
    }

    @Operation(summary = "增加积分", description = "增加用户积分（管理员）")
    @PostMapping("/add")
    public DTO<Void> addPoints(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "积分数量", required = true)
            @RequestParam Integer points,
            @Parameter(description = "描述")
            @RequestParam(required = false) String description) throws BusinessException {
        boolean result = userPointsService.addPoints(userId, points, "admin", description != null ? description : "管理员添加");
        if (!result) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "添加积分失败");
        }
        return new DTO<>(200, "积分添加成功");
    }

    @Operation(summary = "积分排行榜", description = "获取积分排行榜")
    @GetMapping("/ranking")
    public DTO<List<Map<String, Object>>> getPointsRanking(
            @Parameter(description = "排行数量", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        // 获取所有用户积分
        List<UserPoints> allPoints = userPointsService.list();

        // 按积分排序
        allPoints.sort((a, b) -> Integer.compare(b.getTotalEarned(), a.getTotalEarned()));

        // 构建排行榜数据
        List<Map<String, Object>> ranking = new ArrayList<>();
        int rank = 1;
        for (UserPoints points : allPoints) {
            if (rank > limit) break;

            Account account = accountService.getById(points.getUserId());
            if (account == null) continue;

            Map<String, Object> item = new HashMap<>();
            item.put("rank", rank);
            item.put("userId", points.getUserId());
            item.put("username", account.getUname());
            item.put("realname", account.getRealname());
            item.put("level", account.getLevel());
            item.put("totalPoints", points.getTotalEarned());
            item.put("currentPoints", points.getPoints());
            ranking.add(item);
            rank++;
        }

        DTO<List<Map<String, Object>>> dto = new DTO<>(200, "查询成功");
        dto.setT(ranking);
        return dto;
    }
}
