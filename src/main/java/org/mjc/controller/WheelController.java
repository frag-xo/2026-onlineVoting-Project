package org.mjc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mjc.dto.DTO;
import org.mjc.entity.WheelPrize;
import org.mjc.exception.BusinessException;
import org.mjc.exception.ErrorCode;
import org.mjc.service.WheelService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 转盘抽奖接口
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Tag(name = "转盘抽奖", description = "投票后转盘抽奖相关接口")
@RestController
@RequestMapping("/api/wheel")
public class WheelController {

    @Resource
    private WheelService wheelService;

    @Operation(summary = "获取奖品列表", description = "获取所有启用的转盘奖品")
    @GetMapping("/prizes")
    public DTO<List<WheelPrize>> getPrizes() {
        List<WheelPrize> prizes = wheelService.getActivePrizes();
        DTO<List<WheelPrize>> dto = new DTO<>(200, "查询成功");
        dto.setT(prizes);
        return dto;
    }

    @Operation(summary = "抽奖", description = "投票后进行转盘抽奖")
    @PostMapping("/draw")
    public DTO<Map<String, Object>> draw(
            @Parameter(description = "投票ID", required = true)
            @RequestParam Long voteId,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        Long userId = (Long) request.getAttribute("currentUserId");
        Map<String, Object> result = wheelService.draw(userId, voteId);
        if (result == null) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "抽奖失败，可能已经抽过奖");
        }
        DTO<Map<String, Object>> dto = new DTO<>(200, "抽奖成功");
        dto.setT(result);
        return dto;
    }

    @Operation(summary = "检查是否已抽奖", description = "检查用户是否已对某投票抽奖")
    @GetMapping("/check")
    public DTO<Boolean> checkDrawn(
            @Parameter(description = "投票ID", required = true)
            @RequestParam Long voteId,
            jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        boolean drawn = wheelService.hasDrawn(userId, voteId);
        DTO<Boolean> dto = new DTO<>(200, "查询成功");
        dto.setT(drawn);
        return dto;
    }
}
