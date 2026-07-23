package org.mjc.service.impl;

import org.mjc.service.CaptchaService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {

    private static final String CAPTCHA_PREFIX = "captcha:";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean verifyCaptcha(String captchaId, String code) {
        if (captchaId == null || code == null) {
            return false;
        }

        String key = CAPTCHA_PREFIX + captchaId;
        String storedCode = stringRedisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            return false; // 验证码已过期
        }

        // 验证后删除验证码（一次性使用）
        stringRedisTemplate.delete(key);

        return storedCode.equalsIgnoreCase(code.trim());
    }
}
