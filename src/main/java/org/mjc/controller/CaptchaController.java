package org.mjc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mjc.dto.DTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码控制器
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Tag(name = "验证码", description = "图形验证码相关接口")
@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int CODE_LENGTH = 4;
    private static final String CAPTCHA_PREFIX = "captcha:";
    private static final long CAPTCHA_EXPIRE_MINUTES = 5;

    private final Random random = new Random();

    @Operation(summary = "生成验证码", description = "生成图形验证码，返回验证码ID和图片Base64")
    @GetMapping("/generate")
    public DTO<Map<String, String>> generateCaptcha() {
        // 生成验证码ID
        String captchaId = UUID.randomUUID().toString().replace("-", "");

        // 生成验证码答案
        String code = generateCode();

        // 存储到Redis（5分钟过期）
        stringRedisTemplate.opsForValue().set(
                CAPTCHA_PREFIX + captchaId,
                code.toLowerCase(),
                CAPTCHA_EXPIRE_MINUTES,
                TimeUnit.MINUTES
        );

        // 生成验证码图片的Base64
        String base64 = generateCaptchaImage(code);

        Map<String, String> result = new HashMap<>();
        result.put("captchaId", captchaId);
        result.put("image", "data:image/png;base64," + base64);

        DTO<Map<String, String>> dto = new DTO<>(200, "生成成功");
        dto.setT(result);
        return dto;
    }

    /**
     * 验证验证码
     *
     * @param captchaId 验证码ID
     * @param code 用户输入的验证码
     * @return 是否正确
     */
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

    /**
     * 生成随机验证码
     */
    private String generateCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 生成验证码图片的Base64
     */
    private String generateCaptchaImage(String code) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 设置字体
        g.setFont(new Font("Arial", Font.BOLD, 24));

        // 绘制干扰线
        for (int i = 0; i < 5; i++) {
            g.setColor(getRandomColor());
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        // 绘制验证码
        for (int i = 0; i < code.length(); i++) {
            g.setColor(getRandomColor());
            g.drawString(String.valueOf(code.charAt(i)), 25 * i + 10, 28);
        }

        g.dispose();

        // 转换为Base64
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return java.util.Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * 获取随机颜色
     */
    private Color getRandomColor() {
        return new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200));
    }
}
