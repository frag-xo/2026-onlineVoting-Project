package org.mjc.dto.drug;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 药品创建/更新参数
 * 
 * @author System
 * @since 2026-07-13
 */
@Data
@Schema(description = "药品创建/更新参数")
public class DrugSaveDTO {

    @Schema(description = "药品ID（更新时必填）", example = "12650466")
    private Long drugId;

    @NotBlank(message = "药品名称不能为空")
    @Size(max = 255, message = "药品名称长度不能超过255个字符")
    @Schema(description = "药品名称", example = "复方感冒灵颗粒(双蚁)", required = true)
    private String drugName;

    @Schema(description = "药品成分信息", example = "金银花、五指柑、野菊花...")
    private String drugInfo;

    @Schema(description = "药品功能主治", example = "辛凉解表，清热解毒...")
    private String drugEffect;

    @Schema(description = "药品图片URL", example = "http://localhost:8080/image/xxx.jpg")
    private String drugImg;

    @Schema(description = "发布者", example = "管理员")
    private String publisher;
}