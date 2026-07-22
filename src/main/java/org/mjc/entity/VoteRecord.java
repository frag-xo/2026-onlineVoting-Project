package org.mjc.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 投票记录实体类
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vote_record")
@Schema(description = "投票记录")
public class VoteRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "记录ID")
    private Long id;

    /**
     * 投票ID
     */
    @TableField("vote_id")
    @Schema(description = "投票ID")
    private Long voteId;

    /**
     * 选项ID
     */
    @TableField("option_id")
    @Schema(description = "选项ID")
    private Long optionId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 投票时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "投票时间")
    private LocalDateTime createTime;

    /**
     * 逻辑删除：0-未删，1-已删
     */
    @TableLogic
    @TableField("deleted")
    @Schema(description = "逻辑删除：0-未删，1-已删")
    private Integer deleted;
}
