package org.mjc.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 投票选项实体类
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vote_option")
@Schema(description = "投票选项")
public class VoteOption implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 选项ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "选项ID")
    private Long id;

    /**
     * 所属投票ID
     */
    @TableField("vote_id")
    @Schema(description = "所属投票ID")
    private Long voteId;

    /**
     * 选项内容
     */
    @TableField("option_text")
    @Schema(description = "选项内容")
    private String optionText;

    /**
     * 排序序号
     */
    @TableField("sort_order")
    @Schema(description = "排序序号")
    private Integer sortOrder;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 逻辑删除：0-未删，1-已删
     */
    @TableLogic
    @TableField("deleted")
    @Schema(description = "逻辑删除：0-未删，1-已删")
    private Integer deleted;
}
