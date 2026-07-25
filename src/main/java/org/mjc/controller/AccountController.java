package org.mjc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mjc.dto.DTO;
import org.mjc.entity.Account;
import org.mjc.exception.BusinessException;
import org.mjc.exception.ErrorCode;
import org.mjc.service.AccountService;
import org.mjc.service.AvatarService;
import org.mjc.service.OnlineUserService;
import org.mjc.service.UserPointsService;
import org.mjc.utils.JwtUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Resource
    private OnlineUserService onlineUserService;

    @Resource
    private AvatarService avatarService;

    @Resource
    private UserPointsService userPointsService;

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
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }

        Account account = accountService.register(uname, pwd, realname, phoneNumber);
        if (account == null) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }

        // 初始化用户积分（注册+20）
        userPointsService.initUserPoints(account.getId());
        userPointsService.addPoints(account.getId(), 20, "register", "初次注册奖励");

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
            throw new BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 生成 token
        String token = jwtUtils.generateToken(account.getId(), account.getUname(), account.getUtype());

        // 设置用户上线
        onlineUserService.userOnline(account.getId(), account.getUname());

        // 每日登录积分（+10）
        userPointsService.addDailyLoginPoints(account.getId());

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
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        Account account = accountService.getById(id);
        if (account == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 返回时不包含密码
        account.setPwd(null);
        DTO<Account> dto = new DTO<>(200, "查询成功");
        dto.setT(account);
        return dto;
    }

    @Operation(summary = "用户登出", description = "用户登出，设置为离线状态")
    @PostMapping("/logout")
    public DTO<Void> logout(jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        // 从 token 中获取当前用户ID
        Long userId = (Long) request.getAttribute("currentUserId");
        if (userId != null) {
            onlineUserService.userOffline(userId);
        }
        return new DTO<>(200, "登出成功");
    }

    @Operation(summary = "修改用户信息", description = "修改用户信息（只能改自己）")
    @PutMapping
    public DTO<Account> updateAccount(
            @RequestBody Account account,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        if (account.getId() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "用户ID不能为空");
        }

        // 从 token 中获取当前用户ID
        Long currentUserId = (Long) request.getAttribute("currentUserId");

        // 只能改自己
        if (!account.getId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // 不允许修改密码和角色
        account.setPwd(null);
        account.setUtype(null);
        account.setUpdateTime(java.time.LocalDateTime.now());

        boolean result = accountService.updateById(account);
        if (!result) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "修改失败");
        }

        Account updated = accountService.getById(account.getId());
        updated.setPwd(null);
        DTO<Account> dto = new DTO<>(200, "修改成功");
        dto.setT(updated);
        return dto;
    }

    @Operation(summary = "修改密码", description = "修改当前用户的密码")
    @PutMapping("/password")
    public DTO<Void> updatePassword(
            @Parameter(description = "旧密码", required = true)
            @RequestParam String oldPwd,
            @Parameter(description = "新密码", required = true)
            @RequestParam String newPwd,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        Long userId = (Long) request.getAttribute("currentUserId");

        Account account = accountService.getById(userId);
        if (account == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 验证旧密码
        if (!account.getPwd().equals(oldPwd)) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }

        // 更新密码
        account.setPwd(newPwd);
        account.setUpdateTime(java.time.LocalDateTime.now());
        boolean result = accountService.updateById(account);
        if (!result) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "修改密码失败");
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

    // ==================== 头像和用户信息修改 ====================

    @Operation(summary = "上传头像", description = "上传用户头像（支持jpg/png/gif/bmp/webp，自动裁剪为圆形）")
    @PostMapping("/avatar")
    public DTO<Map<String, String>> uploadAvatar(
            @Parameter(description = "头像图片文件", required = true)
            @RequestParam("file") MultipartFile file,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        Long userId = (Long) request.getAttribute("currentUserId");
        String avatarUrl = avatarService.uploadAvatar(file, userId);

        // 更新用户头像
        Account account = accountService.getById(userId);
        if (account != null) {
            // 删除旧头像
            if (account.getAvatar() != null) {
                avatarService.deleteAvatar(account.getAvatar());
            }
            account.setAvatar(avatarUrl);
            account.setUpdateTime(java.time.LocalDateTime.now());
            accountService.updateById(account);
        }

        Map<String, String> result = new HashMap<>();
        result.put("avatar", avatarUrl);
        DTO<Map<String, String>> dto = new DTO<>(200, "头像上传成功");
        dto.setT(result);
        return dto;
    }

    @Operation(summary = "修改用户名", description = "修改当前用户的用户名")
    @PutMapping("/username")
    public DTO<Void> updateUsername(
            @Parameter(description = "新用户名", required = true)
            @RequestParam String newUsername,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        Long userId = (Long) request.getAttribute("currentUserId");

        // 检查新用户名是否已存在
        if (accountService.isUnameExists(newUsername)) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }

        Account account = accountService.getById(userId);
        if (account == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        account.setUname(newUsername);
        account.setUpdateTime(java.time.LocalDateTime.now());
        boolean result = accountService.updateById(account);
        if (!result) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "修改用户名失败");
        }

        return new DTO<>(200, "用户名修改成功");
    }

    // ==================== 管理员用户管理 ====================

    @Operation(summary = "获取用户列表（管理员）", description = "获取所有用户列表，按注册时间倒序")
    @GetMapping("/list")
    public DTO<List<Account>> getUserList(jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        Long userId = (Long) request.getAttribute("currentUserId");
        Account currentUser = accountService.getById(userId);
        if (currentUser == null || !"ROLE_1".equals(currentUser.getUtype())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "仅管理员可操作");
        }
        List<Account> users = accountService.getAllUsers();
        users.forEach(u -> u.setPwd(null));
        DTO<List<Account>> dto = new DTO<>(200, "查询成功");
        dto.setT(users);
        return dto;
    }

    @Operation(summary = "切换用户状态（管理员）", description = "启用或禁用指定用户账号")
    @PutMapping("/{id}/status")
    public DTO<Void> toggleStatus(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        Long userId = (Long) request.getAttribute("currentUserId");
        Account currentUser = accountService.getById(userId);
        if (currentUser == null || !"ROLE_1".equals(currentUser.getUtype())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "仅管理员可操作");
        }
        if (id.equals(userId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "不能禁用自己");
        }
        boolean result = accountService.toggleUserStatus(id);
        if (!result) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "操作失败");
        }
        return new DTO<>(200, "状态已更新");
    }

    @Operation(summary = "修改用户角色（管理员）", description = "修改用户角色为管理员或普通用户")
    @PutMapping("/{id}/role")
    public DTO<Void> updateRole(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Parameter(description = "角色：ROLE_1-管理员，ROLE_3-普通用户", required = true) @RequestParam String utype,
            jakarta.servlet.http.HttpServletRequest request) throws BusinessException {
        Long userId = (Long) request.getAttribute("currentUserId");
        Account currentUser = accountService.getById(userId);
        if (currentUser == null || !"ROLE_1".equals(currentUser.getUtype())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "仅管理员可操作");
        }
        if (id.equals(userId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "不能修改自己的角色");
        }
        boolean result = accountService.updateUserRole(id, utype);
        if (!result) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "修改角色失败");
        }
        return new DTO<>(200, "角色已更新");
    }
}
