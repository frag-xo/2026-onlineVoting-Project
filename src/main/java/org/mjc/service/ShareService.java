package org.mjc.service;

/**
 * 分享服务接口
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
public interface ShareService {

    /**
     * 生成投票分享链接
     *
     * @param voteId 投票ID
     * @param baseUrl 基础URL
     * @return 分享链接
     */
    String generateShareLink(Long voteId, String baseUrl);

    /**
     * 生成投票二维码
     *
     * @param content 二维码内容（链接）
     * @param width 宽度
     * @param height 高度
     * @return 二维码图片的字节数组
     */
    byte[] generateQrCode(String content, int width, int height);
}
