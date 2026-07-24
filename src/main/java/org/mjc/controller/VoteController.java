package org.mjc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.mjc.dto.DTO;
import org.mjc.dto.vote.VoteQueryDTO;
import org.mjc.dto.vote.VoteResponseDTO;
import org.mjc.dto.vote.VoteSaveDTO;
import org.mjc.entity.Vote;
import org.mjc.entity.VoteOption;
import org.mjc.entity.VoteRecord;
import org.mjc.exception.BusinessException;
import org.mjc.exception.ErrorCode;
import org.mjc.service.*;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 投票管理控制器
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Tag(name = "投票管理", description = "投票信息管理相关接口")
@RestController
@RequestMapping("/api/vote")
public class VoteController {

    @Resource
    private VoteService voteService;

    @Resource
    private VoteOptionService voteOptionService;

    @Resource
    private VoteRecordService voteRecordService;

    @Resource
    private CaptchaService captchaService;

    @Resource
    private ExportService exportService;

    @Resource
    private ShareService shareService;

    @Resource
    private VoteAuditService voteAuditService;

    @Resource
    private UserPointsService userPointsService;

    // ==================== 分页查询接口 ====================

    @Operation(summary = "分页查询投票", description = "根据条件分页查询投票列表")
    @PostMapping("/page")
    public DTO<Page<VoteResponseDTO>> queryPage(@RequestBody VoteQueryDTO queryDTO) {
        Page<VoteResponseDTO> page = voteService.queryPageDTO(queryDTO);
        DTO<Page<VoteResponseDTO>> dto = new DTO<>(200, "查询成功");
        dto.setT(page);
        return dto;
    }

    @Operation(summary = "简单分页查询", description = "无条件分页查询所有投票")
    @GetMapping("/page/simple")
    public DTO<Page<VoteResponseDTO>> queryPageSimple(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<VoteResponseDTO> page = voteService.queryPageSimpleDTO(pageNum, pageSize);
        DTO<Page<VoteResponseDTO>> dto = new DTO<>(200, "查询成功");
        dto.setT(page);
        return dto;
    }

    // ==================== 按主键查询接口 ====================

    @Operation(summary = "根据ID查询投票", description = "查询投票详情")
    @GetMapping("/{id}")
    public DTO<VoteResponseDTO> getVoteById(
            @Parameter(description = "投票ID", required = true)
            @PathVariable Long id) throws BusinessException {
        VoteResponseDTO voteDto = voteService.getVoteDTOById(id);
        if (voteDto == null) {
            throw new BusinessException(ErrorCode.VOTE_NOT_FOUND);
        }
        DTO<VoteResponseDTO> dto = new DTO<>(200, "查询成功");
        dto.setT(voteDto);
        return dto;
    }

    @Operation(summary = "查询投票详情（含选项）", description = "查询投票详情及选项列表")
    @GetMapping("/{id}/detail")
    public DTO<Map<String, Object>> getVoteDetail(
            @Parameter(description = "投票ID", required = true)
            @PathVariable Long id) throws BusinessException {
        VoteResponseDTO vote = voteService.getVoteDTOById(id);
        if (vote == null) {
            throw new BusinessException(ErrorCode.VOTE_NOT_FOUND);
        }

        // 查询投票选项
        List<Map<String, Object>> options = voteOptionService.getOptionsWithCountByVoteId(id);

        // 封装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("vote", vote);
        result.put("options", options);

        DTO<Map<String, Object>> dto = new DTO<>(200, "查询成功");
        dto.setT(result);
        return dto;
    }

    // ==================== 新增投票接口 ====================

    @Operation(summary = "新增投票", description = "创建新的投票")
    @PostMapping
    public DTO<Vote> addVote(@Valid @RequestBody VoteSaveDTO saveDTO) throws BusinessException {
        Vote vote = voteService.addVote(saveDTO);
        if (vote == null) {
            throw new BusinessException(ErrorCode.VOTE_CREATE_FAILED);
        }
        DTO<Vote> dto = new DTO<>(200, "新增成功");
        dto.setT(vote);
        return dto;
    }

    // ==================== 修改投票接口 ====================

