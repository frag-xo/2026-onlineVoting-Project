package org.mjc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mjc.dto.DTO;
import org.mjc.entity.Account;
import org.mjc.entity.Vote;
import org.mjc.entity.VoteRecord;
import org.mjc.exception.BusinessException;
import org.mjc.exception.ErrorCode;
import org.mjc.service.AccountService;
import org.mjc.service.OnlineUserService;
import org.mjc.service.VoteRecordService;
import org.mjc.service.VoteService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Tag(name = "管理员", description = "管理员专用接口（数据看板、用户管理）")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Resource
    private VoteService voteService;

    @Resource
    private AccountService accountService;

    @Resource
    private VoteRecordService voteRecordService;

    @Resource
    private OnlineUserService onlineUserService;

    // ==================== 数据看板 ====================

    @Operation(summary = "数据看板", description = "获取系统统计数据")
    @GetMapping("/dashboard")
    public DTO<Map<String, Object>> getDashboard() {
        // 投票总数
        long voteCount = voteService.count();

        // 用户总数
        long userCount = accountService.count();

        // 投票参与总人次
        long recordCount = voteRecordService.count();

        // 进行中的投票数
        LambdaQueryWrapper<Vote> voteWrapper = new LambdaQueryWrapper<>();
        voteWrapper.eq(Vote::getStatus, 1);
        long ongoingVoteCount = voteService.count(voteWrapper);

        // 已结束的投票数
        voteWrapper = new LambdaQueryWrapper<>();
        voteWrapper.eq(Vote::getStatus, 2);
        long endedVoteCount = voteService.count(voteWrapper);

        // 在线用户数
        int onlineUserCount = onlineUserService.getOnlineCount();

        // 封装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("voteCount", voteCount);
        result.put("userCount", userCount);
        result.put("recordCount", recordCount);
        result.put("ongoingVoteCount", ongoingVoteCount);
        result.put("endedVoteCount", endedVoteCount);
        result.put("onlineUserCount", onlineUserCount);

        DTO<Map<String, Object>> dto = new DTO<>(200, "查询成功");
        dto.setT(result);
        return dto;
    }

    @Operation(summary = "投票趋势数据", description = "获取投票参与趋势数据，用于图表展示")
    @GetMapping("/trend")
    public DTO<Map<String, Object>> getTrend() {
        // 获取所有投票
        List<Vote> allVotes = voteService.getAllVotes();

        // 柱状图数据：各投票的参与人数
        List<String> voteTitles = new ArrayList<>();
        List<Long> voteCounts = new ArrayList<>();
        for (Vote vote : allVotes) {
            // 截断标题，最多6个字符
            String title = vote.getTitle();
            if (title.length() > 6) {
                title = title.substring(0, 6) + "...";
            }
            voteTitles.add(title);

            // 统计该投票的参与人数
            Map<Long, Long> countMap = voteRecordService.countByOptionId(vote.getId());
            long totalCount = countMap.values().stream().mapToLong(Long::longValue).sum();
            voteCounts.add(totalCount);
        }

        // 折线图数据：按创建时间排序的投票参与趋势
        List<String> trendLabels = new ArrayList<>();
        List<Long> trendValues = new ArrayList<>();
        // 按创建时间正序（最早的在前）
        allVotes.sort((a, b) -> {
            if (a.getCreateTime() == null) return 1;
            if (b.getCreateTime() == null) return -1;
            return a.getCreateTime().compareTo(b.getCreateTime());
        });
        for (Vote vote : allVotes) {
            String label = vote.getTitle();
            if (label.length() > 6) {
                label = label.substring(0, 6) + "...";
            }
            trendLabels.add(label);

            Map<Long, Long> countMap = voteRecordService.countByOptionId(vote.getId());
            long totalCount = countMap.values().stream().mapToLong(Long::longValue).sum();
            trendValues.add(totalCount);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("voteTitles", voteTitles);
        result.put("voteCounts", voteCounts);
        result.put("trendLabels", trendLabels);
        result.put("trendValues", trendValues);

        DTO<Map<String, Object>> dto = new DTO<>(200, "查询成功");
        dto.setT(result);
        return dto;
    }

    // ==================== 用户管理 ====================

    @Operation(summary = "用户列表", description = "分页查询所有用户")
    @GetMapping("/user/list")
    public DTO<Page<Account>> getUserList(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "用户名（模糊查询）")
            @RequestParam(required = false) String uname) {

        Page<Account> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();

        // 显式过滤已删除的用户（因已移除@TableLogic注解）
        wrapper.eq(Account::getDeleted, 0);

        if (uname != null && !uname.isEmpty()) {
            wrapper.like(Account::getUname, uname);
        }

        wrapper.orderByDesc(Account::getCreateTime);
        Page<Account> result = accountService.page(page, wrapper);

        // 清除密码
        result.getRecords().forEach(account -> account.setPwd(null));

        DTO<Page<Account>> dto = new DTO<>(200, "查询成功");
        dto.setT(result);
        return dto;
    }

    @Operation(summary = "修改用户角色", description = "修改用户角色（ROLE_1管理员、ROLE_3普通用户）")
    @PutMapping("/user/role")
    public DTO<Void> updateUserRole(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "新角色：ROLE_1管理员、ROLE_3普通用户", required = true)
            @RequestParam String utype) throws BusinessException {

        Account account = accountService.getById(userId);
        if (account == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 验证角色值
        if (!"ROLE_1".equals(utype) && !"ROLE_3".equals(utype)) {
            throw new BusinessException(400, "无效的角色值，只能是 ROLE_1 或 ROLE_3");
        }

        account.setUtype(utype);
        account.setUpdateTime(java.time.LocalDateTime.now());
        boolean result = accountService.updateById(account);
        if (!result) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }

        DTO<Void> dto = new DTO<>(200, "修改角色成功");
        return dto;
    }

    @Operation(summary = "禁用/启用用户", description = "禁用或启用用户")
    @PutMapping("/user/status")
    public DTO<Void> updateUserStatus(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "状态：0-禁用，1-启用", required = true)
            @RequestParam Integer status) throws BusinessException {

        Account account = accountService.getById(userId);
        if (account == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 使用deleted字段表示状态：0-启用，1-禁用
        account.setDeleted(status == 0 ? 1 : 0);
        account.setUpdateTime(java.time.LocalDateTime.now());
        boolean result = accountService.updateById(account);
        if (!result) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }

        DTO<Void> dto = new DTO<>(200, status == 1 ? "启用成功" : "禁用成功");
        return dto;
    }

    @Operation(summary = "删除用户", description = "删除用户（逻辑删除）")
    @DeleteMapping("/user/{id}")
    public DTO<Void> deleteUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id) throws BusinessException {

        Account account = accountService.getById(id);
        if (account == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        boolean result = accountService.removeById(id);
        if (!result) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }

        DTO<Void> dto = new DTO<>(200, "删除成功");
        return dto;
    }
}
