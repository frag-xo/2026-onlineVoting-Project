package org.mjc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mjc.dto.DTO;
import org.mjc.entity.AnimeFighter;
import org.mjc.exception.BusinessException;
import org.mjc.exception.ErrorCode;
import org.mjc.service.AnimeService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

@Tag(name = "动漫PK对战", description = "动漫二选一PK对战功能")
@RestController
@RequestMapping("/api/anime")
public class AnimeController {

    @Resource
    private AnimeService animeService;

    @Operation(summary = "获取所有动漫")
    @GetMapping("/list")
    public DTO<List<AnimeFighter>> getAll() {
        DTO<List<AnimeFighter>> dto = new DTO<>(200, "查询成功");
        dto.setT(animeService.getAll());
        return dto;
    }

    @Operation(summary = "获取随机PK对战")
    @GetMapping("/pair")
    public DTO<List<AnimeFighter>> getPair() {
        List<AnimeFighter> pair = animeService.getRandomPair();
        if (pair.size() < 2) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "动漫数据不足");
        }
        DTO<List<AnimeFighter>> dto = new DTO<>(200, "查询成功");
        dto.setT(pair);
        return dto;
    }

    @Operation(summary = "提交对战结果")
    @PostMapping("/battle")
    public DTO<Map<String, Object>> submitBattle(
            @RequestParam Long winnerId,
            @RequestParam Long loserId,
            jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        Map<String, Object> result = animeService.submitBattle(userId, winnerId, loserId);
        if (result == null) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "对战记录失败");
        }
        DTO<Map<String, Object>> dto = new DTO<>(200, "记录成功");
        dto.setT(result);
        return dto;
    }

    @Operation(summary = "获取我的动漫排行")
    @GetMapping("/ranking")
    public DTO<List<Map<String, Object>>> getRanking(jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        DTO<List<Map<String, Object>>> dto = new DTO<>(200, "查询成功");
        dto.setT(animeService.getUserRanking(userId));
        return dto;
    }

    @Operation(summary = "获取对战历史")
    @GetMapping("/history")
    public DTO<List<Map<String, Object>>> getHistory(
            @RequestParam(defaultValue = "20") Integer limit,
            jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        DTO<List<Map<String, Object>>> dto = new DTO<>(200, "查询成功");
        dto.setT(animeService.getBattleHistory(userId, limit));
        return dto;
    }
}
