package org.mjc.dto.doctor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 医生查询参数
 */
@Data
@Schema(description = "医生查询参数")
public class DoctorQueryDTO {

    @Schema(description = "医生姓名（模糊查询）", example = "张")
    private String name;

    @Schema(description = "所属医院（模糊查询）", example = "人民医院")
    private String hospital;

    @Schema(description = "医师级别id", example = "1")
    private Long levelId;

    @Schema(description = "诊治类别id", example = "1")
    private Long typeId;

    @Schema(description = "性别：1男，2女", example = "1")
    private Integer sex;

    @Schema(description = "联系方式", example = "13800138000")
    private String phone;

    @Schema(description = "当前页码", example = "1", defaultValue = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "10", defaultValue = "10")
    private Integer pageSize = 10;
}
