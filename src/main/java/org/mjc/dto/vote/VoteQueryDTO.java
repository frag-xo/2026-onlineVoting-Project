package org.mjc.dto.vote;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 投票查询参数DTO
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Data
@Schema(description = "投票查询参数")
public class VoteQueryDTO {

    @Schema(description = "当前页码", example = "1")
    private Integer pageNum;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize;

    @Schema(description = "投票标题（模糊查询）")
    private String title;

    @Schema(description = "状态：0-未开始，1-进行中，2-已结束")
    private Integer status;

    @Schema(description = "创建者ID")
    private Long creatorId;

    @Schema(description = "开始时间-起")
    private LocalDateTime startTimeBegin;

    @Schema(description = "开始时间-止")
    private LocalDateTime startTimeEnd;

    @Schema(description = "结束时间-起")
    private LocalDateTime endTimeBegin;

    @Schema(description = "结束时间-止")
    private LocalDateTime endTimeEnd;

    @Schema(description = "排序字段：endTime-截止时间，createTime-创建时间", example = "endTime")
    private String orderBy;

    @Schema(description = "排序方式：asc-升序，desc-降序", example = "asc")
    private String orderDirection;
}
