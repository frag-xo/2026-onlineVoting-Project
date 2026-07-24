package org.mjc.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知实体类
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vote_notification")
@Schema(description = "通知")
public class VoteNotification implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "通知ID")
    private Long id;

    @TableField("user_id")
    @Schema(description = "接收用户ID")
    private Long userId;

    @TableField("type")
    @Schema(description = "通知类型：vote-投票通知，comment-评论通知，system-系统通知")
    private String type;

    @TableField("title")
    @Schema(description = "通知标题")
    private String title;

    @TableField("content")
    @Schema(description = "通知内容")
    private String content;

    @TableField("vote_id")
    @Schema(description = "关联投票ID")
    private Long voteId;

    @TableField("is_read")
    @Schema(description = "是否已读：0-未读，1-已读")
    private Integer isRead;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
