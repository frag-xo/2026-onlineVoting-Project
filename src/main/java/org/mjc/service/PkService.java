package org.mjc.service;

import java.util.List;
import java.util.Map;

public interface PkService {

    /** 获取所有分类 */
    List<Map<String, Object>> getCategories();

    /** 获取某个分类的随机对战话题 */
    Map<String, Object> getRandomPair(Long categoryId);

    /** 获取某个分类所有话题（打乱顺序） */
    List<Map<String, Object>> getAllPairs(Long categoryId);

    /** 提交一局对战结果 */
    boolean submitBattle(Long userId, Long categoryId, Long pairId, String chosen);

    /** 获取用户在某个分类的结果+称号 */
    Map<String, Object> getUserResult(Long userId, Long categoryId);
}
