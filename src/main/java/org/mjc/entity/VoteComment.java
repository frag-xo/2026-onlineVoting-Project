package org.mjc.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 投票评论实体类
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vote_comment")
@Schema(description = "投票评论")
public class VoteComment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "评论ID")
    private Long id;

    @TableField("vote_id")
    @Schema(description = "投票ID")
    private Long voteId;

    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    @TableField("content")
    @Schema(description = "评论内容")
    private String content;

    @TableField("parent_id")
    @Schema(description = "父评论ID（支持回复）")
    private Long parentId;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "评论时间")
    private LocalDateTime createTime;

    @TableLogic
    @TableField("deleted")
    @Schema(description = "逻辑删除")
    private Integer deleted;
}
