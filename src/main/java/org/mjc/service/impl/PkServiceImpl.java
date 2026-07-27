package org.mjc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.mjc.entity.PkBattle;
import org.mjc.entity.PkCategory;
import org.mjc.entity.PkPair;
import org.mjc.mapper.PkBattleMapper;
import org.mjc.mapper.PkCategoryMapper;
import org.mjc.mapper.PkPairMapper;
import org.mjc.service.PkService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PkServiceImpl implements PkService {

    @Resource
    private PkCategoryMapper categoryMapper;

    @Resource
    private PkPairMapper pairMapper;

    @Resource
    private PkBattleMapper battleMapper;

    @Override
    public List<Map<String, Object>> getCategories() {
        LambdaQueryWrapper<PkCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(PkCategory::getSortOrder);
        List<PkCategory> categories = categoryMapper.selectList(wrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        for (PkCategory c : categories) {
            // 统计每个分类的话题数
            LambdaQueryWrapper<PkPair> countWrapper = new LambdaQueryWrapper<>();
            countWrapper.eq(PkPair::getCategoryId, c.getId());
            long count = pairMapper.selectCount(countWrapper);
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getId());
            m.put("name", c.getName());
            m.put("icon", c.getIcon());
            m.put("description", c.getDescription());
            m.put("pairCount", count);
            result.add(m);
        }
        return result;
    }

    @Override
    public Map<String, Object> getRandomPair(Long categoryId) {
        LambdaQueryWrapper<PkPair> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PkPair::getCategoryId, categoryId);
        List<PkPair> all = pairMapper.selectList(wrapper);
        if (all.isEmpty()) return null;
        PkPair pair = all.get(new java.security.SecureRandom().nextInt(all.size()));
        Map<String, Object> result = new HashMap<>();
        result.put("id", pair.getId());
        result.put("categoryId", pair.getCategoryId());
        result.put("optionA", pair.getOptionA());
        result.put("optionB", pair.getOptionB());
        return result;
    }

    @Override
    public boolean submitBattle(Long userId, Long categoryId, Long pairId, String chosen) {
        PkBattle battle = new PkBattle();
        battle.setUserId(userId);
        battle.setCategoryId(categoryId);
        battle.setPairId(pairId);
        battle.setChosen(chosen);
        battle.setCreateTime(LocalDateTime.now());
        return battleMapper.insert(battle) > 0;
    }

    @Override
    public Map<String, Object> getUserResult(Long userId, Long categoryId) {
        // 查询该分类下所有对战记录
        LambdaQueryWrapper<PkBattle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PkBattle::getUserId, userId).eq(PkBattle::getCategoryId, categoryId);
        List<PkBattle> battles = battleMapper.selectList(wrapper);

        // 统计每个选项被选的次数
        Map<String, Integer> choiceCount = new HashMap<>();
        for (PkBattle b : battles) {
            choiceCount.merge(b.getChosen(), 1, Integer::sum);
        }

        // 找被选最多的选项
        String topChoice = choiceCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");

        // 根据分类和最高选项生成称号
        String title = generateTitle(categoryId, topChoice, battles.size());

        Map<String, Object> result = new HashMap<>();
        result.put("totalRounds", battles.size());
        result.put("topChoice", topChoice);
        result.put("title", title);
        result.put("choices", choiceCount);
        return result;
    }

    private String generateTitle(Long categoryId, String topChoice, int totalRounds) {
        if (totalRounds == 0) return "还没对战过";
        Map<String, String> titles = new HashMap<>();

        // 美食
        titles.put("甜粽子", "甜党领袖");
        titles.put("咸粽子", "咸党守护者");
        titles.put("香菜", "香菜教父");
        titles.put("不要香菜", "反香菜联盟会长");
        titles.put("火锅", "火锅仙人");
        titles.put("烧烤", "烧烤大王");
        titles.put("麻酱", "麻酱信徒");
        titles.put("油碟", "油碟使者");
        titles.put("螺蛳粉", "螺蛳粉勇士");
        titles.put("番茄炒蛋放糖", "甜口掌门人");
        titles.put("奶茶全糖", "全糖主义战士");

        // 生活
        titles.put("猫派", "猫奴协会会长");
        titles.put("狗派", "狗党首领");
        titles.put("早睡早起", "养生达人");
        titles.put("熬夜冠军", "夜猫子之王");
        titles.put("奶茶", "奶茶续命者");
        titles.put("咖啡", "咖啡因战士");
        titles.put("在家躺平", "躺平学家");
        titles.put("出门旅游", "旅行家");
        titles.put("社恐", "社恐保护协会");

        // 数码
        titles.put("苹果", "苹果信徒");
        titles.put("安卓", "安卓极客");
        titles.put("微信", "微信人");
        titles.put("Chrome", "Chrome教徒");
        titles.put("深色模式", "暗夜行者");
        titles.put("浅色模式", "阳光少年");

        // 游戏
        titles.put("英雄联盟", "峡谷召唤师");
        titles.put("王者荣耀", "王者之星");
        titles.put("原神", "原神旅行者");
        titles.put("主机", "主机党");
        titles.put("PC", "PC Master Race");
        titles.put("单机游戏", "独狼玩家");
        titles.put("网络游戏", "社交玩家");

        // 南北
        titles.put("甜豆腐脑", "甜党传人");
        titles.put("咸豆腐脑", "咸党正宗");
        titles.put("暖气", "暖气拥趸");
        titles.put("搓澡", "搓澡文化人");
        titles.put("北方蟑螂", "北方勇士");
        titles.put("南方蟑螂", "南方幸存者");

        return titles.getOrDefault(topChoice, "投票先锋");
    }
}
