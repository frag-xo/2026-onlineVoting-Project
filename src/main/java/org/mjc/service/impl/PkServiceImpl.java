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
    public List<Map<String, Object>> getAllPairs(Long categoryId) {
        LambdaQueryWrapper<PkPair> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PkPair::getCategoryId, categoryId);
        List<PkPair> all = pairMapper.selectList(wrapper);
        Collections.shuffle(all, new java.security.SecureRandom());
        List<Map<String, Object>> result = new ArrayList<>();
        for (PkPair pair : all) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", pair.getId());
            m.put("optionA", pair.getOptionA());
            m.put("optionB", pair.getOptionB());
            result.add(m);
        }
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
        LambdaQueryWrapper<PkBattle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PkBattle::getUserId, userId).eq(PkBattle::getCategoryId, categoryId);
        List<PkBattle> battles = battleMapper.selectList(wrapper);

        Map<String, Integer> choiceCount = new HashMap<>();
        for (PkBattle b : battles) {
            choiceCount.merge(b.getChosen(), 1, Integer::sum);
        }

        String topChoice = choiceCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");

        String title = generateTitle(categoryId, topChoice, battles.size());
        String analysis = generateAnalysis(categoryId, choiceCount, battles.size());

        Map<String, Object> result = new HashMap<>();
        result.put("totalRounds", battles.size());
        result.put("title", title);
        result.put("analysis", analysis);
        return result;
    }

    /** 生成个性化分析文案 */
    private String generateAnalysis(Long categoryId, Map<String, Integer> choices, int total) {
        if (total == 0) return "还没有对战过，去试试吧！";
        int t = Math.max(total, 1);

        java.util.function.Function<String, Integer> cnt = key -> choices.getOrDefault(key, 0);
        java.util.function.Function<Integer, String> pct = c -> c * 100 / t + "%";

        switch (categoryId.intValue()) {
            case 1: { // 美食
                int sweet = cnt.apply("甜粽子") + cnt.apply("番茄炒蛋放糖") + cnt.apply("奶茶全糖");
                int savory = cnt.apply("咸粽子") + cnt.apply("香菜") + cnt.apply("麻酱");
                if (sweet > savory) return "你有一颗" + pct.apply(sweet) + "的甜党之心 🍬";
                else return "你的灵魂有" + pct.apply(savory) + "是咸党口味 🧂";
            }
            case 2: { // 生活
                int cat = cnt.apply("猫派"), dog = cnt.apply("狗派");
                int night = cnt.apply("熬夜冠军"), early = cnt.apply("早睡早起");
                int mt = cnt.apply("奶茶"), coffee = cnt.apply("咖啡");
                int home = cnt.apply("在家躺平"), travel = cnt.apply("出门旅游");

                if (cat > dog) return "你是" + pct.apply(cat) + "的猫奴 🐱，喵星人统治你";
                if (dog > cat) return "你是" + pct.apply(dog) + "的狗党 🐶";
                if (night > early) return "你是" + pct.apply(night) + "的夜猫子 🌙";
                if (mt > coffee) return "你是" + pct.apply(mt) + "的奶茶续命者 🧋";
                if (coffee > mt) return "你是" + pct.apply(coffee) + "的咖啡因战士 ☕";
                if (home > travel) return "你有" + pct.apply(home) + "的躺平基因 🛋️";
                return "你是" + pct.apply(travel) + "的旅行家 ✈️";
            }
            case 6: { // 奶茶
                int highEnd = cnt.apply("喜茶") + cnt.apply("奈雪的茶") + cnt.apply("乐乐茶");
                int budget = cnt.apply("蜜雪冰城") + cnt.apply("甜啦啦");
                int gufeng = cnt.apply("茶颜悦色") + cnt.apply("霸王茶姬") + cnt.apply("古茗");
                if (highEnd > budget && highEnd > gufeng) return "你有" + pct.apply(highEnd) + "的贵妇奶茶胃 💎";
                if (budget > highEnd && budget > gufeng) return "你有" + pct.apply(budget) + "的性价比之魂 💰";
                if (gufeng > highEnd && gufeng > budget) return "你有" + pct.apply(gufeng) + "的国风茶韵 🏮";
                return "你是" + pct.apply(Math.max(highEnd, Math.max(budget, gufeng))) + "的奶茶自由人 🧋";
            }
            case 5: { // 南北差异
                int north = cnt.apply("咸豆腐脑") + cnt.apply("暖气") + cnt.apply("搓澡")
                          + cnt.apply("北方蟑螂") + cnt.apply("澡堂") + cnt.apply("北方冬天外面冷")
                          + cnt.apply("大葱蘸酱");
                int south = cnt.apply("甜豆腐脑") + cnt.apply("空调") + cnt.apply("南方蟑螂")
                          + cnt.apply("南方冬天屋里冷") + cnt.apply("独立卫浴") + cnt.apply("精致小菜");
                if (north > south) return "你是" + pct.apply(north) + "的北方人 🧊，抗冻属性点满";
                if (south > north) return "你是" + pct.apply(south) + "的南方人 🌴，魔法攻击免疫";
                return "你是南北混血，左右逢源 🤝";
            }
            default: {
                var maxEntry = choices.entrySet().stream()
                    .max(Map.Entry.comparingByValue()).orElse(null);
                if (maxEntry == null) return "还没有对战记录";
                int p = maxEntry.getValue() * 100 / t;
                return "你的选择中「" + maxEntry.getKey() + "」占了" + p + "%";
            }
        }
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

        // 奶茶
        titles.put("喜茶", "喜茶信徒");
        titles.put("蜜雪冰城", "蜜雪精神股东");
        titles.put("一点点", "一点点铁粉");
        titles.put("茶颜悦色", "茶颜死忠");
        titles.put("霸王茶姬", "霸王茶人");
        titles.put("古茗", "古茗老客");
        titles.put("茶百道", "茶百道拥趸");
        titles.put("CoCo都可", "Coco常客");
        titles.put("奈雪的茶", "奈雪女孩");
        titles.put("乐乐茶", "乐乐茶教主");
        titles.put("沪上阿姨", "沪上阿姨VIP");
        titles.put("书亦烧仙草", "书亦爱好者");

        return titles.getOrDefault(topChoice, "投票先锋");
    }
}
