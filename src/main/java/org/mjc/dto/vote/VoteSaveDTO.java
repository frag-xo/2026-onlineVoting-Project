package org.mjc.dto.vote;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 投票保存参数DTO
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Data
@Schema(description = "投票保存参数")
public class VoteSaveDTO {

    @Schema(description = "投票ID（修改时必填）")
    private Long id;

    @NotBlank(message = "投票标题不能为空")
    @Size(max = 200, message = "投票标题不能超过200个字符")
    @Schema(description = "投票标题", example = "最喜欢的编程语言", required = true)
    private String title;

    @Size(max = 500, message = "投票描述不能超过500个字符")
    @Schema(description = "投票描述", example = "请选择你最喜欢的编程语言")
    private String description;

    @Schema(description = "状态：0-未开始，1-进行中，2-已结束", example = "1")
    private Integer status;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "创建者ID")
    private Long creatorId;

    @Schema(description = "投票选项列表")
    private List<String> options;

    @Schema(description = "定时发布时间（null表示立即发布）")
    private LocalDateTime publishTime;
}
