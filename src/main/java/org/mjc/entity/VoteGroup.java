package org.mjc.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 投票分组实体类
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vote_group")
@Schema(description = "投票分组")
public class VoteGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "分组ID")
    private Long id;

    @TableField("name")
    @Schema(description = "分组名称")
    private String name;

    @TableField("description")
    @Schema(description = "分组描述")
    private String description;

    @TableField("creator_id")
    @Schema(description = "创建者ID")
    private Long creatorId;

    @TableField("sort_order")
    @Schema(description = "排序序号")
    private Integer sortOrder;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableLogic
    @TableField("deleted")
    @Schema(description = "逻辑删除")
    private Integer deleted;
}
