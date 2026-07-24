package org.mjc.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 转盘奖品实体类
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wheel_prize")
@Schema(description = "转盘奖品")
public class WheelPrize implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "奖品ID")
    private Long id;

    @TableField("name")
    @Schema(description = "奖品名称")
    private String name;

    @TableField("points")
    @Schema(description = "奖品积分")
    private Integer points;

    @TableField("probability")
    @Schema(description = "中奖概率（百分比）")
    private BigDecimal probability;

    @TableField("color")
    @Schema(description = "显示颜色")
    private String color;

    @TableField("icon")
    @Schema(description = "图标")
    private String icon;

    @TableField("is_active")
    @Schema(description = "是否启用")
    private Boolean isActive;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
