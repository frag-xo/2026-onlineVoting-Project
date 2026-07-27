package org.mjc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mjc.dto.DTO;
import org.mjc.exception.BusinessException;
import org.mjc.exception.ErrorCode;
import org.mjc.service.PkService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

@Tag(name = "多分类PK对战", description = "美食/生活/数码等分类PK对战")
@RestController
@RequestMapping("/api/pk")
public class PkController {

    @Resource
    private PkService pkService;

    @Operation(summary = "获取所有PK分类")
    @GetMapping("/categories")
    public DTO<List<Map<String, Object>>> getCategories() {
        DTO<List<Map<String, Object>>> dto = new DTO<>(200, "查询成功");
        dto.setT(pkService.getCategories());
        return dto;
    }

    @Operation(summary = "获取随机对战话题")
    @GetMapping("/pair/{categoryId}")
    public DTO<Map<String, Object>> getPair(@PathVariable Long categoryId) {
        Map<String, Object> pair = pkService.getRandomPair(categoryId);
        if (pair == null) throw new BusinessException(ErrorCode.NOT_FOUND, "暂无对战话题");
        DTO<Map<String, Object>> dto = new DTO<>(200, "查询成功");
        dto.setT(pair);
        return dto;
    }

    @Operation(summary = "提交对战结果")
    @PostMapping("/battle")
    public DTO<Void> submit(
            @RequestParam Long categoryId,
            @RequestParam Long pairId,
            @RequestParam String chosen,
            jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        boolean ok = pkService.submitBattle(userId, categoryId, pairId, chosen);
        if (!ok) throw new BusinessException(ErrorCode.INTERNAL_ERROR, "提交失败");
        return new DTO<>(200, "记录成功");
    }

    @Operation(summary = "获取结果和称号")
    @GetMapping("/result/{categoryId}")
    public DTO<Map<String, Object>> getResult(
            @PathVariable Long categoryId,
            jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        DTO<Map<String, Object>> dto = new DTO<>(200, "查询成功");
        dto.setT(pkService.getUserResult(userId, categoryId));
        return dto;
    }
}
