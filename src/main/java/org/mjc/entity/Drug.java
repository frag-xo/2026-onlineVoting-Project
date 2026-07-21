package org.mjc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 药品实体类
 * 
 * @author System
 * @since 2026-07-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("drug")
public class Drug implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 药品信息表id
     */
    @TableId(value = "drug_id", type = IdType.AUTO)
    private Long drugId;

    /**
     * 药名
     */
    @TableField("drug_name")
    private String drugName;

    /**
     * 药品成分信息
     */
    @TableField("drug_info")
    private String drugInfo;

    /**
     * 药品功能
     */
    @TableField("drug_effect")
    private String drugEffect;

    /**
     * 药品图片url
     */
    @TableField("drug_img")
    private String drugImg;

    /**
     * 创建时间
     */
    @TableField(value = "createtime", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updatetime", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 发布者
     */
    @TableField("publisher")
    private String publisher;
}