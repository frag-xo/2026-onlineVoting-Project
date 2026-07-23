package org.mjc.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 账号实体类
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("account")
@Schema(description = "账号信息")
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 账号ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "账号ID")
    private Long id;

    /**
     * 用户名
     */
    @TableField("uname")
    @Schema(description = "用户名")
    private String uname;

    /**
     * 密码
     */
    @TableField("pwd")
    @Schema(description = "密码")
    private String pwd;

    /**
     * 手机号
     */
    @TableField("phone_number")
    @Schema(description = "手机号")
    private String phoneNumber;

    /**
     * 角色：ROLE_1管理员、ROLE_3普通用户
     */
    @TableField("utype")
    @Schema(description = "角色：ROLE_1管理员、ROLE_3普通用户")
    private String utype;

    /**
     * 真实姓名
     */
    @TableField("realname")
    @Schema(description = "真实姓名")
    private String realname;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除：0-未删，1-已删
     * 不使用@TableLogic注解，手动在SQL中过滤，避免与禁用用户功能冲突
     */
    @TableField("deleted")
    @Schema(description = "逻辑删除：0-未删，1-已删")
    private Integer deleted;
}
