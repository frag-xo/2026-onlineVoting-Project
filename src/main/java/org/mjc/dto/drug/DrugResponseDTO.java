package org.mjc.dto.drug;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 药品响应数据
 * 
 * @author System
 * @since 2026-07-13
 */
@Data
@Schema(description = "药品响应数据")
public class DrugResponseDTO {

    @Schema(description = "药品ID", example = "12650466")
    private Long drugId;

    @Schema(description = "药品名称", example = "复方感冒灵颗粒(双蚁)")
    private String drugName;

    @Schema(description = "药品成分信息")
    private String drugInfo;

    @Schema(description = "药品功能主治")
    private String drugEffect;

    @Schema(description = "药品图片URL")
    private String drugImg;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "发布者")
    private String publisher;

    @Schema(description = "是否包含图片")
    private Boolean hasImage;

    @Schema(description = "药品简称")
    private String shortName;
}