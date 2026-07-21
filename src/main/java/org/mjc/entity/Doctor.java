package org.mjc.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 医生信息实体
 *
 * @author yourname
 * @since 2026-07-13
 */
@Data
@TableName("doctor")
@Schema(description = "医生信息")
public class Doctor {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "医生ID")
    private Long id;

    @TableField("name")
    @Schema(description = "医生姓名")
    private String name;

    @TableField("age")
    @Schema(description = "年龄")
    private Integer age;

    @TableField("sex")
    @Schema(description = "性别：1男，2女")
    private Integer sex;

    @TableField("level_id")
    @Schema(description = "医师级别id")
    private Long levelId;

    @TableField("phone")
    @Schema(description = "联系方式")
    private String phone;

    @TableField("type_id")
    @Schema(description = "诊治类别id")
    private Long typeId;

    @TableField("hospital")
    @Schema(description = "所属医院")
    private String hospital;

    @TableField(value = "updatetime", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableField(value = "createtime", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField("account_id")
    @Schema(description = "账号id")
    private Long accountId;
}