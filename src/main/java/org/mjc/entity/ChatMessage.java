package org.mjc.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 聊天消息实体类
 *
 * @author Online_Voting
 * @since 2026-07-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("chat_message")
@Schema(description = "聊天消息")
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "消息ID")
    private Long id;

    @TableField("from_user_id")
    @Schema(description = "发送方用户ID")
    private Long fromUserId;

    @TableField("to_user_id")
    @Schema(description = "接收方用户ID")
    private Long toUserId;

    @TableField("content")
    @Schema(description = "消息内容")
    private String content;

    @TableField("is_read")
    @Schema(description = "是否已读：0-未读，1-已读")
    private Integer isRead;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "发送时间")
    private LocalDateTime createTime;
}
