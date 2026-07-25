package org.mjc.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 好友关系实体类
 *
 * @author Online_Voting
 * @since 2026-07-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_friend")
@Schema(description = "好友关系")
public class UserFriend implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    @TableField("user_id")
    @Schema(description = "发起方用户ID")
    private Long userId;

    @TableField("friend_id")
    @Schema(description = "接收方用户ID")
    private Long friendId;

    @TableField("status")
    @Schema(description = "状态：0-待确认，1-已同意，2-已拒绝")
    private Integer status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "申请时间")
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