    @Operation(summary = "修改投票", description = "修改投票信息")
    @PutMapping
    public DTO<Vote> updateVote(@Valid @RequestBody VoteSaveDTO saveDTO) throws BusinessException {
        if (saveDTO.getId() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        Vote vote = voteService.updateVote(saveDTO);
        if (vote == null) {
            throw new BusinessException(ErrorCode.VOTE_UPDATE_FAILED);
        }
        DTO<Vote> dto = new DTO<>(200, "修改成功");
        dto.setT(vote);
        return dto;
    }

    // ==================== 删除投票接口 ====================

    @Operation(summary = "删除投票", description = "根据ID删除投票（逻辑删除）")
    @DeleteMapping("/{id}")
    public DTO<Void> deleteVoteById(
            @Parameter(description = "投票ID", required = true)
            @PathVariable Long id) throws BusinessException {
        boolean result = voteService.deleteVoteById(id);
        if (!result) {
            throw new BusinessException(ErrorCode.VOTE_DELETE_FAILED);
        }
        return new DTO<>(200, "删除成功");
    }

    @Operation(summary = "批量删除投票", description = "批量删除投票（逻辑删除）")
    @DeleteMapping("/batch")
    public DTO<Void> deleteVoteBatch(@RequestBody List<Long> ids) throws BusinessException {
        boolean result = voteService.deleteVoteBatch(ids);
        if (!result) {
            throw new BusinessException(ErrorCode.VOTE_DELETE_FAILED);
        }
        return new DTO<>(200, "批量删除成功");
    }

    // ==================== 投票操作接口 ====================

    @Operation(summary = "用户投票", description = "用户对某个投票进行投票（需要验证码）")
    @PostMapping("/vote")
    public DTO<Void> vote(
            @Parameter(description = "投票ID", required = true)
            @RequestParam Long voteId,
            @Parameter(description = "选项ID", required = true)
            @RequestParam Long optionId,
            @Parameter(description = "验证码ID", required = true)
            @RequestParam String captchaId,
            @Parameter(description = "验证码", required = true)
            @RequestParam String captchaCode,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {

        // 从 token 中获取用户ID
        Long userId = (Long) request.getAttribute("currentUserId");

        // 1. 验证验证码
        if (!captchaService.verifyCaptcha(captchaId, captchaCode)) {
            throw new BusinessException(ErrorCode.CAPTCHA_ERROR);
        }

        // 2. 检查投票是否存在
        Vote vote = voteService.getVoteById(voteId);
        if (vote == null) {
            throw new BusinessException(ErrorCode.VOTE_NOT_FOUND);
        }

        // 3. 检查投票状态
        if (vote.getStatus() != 1) {
            throw new BusinessException(ErrorCode.VOTE_NOT_STARTED);
        }

        // 4. 检查截止时间
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        if (vote.getStartTime() != null && now.isBefore(vote.getStartTime())) {
            throw new BusinessException(ErrorCode.VOTE_NOT_STARTED);
        }
        if (vote.getEndTime() != null && now.isAfter(vote.getEndTime())) {
            throw new BusinessException(ErrorCode.VOTE_ENDED);
        }

        // 5. 执行投票
        boolean result = voteRecordService.vote(voteId, optionId, userId);
        if (!result) {
            throw new BusinessException(ErrorCode.VOTE_ALREADY_VOTED);
        }

        // 6. 投票成功，奖励积分（+5，每天最多20次）
        boolean pointsAdded = userPointsService.addVotePoints(userId);
        String msg = pointsAdded ? "投票成功，获得5积分" : "投票成功";

        return new DTO<>(200, msg);
    }

    @Operation(summary = "查询投票结果", description = "查询投票的统计结果")
    @GetMapping("/result/{id}")
    public DTO<Map<String, Object>> getVoteResult(
            @Parameter(description = "投票ID", required = true)
            @PathVariable Long id) throws BusinessException {
        // 查询投票信息
        VoteResponseDTO vote = voteService.getVoteDTOById(id);
        if (vote == null) {
            throw new BusinessException(ErrorCode.VOTE_NOT_FOUND);
        }

        // 查询投票选项（含票数）
        List<Map<String, Object>> options = voteOptionService.getOptionsWithCountByVoteId(id);

        // 计算总票数
        long totalCount = options.stream()
                .mapToLong(opt -> (Long) opt.getOrDefault("count", 0L))
                .sum();

        // 封装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("vote", vote);
        result.put("options", options);
        result.put("totalCount", totalCount);

        DTO<Map<String, Object>> dto = new DTO<>(200, "查询成功");
        dto.setT(result);
        return dto;
    }

    @Operation(summary = "检查是否已投票", description = "检查用户是否已对某投票投票")
    @GetMapping("/hasVoted")
    public DTO<Boolean> hasVoted(
            @Parameter(description = "投票ID")
            @RequestParam Long voteId,
            jakarta.servlet.http.HttpServletRequest request) {
        // 从 token 中获取用户ID
        Long userId = (Long) request.getAttribute("currentUserId");
        boolean hasVoted = voteRecordService.hasVoted(voteId, userId);
        DTO<Boolean> dto = new DTO<>(200, "查询成功");
        dto.setT(hasVoted);
        return dto;
    }

    // ==================== 结束投票接口 ====================

    @Operation(summary = "结束投票", description = "结束某个投票")
    @PutMapping("/end/{id}")
    public DTO<Void> endVote(
            @Parameter(description = "投票ID", required = true)
            @PathVariable Long id) throws BusinessException {
        boolean result = voteService.endVote(id);
        if (!result) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }
        return new DTO<>(200, "结束成功");
    }

    // ==================== 投票历史统计 ====================

    @Operation(summary = "用户投票历史", description = "获取当前用户的投票历史记录")
    @GetMapping("/history")
    public DTO<List<Map<String, Object>>> getVoteHistory(jakarta.servlet.http.HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");

        // 查询用户的投票记录
        List<VoteRecord> records = voteRecordService.getRecordsByVoteId(null); // 需要修改为按用户查询

        List<Map<String, Object>> history = new java.util.ArrayList<>();
        for (VoteRecord record : records) {
            Vote vote = voteService.getVoteById(record.getVoteId());
            if (vote != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("voteId", vote.getId());
                item.put("voteTitle", vote.getTitle());
                item.put("optionId", record.getOptionId());
                item.put("voteTime", record.getCreateTime());
                history.add(item);
            }
        }

        DTO<List<Map<String, Object>>> dto = new DTO<>(200, "查询成功");
        dto.setT(history);
        return dto;
    }

    // ==================== 投票排行 ====================

    @Operation(summary = "投票排行", description = "获取热门投票排行榜")
    @GetMapping("/ranking")
    public DTO<List<Map<String, Object>>> getVoteRanking(
            @Parameter(description = "排行数量", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        // 获取所有投票
        List<Vote> allVotes = voteService.getAllVotes();

        // 统计每个投票的参与人数
        List<Map<String, Object>> ranking = new java.util.ArrayList<>();
        for (Vote vote : allVotes) {
            Map<Long, Long> countMap = voteRecordService.countByOptionId(vote.getId());
            long totalCount = countMap.values().stream().mapToLong(Long::longValue).sum();

            Map<String, Object> item = new HashMap<>();
            item.put("voteId", vote.getId());
            item.put("title", vote.getTitle());
            item.put("totalVotes", totalCount);
            item.put("status", vote.getStatus());
            item.put("endTime", vote.getEndTime());
            ranking.add(item);
        }

        // 按投票数排序
        ranking.sort((a, b) -> Long.compare((Long) b.get("totalVotes"), (Long) a.get("totalVotes")));

        // 限制数量
        if (ranking.size() > limit) {
            ranking = ranking.subList(0, limit);
        }

        DTO<List<Map<String, Object>>> dto = new DTO<>(200, "查询成功");
        dto.setT(ranking);
        return dto;
    }

    // ==================== 普通用户发布投票（需审核） ====================

    @Operation(summary = "普通用户发布投票", description = "普通用户发布投票（需要管理员审核）")
    @PostMapping("/submit")
    public DTO<Vote> submitVote(
            @Valid @RequestBody VoteSaveDTO saveDTO,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        Long userId = (Long) request.getAttribute("currentUserId");

        // 创建投票
        Vote vote = voteService.addVote(saveDTO);
        if (vote == null) {
            throw new BusinessException(ErrorCode.VOTE_CREATE_FAILED);
        }

        // 设置为待审核状态
        vote.setAuditStatus(0); // 待审核
        vote.setCreatorId(userId);
        voteService.updateById(vote);

        // 创建审核记录
        voteAuditService.createAudit(vote.getId());

        DTO<Vote> dto = new DTO<>(200, "提交成功，等待管理员审核");
        dto.setT(vote);
        return dto;
    }

    // ==================== 随机数据生成接口 ====================

    @Operation(summary = "生成随机投票", description = "生成随机投票数据用于测试")
    @PostMapping("/init/random")
    public DTO<List<Vote>> generateRandomVotes(
            @Parameter(description = "生成数量", example = "10")
            @RequestParam(defaultValue = "10") Integer count) {
        List<Vote> voteList = voteService.generateRandomVotes(count);
        DTO<List<Vote>> dto = new DTO<>(200, "生成成功");
        dto.setT(voteList);
        return dto;
    }

    // ==================== 选项管理接口 ====================

    @Operation(summary = "查询选项", description = "根据ID查询选项")
    @GetMapping("/option/{id}")
    public DTO<VoteOption> getOptionById(
            @Parameter(description = "选项ID", required = true)
            @PathVariable Long id) throws BusinessException {
        VoteOption option = voteOptionService.getOptionById(id);
        if (option == null) {
            throw new BusinessException(ErrorCode.OPTION_NOT_FOUND);
        }
        DTO<VoteOption> dto = new DTO<>(200, "查询成功");
        dto.setT(option);
        return dto;
    }

    @Operation(summary = "查询投票的所有选项", description = "根据投票ID查询所有选项")
    @GetMapping("/option/list/{voteId}")
    public DTO<List<VoteOption>> getOptionsByVoteId(
            @Parameter(description = "投票ID", required = true)
            @PathVariable Long voteId) {
        List<VoteOption> options = voteOptionService.getOptionsByVoteId(voteId);
        DTO<List<VoteOption>> dto = new DTO<>(200, "查询成功");
        dto.setT(options);
        return dto;
    }

    @Operation(summary = "新增选项", description = "为投票新增选项")
    @PostMapping("/option")
    public DTO<VoteOption> addOption(@RequestBody VoteOption option) throws BusinessException {
        if (option.getVoteId() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        option.setCreateTime(java.time.LocalDateTime.now());
        boolean result = voteOptionService.save(option);
        if (!result) {
            throw new BusinessException(ErrorCode.VOTE_CREATE_FAILED);
        }
        DTO<VoteOption> dto = new DTO<>(200, "新增成功");
        dto.setT(option);
        return dto;
    }

    @Operation(summary = "修改选项", description = "修改选项内容")
    @PutMapping("/option")
    public DTO<VoteOption> updateOption(@RequestBody VoteOption option) throws BusinessException {
        if (option.getId() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        boolean result = voteOptionService.updateOption(option);
        if (!result) {
            throw new BusinessException(ErrorCode.VOTE_UPDATE_FAILED);
        }
        VoteOption updated = voteOptionService.getOptionById(option.getId());
        DTO<VoteOption> dto = new DTO<>(200, "修改成功");
        dto.setT(updated);
        return dto;
    }

    @Operation(summary = "删除选项", description = "根据ID删除选项")
    @DeleteMapping("/option/{id}")
    public DTO<Void> deleteOption(
            @Parameter(description = "选项ID", required = true)
            @PathVariable Long id) throws BusinessException {
        boolean result = voteOptionService.deleteOptionById(id);
        if (!result) {
            throw new BusinessException(ErrorCode.VOTE_DELETE_FAILED);
        }
        return new DTO<>(200, "删除成功");
    }

    // ==================== 投票记录管理接口 ====================

    @Operation(summary = "查询投票记录", description = "根据投票ID查询所有投票记录")
    @GetMapping("/record/list/{voteId}")
    public DTO<List<VoteRecord>> getRecordsByVoteId(
            @Parameter(description = "投票ID", required = true)
            @PathVariable Long voteId) {
        List<VoteRecord> records = voteRecordService.getRecordsByVoteId(voteId);
        DTO<List<VoteRecord>> dto = new DTO<>(200, "查询成功");
        dto.setT(records);
        return dto;
    }

    @Operation(summary = "删除投票记录", description = "根据ID删除投票记录")
    @DeleteMapping("/record/{id}")
    public DTO<Void> deleteRecord(
            @Parameter(description = "记录ID", required = true)
            @PathVariable Long id) throws BusinessException {
        boolean result = voteRecordService.deleteRecordById(id);
        if (!result) {
            throw new BusinessException(ErrorCode.VOTE_DELETE_FAILED);
        }
        return new DTO<>(200, "删除成功");
    }

    @Operation(summary = "清空投票记录", description = "根据投票ID清空所有投票记录")
    @DeleteMapping("/record/clear/{voteId}")
    public DTO<Void> clearRecordsByVoteId(
            @Parameter(description = "投票ID", required = true)
            @PathVariable Long voteId) throws BusinessException {
        boolean result = voteRecordService.deleteByVoteId(voteId);
        if (!result) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }
        return new DTO<>(200, "清空成功");
    }

    // ==================== 导出功能接口 ====================

    @Operation(summary = "导出投票结果", description = "导出投票结果为Excel文件")
    @GetMapping("/export/{id}")
    public void exportVoteResult(
            @Parameter(description = "投票ID", required = true)
            @PathVariable Long id,
            jakarta.servlet.http.HttpServletResponse response) throws BusinessException {
        // 查询投票信息
        Vote vote = voteService.getVoteById(id);
        if (vote == null) {
            throw new BusinessException(ErrorCode.VOTE_NOT_FOUND);
        }

        // 查询投票选项（含票数）
        List<Map<String, Object>> options = voteOptionService.getOptionsWithCountByVoteId(id);

        // 计算总票数
        long totalCount = options.stream()
                .mapToLong(opt -> (Long) opt.getOrDefault("count", 0L))
                .sum();

        // 生成Excel
        byte[] excelData = exportService.exportVoteResultToExcel(vote, options, totalCount);

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=vote_result_" + id + ".xlsx");

        try {
            response.getOutputStream().write(excelData);
            response.getOutputStream().flush();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.EXPORT_FAILED);
        }
    }

    // ==================== 分享功能接口 ====================

    @Operation(summary = "生成分享链接", description = "生成投票分享链接")
    @GetMapping("/share/link/{id}")
    public DTO<String> generateShareLink(
            @Parameter(description = "投票ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "基础URL", required = true)
            @RequestParam String baseUrl) throws BusinessException {
        Vote vote = voteService.getVoteById(id);
        if (vote == null) {
            throw new BusinessException(ErrorCode.VOTE_NOT_FOUND);
        }

        String shareLink = shareService.generateShareLink(id, baseUrl);
        DTO<String> dto = new DTO<>(200, "生成成功");
        dto.setT(shareLink);
        return dto;
    }

    @Operation(summary = "生成二维码", description = "生成投票二维码")
    @GetMapping("/share/qrcode/{id}")
    public void generateQrCode(
            @Parameter(description = "投票ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "基础URL", required = true)
            @RequestParam String baseUrl,
            @Parameter(description = "二维码宽度", example = "300")
            @RequestParam(defaultValue = "300") Integer width,
            @Parameter(description = "二维码高度", example = "300")
            @RequestParam(defaultValue = "300") Integer height,
            jakarta.servlet.http.HttpServletResponse response) throws BusinessException {
        Vote vote = voteService.getVoteById(id);
        if (vote == null) {
            throw new BusinessException(ErrorCode.VOTE_NOT_FOUND);
        }

        String shareLink = shareService.generateShareLink(id, baseUrl);
        byte[] qrCodeData = shareService.generateQrCode(shareLink, width, height);

        response.setContentType("image/png");
        response.setHeader("Content-Disposition", "inline; filename=qrcode_" + id + ".png");

        try {
            response.getOutputStream().write(qrCodeData);
            response.getOutputStream().flush();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.QRCODE_GENERATE_FAILED);
        }
    }
}
