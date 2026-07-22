package org.mjc.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.mjc.service.ShareService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 分享服务实现类
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Slf4j
@Service
public class ShareServiceImpl implements ShareService {

    @Override
    public String generateShareLink(Long voteId, String baseUrl) {
        if (voteId == null || baseUrl == null) {
            return null;
        }
        // 生成投票详情页链接
        return baseUrl + "/vote/detail?id=" + voteId;
    }

    @Override
    public byte[] generateQrCode(String content, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
            return baos.toByteArray();

        } catch (WriterException | IOException e) {
            log.error("生成二维码失败", e);
            return new byte[0];
        }
    }
}
