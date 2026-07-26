package org.mjc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mjc.dto.DTO;
import org.mjc.entity.ShopItem;
import org.mjc.exception.BusinessException;
import org.mjc.exception.ErrorCode;
import org.mjc.service.ShopService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Tag(name = "积分商城", description = "积分商品浏览、购买、装备")
@RestController
@RequestMapping("/api/shop")
public class ShopController {

    @Resource
    private ShopService shopService;

    @Operation(summary = "商品列表", description = "获取所有上架商品")
    @GetMapping("/items")
    public DTO<List<Map<String, Object>>> getItems(HttpServletRequest request) {
        String base = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        List<ShopItem> items = shopService.getItemList();
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (ShopItem item : items) {
            Map<String, Object> m = new java.util.HashMap<>();
            m.put("id", item.getId());
            m.put("name", item.getName());
            m.put("description", item.getDescription());
            m.put("type", item.getType());
            m.put("content", item.getContent());
            m.put("price", item.getPrice());
            m.put("icon", item.getIcon() != null ? base + item.getIcon() : null);
            m.put("sortOrder", item.getSortOrder());
            m.put("status", item.getStatus());
            result.add(m);
        }
        DTO<List<Map<String, Object>>> dto = new DTO<>(200, "查询成功");
        dto.setT(result);
        return dto;
    }

    @Operation(summary = "购买商品", description = "使用积分购买商品")
    @PostMapping("/buy/{itemId}")
    public DTO<Void> buy(
            @PathVariable Long itemId,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        Long userId = (Long) request.getAttribute("currentUserId");
        boolean result = shopService.purchase(userId, itemId);
        if (!result) throw new BusinessException(ErrorCode.INTERNAL_ERROR, "购买失败，请检查积分或是否已拥有");
        return new DTO<>(200, "购买成功");
    }

    @Operation(summary = "我的物品", description = "获取已购买的物品列表")
    @GetMapping("/my-items")
    public DTO<List<Map<String, Object>>> myItems(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        String base = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        List<Map<String, Object>> items = shopService.getUserItems(userId);
        for (Map<String, Object> item : items) {
            String icon = (String) item.get("icon");
            if (icon != null && !icon.startsWith("http")) {
                item.put("icon", base + icon);
            }
        }
        DTO<List<Map<String, Object>>> dto = new DTO<>(200, "查询成功");
        dto.setT(items);
        return dto;
    }

    @Operation(summary = "装备/卸下物品", description = "切换物品的装备状态")
    @PutMapping("/equip/{itemId}")
    public DTO<Void> equip(
            @PathVariable Long itemId,
            @RequestParam boolean equip,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        Long userId = (Long) request.getAttribute("currentUserId");
        boolean result = shopService.equipItem(userId, itemId, equip);
        if (!result) throw new BusinessException(ErrorCode.INTERNAL_ERROR, "操作失败");
        return new DTO<>(200, "装备状态已更新");
    }

    @Operation(summary = "使用改名卡", description = "消耗改名卡修改用户名")
    @PutMapping("/use/name-card/{itemId}")
    public DTO<Void> useNameCard(
            @PathVariable Long itemId,
            @RequestParam String newName,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        Long userId = (Long) request.getAttribute("currentUserId");
        boolean result = shopService.useNameCard(userId, itemId, newName);
        if (!result) throw new BusinessException(ErrorCode.INTERNAL_ERROR, "使用失败");
        return new DTO<>(200, "用户名修改成功");
    }
}
