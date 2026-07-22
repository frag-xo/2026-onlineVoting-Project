package org.mjc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mjc.dto.DTO;
import org.mjc.entity.Account;
import org.mjc.exception.BusinessException;
import org.mjc.service.AccountService;
import org.mjc.utils.JwtUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 账号管理控制器
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Tag(name = "账号管理", description = "用户登录注册相关接口")
@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Resource
    private AccountService accountService;

    @Resource
    private JwtUtils jwtUtils;

    @Operation(summary = "用户注册", description = "注册新用户")
    @PostMapping("/register")
    public DTO<Account> register(
            @Parameter(description = "用户名", required = true)
            @RequestParam String uname,
            @Parameter(description = "密码", required = true)
            @RequestParam String pwd,
            @Parameter(description = "真实姓名")
            @RequestParam(required = false) String realname,
            @Parameter(description = "手机号")
            @RequestParam(required = false) String phoneNumber) throws BusinessException {

        // 检查用户名是否已存在
        if (accountService.isUnameExists(uname)) {
            throw new BusinessException(400, "用户名已存在");
        }

        Account account = accountService.register(uname, pwd, realname, phoneNumber);
        if (account == null) {
            throw new BusinessException(500, "注册失败");
        }

        // 返回时不包含密码
        account.setPwd(null);
        DTO<Account> dto = new DTO<>(200, "注册成功");
        dto.setT(account);
        return dto;
    }

    @Operation(summary = "用户登录", description = "用户登录，返回token")
    @PostMapping("/login")
    public DTO<Map<String, Object>> login(
            @Parameter(description = "用户名", required = true)
            @RequestParam String uname,
            @Parameter(description = "密码", required = true)
            @RequestParam String pwd) throws BusinessException {

        Account account = accountService.login(uname, pwd);
        if (account == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 生成 token
        String token = jwtUtils.generateToken(account.getId(), account.getUname(), account.getUtype());

        // 返回 token 和用户信息
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("id", account.getId());
        result.put("uname", account.getUname());
        result.put("realname", account.getRealname());
        result.put("utype", account.getUtype());

        DTO<Map<String, Object>> dto = new DTO<>(200, "登录成功");
        dto.setT(result);
        return dto;
    }

    @Operation(summary = "查询用户信息", description = "查询用户信息（只能查自己）")
    @GetMapping("/{id}")
    public DTO<Account> getAccountById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {

        // 从 token 中获取当前用户ID
        Long currentUserId = (Long) request.getAttribute("currentUserId");

        // 普通用户只能查自己
        if (!id.equals(currentUserId)) {
            throw new BusinessException(403, "无权查看其他用户信息");
        }

        Account account = accountService.getById(id);
        if (account == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 返回时不包含密码
        account.setPwd(null);
        DTO<Account> dto = new DTO<>(200, "查询成功");
        dto.setT(account);
        return dto;
    }

    @Operation(summary = "修改用户信息", description = "修改用户信息（只能改自己）")
    @PutMapping
    public DTO<Account> updateAccount(
            @RequestBody Account account,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        if (account.getId() == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        // 从 token 中获取当前用户ID
        Long currentUserId = (Long) request.getAttribute("currentUserId");

        // 只能改自己
        if (!account.getId().equals(currentUserId)) {
            throw new BusinessException(403, "无权修改其他用户信息");
        }

        // 不允许修改密码和角色
        account.setPwd(null);
        account.setUtype(null);
        account.setUpdateTime(java.time.LocalDateTime.now());

        boolean result = accountService.updateById(account);
        if (!result) {
            throw new BusinessException(500, "修改失败");
        }

        Account updated = accountService.getById(account.getId());
        updated.setPwd(null);
        DTO<Account> dto = new DTO<>(200, "修改成功");
        dto.setT(updated);
        return dto;
    }

    @Operation(summary = "修改密码", description = "修改用户密码（只能改自己）")
    @PutMapping("/password")
    public DTO<Void> updatePassword(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long id,
            @Parameter(description = "旧密码", required = true)
            @RequestParam String oldPwd,
            @Parameter(description = "新密码", required = true)
            @RequestParam String newPwd,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {

        // 从 token 中获取当前用户ID
        Long currentUserId = (Long) request.getAttribute("currentUserId");

        // 只能改自己
        if (!id.equals(currentUserId)) {
            throw new BusinessException(403, "无权修改其他用户密码");
        }

        Account account = accountService.getById(id);
        if (account == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 验证旧密码
        if (!account.getPwd().equals(oldPwd)) {
            throw new BusinessException(400, "旧密码错误");
        }

        // 更新密码
        account.setPwd(newPwd);
        account.setUpdateTime(java.time.LocalDateTime.now());
        boolean result = accountService.updateById(account);
        if (!result) {
            throw new BusinessException(500, "修改密码失败");
        }

        DTO<Void> dto = new DTO<>(200, "修改密码成功");
        return dto;
    }

    @Operation(summary = "生成随机用户", description = "生成随机用户数据用于测试")
    @PostMapping("/init/random")
    public DTO<List<Account>> generateRandomAccounts(
            @Parameter(description = "生成数量", example = "10")
            @RequestParam(defaultValue = "10") Integer count) {
        List<Account> accountList = accountService.generateRandomAccounts(count);
        DTO<List<Account>> dto = new DTO<>(200, "生成成功");
        dto.setT(accountList);
        return dto;
    }
}
