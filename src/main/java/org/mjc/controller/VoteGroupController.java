package org.mjc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mjc.dto.DTO;
import org.mjc.entity.VoteGroup;
import org.mjc.exception.BusinessException;
import org.mjc.exception.ErrorCode;
import org.mjc.service.VoteGroupService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 投票分组接口
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
@Tag(name = "投票分组", description = "投票分组管理相关接口")
@RestController
@RequestMapping("/api/vote-group")
public class VoteGroupController {

    @Resource
    private VoteGroupService voteGroupService;

    @Operation(summary = "获取所有分组", description = "获取所有投票分组")
    @GetMapping("/list")
    public DTO<List<VoteGroup>> getAllGroups() {
        List<VoteGroup> groups = voteGroupService.getAllGroups();
        DTO<List<VoteGroup>> dto = new DTO<>(200, "查询成功");
        dto.setT(groups);
        return dto;
    }

    @Operation(summary = "创建分组", description = "创建新的投票分组")
    @PostMapping
    public DTO<VoteGroup> createGroup(
            @Parameter(description = "分组名称", required = true)
            @RequestParam String name,
            @Parameter(description = "分组描述")
            @RequestParam(required = false) String description,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        Long userId = (Long) request.getAttribute("currentUserId");
        VoteGroup group = voteGroupService.createGroup(name, description, userId);
        if (group == null) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "创建分组失败");
        }
        DTO<VoteGroup> dto = new DTO<>(200, "创建成功");
        dto.setT(group);
        return dto;
    }

    @Operation(summary = "更新分组", description = "更新投票分组信息")
    @PutMapping("/{id}")
    public DTO<VoteGroup> updateGroup(
            @Parameter(description = "分组ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "分组名称", required = true)
            @RequestParam String name,
            @Parameter(description = "分组描述")
            @RequestParam(required = false) String description) throws BusinessException {
        VoteGroup group = voteGroupService.updateGroup(id, name, description);
        if (group == null) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "更新分组失败");
        }
        DTO<VoteGroup> dto = new DTO<>(200, "更新成功");
        dto.setT(group);
        return dto;
    }

    @Operation(summary = "删除分组", description = "删除投票分组")
    @DeleteMapping("/{id}")
    public DTO<Void> deleteGroup(
            @Parameter(description = "分组ID", required = true)
            @PathVariable Long id) throws BusinessException {
        boolean result = voteGroupService.deleteGroup(id);
        if (!result) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "删除分组失败");
        }
        return new DTO<>(200, "删除成功");
    }
}
