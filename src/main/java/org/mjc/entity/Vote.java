package org.mjc.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 投票实体类
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vote")
@Schema(description = "投票信息")
public class Vote implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 投票ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "投票ID")
    private Long id;

    /**
     * 投票标题
     */
    @TableField("title")
    @Schema(description = "投票标题")
    private String title;

    /**
     * 投票描述
     */
    @TableField("description")
    @Schema(description = "投票描述")
    private String description;

    /**
     * 状态：0-未开始，1-进行中，2-已结束
     */
    @TableField("status")
    @Schema(description = "状态：0-未开始，1-进行中，2-已结束")
    private Integer status;

    /**
     * 开始时间
     */
    @TableField("start_time")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    /**
     * 创建者ID
     */
    @TableField("creator_id")
    @Schema(description = "创建者ID")
    private Long creatorId;

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
     */
    @TableLogic
    @TableField("deleted")
    @Schema(description = "逻辑删除：0-未删，1-已删")
    private Integer deleted;
}
