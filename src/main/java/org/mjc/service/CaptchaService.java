package org.mjc.service;

/**
 * 验证码服务接口
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
public interface CaptchaService {

    /**
     * 验证验证码
     *
     * @param captchaId 验证码ID
     * @param code 用户输入的验证码
     * @return 是否正确
     */
    boolean verifyCaptcha(String captchaId, String code);
}
