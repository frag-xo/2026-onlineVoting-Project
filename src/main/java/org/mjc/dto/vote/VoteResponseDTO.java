package org.mjc.dto.vote;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 投票响应DTO
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Data
@Schema(description = "投票响应数据")
public class VoteResponseDTO {

    @Schema(description = "投票ID")
    private Long id;

    @Schema(description = "投票标题")
    private String title;

    @Schema(description = "投票描述")
    private String description;

    @Schema(description = "状态：0-未开始，1-进行中，2-已结束")
    private Integer status;

    @Schema(description = "状态文本")
    private String statusText;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "创建者ID")
    private Long creatorId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    // ========== 计算字段 ==========

    @Schema(description = "是否已开始")
    private Boolean isStarted;

    @Schema(description = "是否已结束")
    private Boolean isEnded;

    @Schema(description = "是否进行中")
    private Boolean isOngoing;

    @Schema(description = "投票选项列表")
    private List<Map<String, Object>> options;

    @Schema(description = "总投票人数")
    private Long totalVoters;

    @Schema(description = "简短标题（超过20字符显示省略号）")
    private String shortTitle;
}
