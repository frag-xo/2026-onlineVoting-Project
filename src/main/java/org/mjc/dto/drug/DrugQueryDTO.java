package org.mjc.dto.drug;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 药品查询参数
 * 
 * @author System
 * @since 2026-07-13
 */
@Data
@Schema(description = "药品查询参数")
public class DrugQueryDTO {

    @Schema(description = "药品名称（模糊查询）", example = "感冒")
    private String drugName;

    @Schema(description = "药品功能（模糊查询）", example = "清热解毒")
    private String drugEffect;

    @Schema(description = "发布者", example = "管理员")
    private String publisher;

    @Schema(description = "开始时间", example = "2023-06-01 00:00:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2023-06-30 23:59:59")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "是否包含图片", example = "true")
    private Boolean hasImage;

    @Schema(description = "当前页码", example = "1", defaultValue = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "10", defaultValue = "10")
    private Integer pageSize = 10;
}