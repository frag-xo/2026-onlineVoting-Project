package org.mjc.dto.doctor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 医生创建/更新参数
 */
@Data
@Schema(description = "医生创建/更新参数")
public class DoctorSaveDTO {

    @Schema(description = "医生ID（更新时必填）", example = "1")
    private Long id;

    @NotBlank(message = "医生姓名不能为空")
    @Size(max = 255, message = "医生姓名长度不能超过255个字符")
    @Schema(description = "医生姓名", example = "张三", required = true)
    private String name;

    @Schema(description = "年龄", example = "35")
    private Integer age;

    @Schema(description = "性别：1男，2女", example = "1")
    private Integer sex;

    @Schema(description = "医师级别id", example = "1")
    private Long levelId;

    @Schema(description = "联系方式", example = "13800138000")
    private String phone;

    @Schema(description = "诊治类别id", example = "1")
    private Long typeId;

    @Schema(description = "所属医院", example = "市人民医院")
    private String hospital;

    @Schema(description = "账号id", example = "1001")
    private Long accountId;
}
