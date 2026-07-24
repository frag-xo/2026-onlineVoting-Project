package org.mjc.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 头像服务接口
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
public interface AvatarService {

    /**
     * 上传头像
     *
     * @param file 图片文件
     * @param userId 用户ID
     * @return 头像URL
     */
    String uploadAvatar(MultipartFile file, Long userId);

    /**
     * 删除头像
     *
     * @param avatarUrl 头像URL
     */
    void deleteAvatar(String avatarUrl);
}
