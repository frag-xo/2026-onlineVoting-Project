package org.mjc.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 投票收藏实体类
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vote_favorite")
@Schema(description = "投票收藏")
public class VoteFavorite implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "收藏ID")
    private Long id;

    @TableField("vote_id")
    @Schema(description = "投票ID")
    private Long voteId;

    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "收藏时间")
    private LocalDateTime createTime;
}
