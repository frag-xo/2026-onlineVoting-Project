package org.mjc.service;

/**
 * AI 小助手服务接口
 *
 * @author Online_Voting
 * @since 2026-07-25
 */
public interface AIService {

    /**
     * 发送消息给 AI 并获取回复
     *
     * @param userId    用户ID（用于上下文）
     * @param message   用户消息
     * @return AI 回复内容
     */
    String chat(Long userId, String message);
}
