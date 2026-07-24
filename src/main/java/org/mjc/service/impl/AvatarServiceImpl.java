package org.mjc.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.mjc.exception.BusinessException;
import org.mjc.exception.ErrorCode;
import org.mjc.service.AvatarService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 头像服务实现类
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Slf4j
@Service
public class AvatarServiceImpl implements AvatarService {

    @Value("${file.upload.path:./upload/}")
    private String uploadPath;

    @Value("${file.upload.url:http://localhost:8080/upload/}")
    private String uploadUrl;

    private static final int AVATAR_SIZE = 200; // 头像尺寸
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};

    @Override
    public String uploadAvatar(MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "请选择图片文件");
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isAllowedExtension(originalFilename)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "只支持 jpg、jpeg、png、gif、bmp、webp 格式");
        }

        // 检查文件大小（最大5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "图片大小不能超过5MB");
        }

        try {
            // 创建上传目录
            Path uploadDir = Paths.get(uploadPath, "avatars");
            Files.createDirectories(uploadDir);

            // 生成文件名
            String extension = getFileExtension(originalFilename);
            String fileName = "avatar_" + userId + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
            Path filePath = uploadDir.resolve(fileName);

            // 读取图片并裁剪为圆形
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            BufferedImage circularImage = cropToCircle(originalImage);

            // 保存图片
            ImageIO.write(circularImage, "png", filePath.toFile());

            // 返回访问URL
            String avatarUrl = uploadUrl + "avatars/" + fileName;
            log.info("头像上传成功: userId={}, url={}", userId, avatarUrl);
            return avatarUrl;

        } catch (IOException e) {
            log.error("头像上传失败", e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "头像上传失败");
        }
    }

    @Override
    public void deleteAvatar(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            return;
        }

        try {
            // 从URL提取文件路径
            String fileName = avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadPath, "avatars", fileName);
            Files.deleteIfExists(filePath);
            log.info("头像删除成功: {}", avatarUrl);
        } catch (IOException e) {
            log.warn("头像删除失败: {}", avatarUrl, e);
        }
    }

    /**
     * 裁剪图片为圆形（从中心裁剪）
     */
    private BufferedImage cropToCircle(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();
        int size = Math.min(width, height);

        // 从中心裁剪正方形
        int x = (width - size) / 2;
        int y = (height - size) / 2;
        BufferedImage cropped = original.getSubimage(x, y, size, size);

        // 缩放到目标尺寸
        BufferedImage scaled = new BufferedImage(AVATAR_SIZE, AVATAR_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaled.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(cropped, 0, 0, AVATAR_SIZE, AVATAR_SIZE, null);
        g2d.dispose();

        // 创建圆形蒙版
        BufferedImage circular = new BufferedImage(AVATAR_SIZE, AVATAR_SIZE, BufferedImage.TYPE_INT_ARGB);
        g2d = circular.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 创建圆形裁剪区域
        Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, AVATAR_SIZE, AVATAR_SIZE);
        g2d.setClip(circle);
        g2d.drawImage(scaled, 0, 0, null);

        // 添加圆形边框
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(circle);
        g2d.dispose();

        return circular;
    }

    /**
     * 检查文件扩展名是否允许
     */
    private boolean isAllowedExtension(String fileName) {
        String lowerFileName = fileName.toLowerCase();
        for (String ext : ALLOWED_EXTENSIONS) {
            if (lowerFileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : ".png";
    }
}
