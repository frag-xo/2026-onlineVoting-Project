package org.mjc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.mjc.entity.Account;
import org.mjc.entity.ShopItem;
import org.mjc.entity.UserItem;
import org.mjc.entity.UserPoints;
import org.mjc.mapper.ShopItemMapper;
import org.mjc.mapper.UserItemMapper;
import org.mjc.service.AccountService;
import org.mjc.service.ShopService;
import org.mjc.service.UserPointsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ShopServiceImpl implements ShopService {

    @Resource
    private ShopItemMapper shopItemMapper;

    @Resource
    private UserItemMapper userItemMapper;

    @Resource
    private UserPointsService userPointsService;

    @Resource
    private AccountService accountService;

    @Override
    public List<ShopItem> getItemList() {
        LambdaQueryWrapper<ShopItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShopItem::getStatus, 1);
        wrapper.orderByAsc(ShopItem::getSortOrder);
        return shopItemMapper.selectList(wrapper);
    }

    @Override
    public boolean purchase(Long userId, Long itemId) {
        ShopItem item = shopItemMapper.selectById(itemId);
        if (item == null || item.getStatus() != 1) {
            log.warn("购买失败：商品不存在或已下架 - itemId={}", itemId);
            return false;
        }

        // 检查是否已拥有
        LambdaQueryWrapper<UserItem> check = new LambdaQueryWrapper<>();
        check.eq(UserItem::getUserId, userId).eq(UserItem::getItemId, itemId);
        if (userItemMapper.selectCount(check) > 0) {
            log.warn("购买失败：已拥有该商品 - userId={}, itemId={}", userId, itemId);
            return false;
        }

        // 扣积分
        boolean deducted = userPointsService.deductPoints(userId, item.getPrice(), "shop", "购买「" + item.getName() + "」");
        if (!deducted) {
            log.warn("购买失败：积分不足 - userId={}, price={}", userId, item.getPrice());
            return false;
        }

        // 发放物品
        UserItem userItem = new UserItem();
        userItem.setUserId(userId);
        userItem.setItemId(itemId);
        userItem.setIsEquipped(0);
        userItem.setPurchaseTime(LocalDateTime.now());
        userItemMapper.insert(userItem);

        log.info("购买成功: userId={}, item={}, price={}", userId, item.getName(), item.getPrice());
        return true;
    }

    @Override
    public List<Map<String, Object>> getUserItems(Long userId) {
        LambdaQueryWrapper<UserItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserItem::getUserId, userId);
        List<UserItem> userItems = userItemMapper.selectList(wrapper);

        if (userItems.isEmpty()) return Collections.emptyList();

        Set<Long> itemIds = userItems.stream().map(UserItem::getItemId).collect(Collectors.toSet());
        List<ShopItem> shopItems = shopItemMapper.selectBatchIds(itemIds);
        Map<Long, ShopItem> itemMap = shopItems.stream().collect(Collectors.toMap(ShopItem::getId, i -> i));

        List<Map<String, Object>> result = new ArrayList<>();
        for (UserItem ui : userItems) {
            ShopItem si = itemMap.get(ui.getItemId());
            if (si == null) continue;
            Map<String, Object> m = new HashMap<>();
            m.put("id", ui.getId());
            m.put("itemId", si.getId());
            m.put("name", si.getName());
            m.put("type", si.getType());
            m.put("content", si.getContent());
            m.put("icon", si.getIcon());
            m.put("isEquipped", ui.getIsEquipped());
            result.add(m);
        }
        return result;
    }

    @Override
    public boolean equipItem(Long userId, Long itemId, boolean equip) {
        LambdaQueryWrapper<UserItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserItem::getUserId, userId).eq(UserItem::getItemId, itemId);
        UserItem userItem = userItemMapper.selectOne(wrapper);
        if (userItem == null) return false;

        if (equip) {
            // 同类物品只能装备一个，先取消同类装备
            ShopItem item = shopItemMapper.selectById(itemId);
            if (item == null) return false;

            LambdaQueryWrapper<UserItem> sameType = new LambdaQueryWrapper<>();
            sameType.eq(UserItem::getUserId, userId).eq(UserItem::getIsEquipped, 1);
            List<UserItem> equipped = userItemMapper.selectList(sameType);
            for (UserItem ui : equipped) {
                ShopItem si = shopItemMapper.selectById(ui.getItemId());
                if (si != null && si.getType().equals(item.getType()) && !ui.getItemId().equals(itemId)) {
                    ui.setIsEquipped(0);
                    userItemMapper.updateById(ui);
                }
            }

            // 如果已装备则卸下，未装备则装上
            if (userItem.getIsEquipped() == 1) {
                userItem.setIsEquipped(0);
            } else {
                userItem.setIsEquipped(1);
            }
        } else {
            userItem.setIsEquipped(0);
        }
        userItemMapper.updateById(userItem);
        log.info("装备状态变更: userId={}, itemId={}, equip={}", userId, itemId, userItem.getIsEquipped());
        return true;
    }

    @Override
    public boolean useNameCard(Long userId, Long itemId, String newName) {
        LambdaQueryWrapper<UserItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserItem::getUserId, userId).eq(UserItem::getItemId, itemId);
        UserItem userItem = userItemMapper.selectOne(wrapper);
        if (userItem == null) return false;

        Account account = accountService.getById(userId);
        if (account == null) return false;

        account.setUname(newName);
        account.setUpdateTime(LocalDateTime.now());
        accountService.updateById(account);

        userItemMapper.deleteById(userItem.getId());
        log.info("改名卡使用成功: userId={}, newName={}", userId, newName);
        return true;
    }

    @Override
    public boolean useTopCard(Long userId, Long itemId, Long voteId) {
        LambdaQueryWrapper<UserItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserItem::getUserId, userId).eq(UserItem::getItemId, itemId);
        UserItem userItem = userItemMapper.selectOne(wrapper);
        if (userItem == null) return false;

        // 调用投票置顶逻辑（复用已有接口）
        // 这里由前端调 vote 的置顶接口，消费掉物品
        userItemMapper.deleteById(userItem.getId());
        log.info("置顶卡使用成功: userId={}, voteId={}", userId, voteId);
        return true;
    }
}
