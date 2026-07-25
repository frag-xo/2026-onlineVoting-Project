package org.mjc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.mjc.entity.AnimeBattle;
import org.mjc.entity.AnimeFighter;
import org.mjc.mapper.AnimeBattleMapper;
import org.mjc.mapper.AnimeFighterMapper;
import org.mjc.service.AnimeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AnimeServiceImpl implements AnimeService {

    @Resource
    private AnimeFighterMapper fighterMapper;

    @Resource
    private AnimeBattleMapper battleMapper;

    private static final int K_FACTOR = 32;
    private static final int DEFAULT_ELO = 1500;

    @Override
    public List<AnimeFighter> getAll() {
        return fighterMapper.selectList(null);
    }

    @Override
    public List<AnimeFighter> getRandomPair() {
        List<AnimeFighter> all = getAll();
        if (all.size() < 2) return all;
        Collections.shuffle(all, new java.security.SecureRandom());
        return all.subList(0, 2);
    }

    @Override
    public Map<String, Object> submitBattle(Long userId, Long winnerId, Long loserId) {
        AnimeFighter winner = fighterMapper.selectById(winnerId);
        AnimeFighter loser = fighterMapper.selectById(loserId);
        if (winner == null || loser == null) return null;

        // ELO 计算
        double expectedWinner = 1.0 / (1 + Math.pow(10, (loser.getEloRating() - winner.getEloRating()) / 400.0));
        double expectedLoser = 1.0 / (1 + Math.pow(10, (winner.getEloRating() - loser.getEloRating()) / 400.0));

        int newWinnerElo = (int) Math.round(winner.getEloRating() + K_FACTOR * (1 - expectedWinner));
        int newLoserElo = (int) Math.round(loser.getEloRating() + K_FACTOR * (0 - expectedLoser));

        // 更新胜者
        winner.setEloRating(newWinnerElo);
        winner.setWinCount(winner.getWinCount() + 1);
        winner.setBattleCount(winner.getBattleCount() + 1);
        fighterMapper.updateById(winner);

        // 更新败者
        loser.setEloRating(newLoserElo);
        loser.setBattleCount(loser.getBattleCount() + 1);
        fighterMapper.updateById(loser);

        // 记录对战
        AnimeBattle battle = new AnimeBattle();
        battle.setUserId(userId);
        battle.setWinnerId(winnerId);
        battle.setLoserId(loserId);
        battle.setCreateTime(LocalDateTime.now());
        battleMapper.insert(battle);

        Map<String, Object> result = new HashMap<>();
        result.put("winnerElo", newWinnerElo);
        result.put("loserElo", newLoserElo);
        return result;
    }

    @Override
    public List<Map<String, Object>> getUserRanking(Long userId) {
        // 查询该用户的所有对战记录
        LambdaQueryWrapper<AnimeBattle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnimeBattle::getUserId, userId);
        List<AnimeBattle> battles = battleMapper.selectList(wrapper);

        // 统计每个动漫的胜场
        Map<Long, Integer> winMap = new HashMap<>();
        Map<Long, Integer> totalMap = new HashMap<>();
        for (AnimeBattle b : battles) {
            winMap.merge(b.getWinnerId(), 1, Integer::sum);
            totalMap.merge(b.getWinnerId(), 1, Integer::sum);
            totalMap.merge(b.getLoserId(), 1, Integer::sum);
        }

        // 组装排行
        List<AnimeFighter> all = getAll();
        List<Map<String, Object>> ranking = new ArrayList<>();
        for (AnimeFighter f : all) {
            int wins = winMap.getOrDefault(f.getId(), 0);
            int total = totalMap.getOrDefault(f.getId(), 0);
            double winRate = total > 0 ? (double) wins / total * 100 : 0;

            Map<String, Object> item = new HashMap<>();
            item.put("id", f.getId());
            item.put("name", f.getName());
            item.put("imageUrl", f.getImageUrl());
            item.put("wins", wins);
            item.put("total", total);
            item.put("winRate", Math.round(winRate * 10) / 10.0);
            item.put("eloRating", f.getEloRating());
            ranking.add(item);
        }

        // 按胜率降序，然后按ELO降序
        ranking.sort((a, b) -> {
            int cmp = Double.compare((Double) b.get("winRate"), (Double) a.get("winRate"));
            if (cmp != 0) return cmp;
            return Integer.compare((Integer) b.get("eloRating"), (Integer) a.get("eloRating"));
        });

        // 标排名
        for (int i = 0; i < ranking.size(); i++) {
            ranking.get(i).put("rank", i + 1);
        }

        return ranking;
    }

    @Override
    public List<Map<String, Object>> getBattleHistory(Long userId, int limit) {
        LambdaQueryWrapper<AnimeBattle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnimeBattle::getUserId, userId);
        wrapper.orderByDesc(AnimeBattle::getCreateTime);
        wrapper.last("LIMIT " + limit);

        List<AnimeBattle> battles = battleMapper.selectList(wrapper);
        List<Map<String, Object>> result = new ArrayList<>();

        for (AnimeBattle b : battles) {
            AnimeFighter winner = fighterMapper.selectById(b.getWinnerId());
            AnimeFighter loser = fighterMapper.selectById(b.getLoserId());
            if (winner == null || loser == null) continue;

            Map<String, Object> item = new HashMap<>();
            item.put("id", b.getId());
            item.put("winnerName", winner.getName());
            item.put("winnerImage", winner.getImageUrl());
            item.put("loserName", loser.getName());
            item.put("loserImage", loser.getImageUrl());
            item.put("createTime", b.getCreateTime());
            result.add(item);
        }
        return result;
    }

    @Override
    @PostConstruct
    public void initData() {
        Long count = fighterMapper.selectCount(null);
        if (count != null && count > 0) {
            log.info("动漫数据已存在，跳过初始化 (count={})", count);
            return;
        }

        List<AnimeFighter> list = Arrays.asList(
            create("鬼灭之刃", "https://picsum.photos/seed/kimetsu/400/560"),
            create("进击的巨人", "https://picsum.photos/seed/aot/400/560"),
            create("咒术回战", "https://picsum.photos/seed/jujutsu/400/560"),
            create("海贼王", "https://picsum.photos/seed/onepiece/400/560"),
            create("火影忍者", "https://picsum.photos/seed/naruto/400/560"),
            create("龙珠", "https://picsum.photos/seed/dragonball/400/560"),
            create("灌篮高手", "https://picsum.photos/seed/slamdunk/400/560"),
            create("死亡笔记", "https://picsum.photos/seed/deathnote/400/560"),
            create("钢之炼金术师", "https://picsum.photos/seed/fma/400/560"),
            create("命运石之门", "https://picsum.photos/seed/steinsgate/400/560"),
            create("全职猎人", "https://picsum.photos/seed/hunter/400/560"),
            create("EVA 新世纪福音战士", "https://picsum.photos/seed/eva/400/560"),
            create("千与千寻", "https://picsum.photos/seed/spiritedaway/400/560"),
            create("你的名字", "https://picsum.photos/seed/yourname/400/560"),
            create("声之形", "https://picsum.photos/seed/kokoro/400/560"),
            create("路人超能100", "https://picsum.photos/seed/mob/400/560"),
            create("孤独摇滚", "https://picsum.photos/seed/bocchi/400/560"),
            create("辉夜大小姐想让我告白", "https://picsum.photos/seed/kaguya/400/560"),
            create("间谍过家家", "https://picsum.photos/seed/spyfamily/400/560"),
            create("葬送的芙莉莲", "https://picsum.photos/seed/frieren/400/560"),
            create("JoJo的奇妙冒险", "https://picsum.photos/seed/jojo/400/560"),
            create("排球少年", "https://picsum.photos/seed/haikyuu/400/560"),
            create("东京喰种", "https://picsum.photos/seed/tokyoghoul/400/560"),
            create("反叛的鲁路修", "https://picsum.photos/seed/codegeass/400/560"),
            create("紫罗兰永恒花园", "https://picsum.photos/seed/violet/400/560"),
            create("一拳超人", "https://picsum.photos/seed/onepunch/400/560"),
            create("夏目友人帐", "https://picsum.photos/seed/natsume/400/560"),
            create("关于我转生变成史莱姆这档事", "https://picsum.photos/seed/tensura/400/560"),
            create("名侦探柯南", "https://picsum.photos/seed/conan/400/560"),
            create("暗杀教室", "https://picsum.photos/seed/assassination/400/560"),
            create("蜡笔小新", "https://picsum.photos/seed/shinnosuke/400/560")
        );

        for (AnimeFighter f : list) {
            fighterMapper.insert(f);
        }
        log.info("动漫数据初始化完成，共 {} 部", list.size());
    }

    private AnimeFighter create(String name, String imageUrl) {
        AnimeFighter f = new AnimeFighter();
        f.setName(name);
        f.setImageUrl(imageUrl);
        f.setEloRating(DEFAULT_ELO);
        f.setWinCount(0);
        f.setBattleCount(0);
        return f;
    }
}
