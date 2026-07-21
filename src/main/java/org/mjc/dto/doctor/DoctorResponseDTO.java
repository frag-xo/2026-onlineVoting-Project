package org.mjc.dto.doctor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 医生响应数据
 */
@Data
@Schema(description = "医生响应数据")
public class DoctorResponseDTO {

    @Schema(description = "医生ID", example = "1")
    private Long id;

    @Schema(description = "医生姓名", example = "张三")
    private String name;

    @Schema(description = "年龄", example = "35")
    private Integer age;

    @Schema(description = "性别：1男，2女", example = "1")
    private Integer sex;

    @Schema(description = "性别描述", example = "男")
    private String sexDesc;

    @Schema(description = "医师级别id", example = "1")
    private Long levelId;

    @Schema(description = "联系方式", example = "13800138000")
    private String phone;

    @Schema(description = "诊治类别id", example = "1")
    private Long typeId;

    @Schema(description = "所属医院", example = "市人民医院")
    private String hospital;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "账号id", example = "1001")
    private Long accountId;
}
