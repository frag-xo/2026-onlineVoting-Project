package org.mjc.service;

import org.mjc.entity.AnimeFighter;
import java.util.List;
import java.util.Map;

public interface AnimeService {

    /** 获取所有动漫 */
    List<AnimeFighter> getAll();

    /** 获取两部随机动漫进行PK */
    List<AnimeFighter> getRandomPair();

    /** 提交对战结果，返回更新后的ELO */
    Map<String, Object> submitBattle(Long userId, Long winnerId, Long loserId);

    /** 获取用户的对战统计排行 */
    List<Map<String, Object>> getUserRanking(Long userId);

    /** 获取用户的最近对战历史 */
    List<Map<String, Object>> getBattleHistory(Long userId, int limit);

    /** 初始化动漫数据（如为空） */
    void initData();
}
