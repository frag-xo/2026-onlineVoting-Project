package org.mjc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mjc.dto.DTO;
import org.mjc.entity.VoteAudit;
import org.mjc.exception.BusinessException;
import org.mjc.exception.ErrorCode;
import org.mjc.service.VoteAuditService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 投票审核接口
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Tag(name = "投票审核", description = "投票审核相关接口")
@RestController
@RequestMapping("/api/vote-audit")
public class VoteAuditController {

    @Resource
    private VoteAuditService voteAuditService;

    @Operation(summary = "获取待审核列表", description = "获取所有待审核的投票")
    @GetMapping("/pending")
    public DTO<List<VoteAudit>> getPendingAudits() {
        List<VoteAudit> audits = voteAuditService.getPendingAudits();
        DTO<List<VoteAudit>> dto = new DTO<>(200, "查询成功");
        dto.setT(audits);
        return dto;
    }

    @Operation(summary = "审核投票", description = "审核投票（通过/拒绝）")
    @PostMapping("/audit")
    public DTO<Void> audit(
            @Parameter(description = "投票ID", required = true)
            @RequestParam Long voteId,
            @Parameter(description = "审核状态：1-通过，2-拒绝", required = true)
            @RequestParam Integer status,
            @Parameter(description = "审核备注")
            @RequestParam(required = false) String remark,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        Long auditorId = (Long) request.getAttribute("currentUserId");
        boolean result = voteAuditService.audit(voteId, auditorId, status, remark);
        if (!result) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "审核失败");
        }
        return new DTO<>(200, status == 1 ? "审核通过" : "审核拒绝");
    }

    @Operation(summary = "获取审核记录", description = "根据投票ID获取审核记录")
    @GetMapping("/vote/{voteId}")
    public DTO<VoteAudit> getAuditByVoteId(
            @Parameter(description = "投票ID", required = true)
            @PathVariable Long voteId) {
        VoteAudit audit = voteAuditService.getByVoteId(voteId);
        DTO<VoteAudit> dto = new DTO<>(200, "查询成功");
        dto.setT(audit);
        return dto;
    }
}
