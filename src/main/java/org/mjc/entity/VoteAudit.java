package org.mjc.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 审核记录实体类
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vote_audit")
@Schema(description = "审核记录")
public class VoteAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "审核ID")
    private Long id;

    @TableField("vote_id")
    @Schema(description = "投票ID")
    private Long voteId;

    @TableField("auditor_id")
    @Schema(description = "审核人ID")
    private Long auditorId;

    @TableField("status")
    @Schema(description = "审核状态：0-待审核，1-已通过，2-已拒绝")
    private Integer status;

    @TableField("remark")
    @Schema(description = "审核备注")
    private String remark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
