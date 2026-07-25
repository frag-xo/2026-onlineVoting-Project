package org.mjc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.mjc.entity.Account;
import org.mjc.mapper.AccountMapper;
import org.mjc.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 账号服务实现类
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Override
    public Account register(String uname, String pwd, String realname, String phoneNumber) {
        // 参数校验
        if (!StringUtils.hasText(uname) || !StringUtils.hasText(pwd)) {
            log.warn("注册失败：用户名或密码为空");
            return null;
        }

        // 检查用户名是否已存在
        if (isUnameExists(uname)) {
            log.warn("注册失败：用户名已存在 - {}", uname);
            return null;
        }

        // 创建账号
        Account account = new Account();
        account.setUname(uname);
        account.setPwd(pwd);
        account.setRealname(realname);
        account.setPhoneNumber(phoneNumber);
        account.setUtype("ROLE_3"); // 默认投票参与人员
        account.setLevel(1); // 默认等级1
        account.setCreateTime(LocalDateTime.now());
        account.setUpdateTime(LocalDateTime.now());

        boolean result = this.save(account);
        if (result) {
            log.info("注册成功: {} (ID: {})", uname, account.getId());
            return account;
        } else {
            log.warn("注册失败: {}", uname);
            return null;
        }
    }

    @Override
    public Account login(String uname, String pwd) {
        if (!StringUtils.hasText(uname) || !StringUtils.hasText(pwd)) {
            log.warn("登录失败：用户名或密码为空");
            return null;
        }

        Account account = baseMapper.selectByUnameAndPwd(uname, pwd);
        if (account == null) {
            log.warn("登录失败：用户名或密码错误 - {}", uname);
            return null;
        }

        // 检查账号是否被禁用（deleted=1表示禁用）
        if (account.getDeleted() != null && account.getDeleted() == 1) {
            log.warn("登录失败：账号已被禁用 - {}", uname);
            return null;
        }

        log.info("登录成功: {} (ID: {})", uname, account.getId());
        return account;
    }

    @Override
    public Account getByUname(String uname) {
        if (!StringUtils.hasText(uname)) {
            return null;
        }
        return baseMapper.selectByUname(uname);
    }

    @Override
    public boolean isUnameExists(String uname) {
        if (!StringUtils.hasText(uname)) {
            return false;
        }
        return getByUname(uname) != null;
    }

    // ==================== 管理员用户管理 ====================

    @Override
    public List<Account> getAllUsers() {
        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Account::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public boolean toggleUserStatus(Long userId) {
        if (userId == null) {
            return false;
        }
        Account account = this.getById(userId);
        if (account == null) {
            log.warn("切换用户状态失败：用户不存在 - userId={}", userId);
            return false;
        }
        // 切换禁用状态：0→启用，1→禁用
        int newStatus = (account.getDeleted() == null || account.getDeleted() == 0) ? 1 : 0;
        account.setDeleted(newStatus);
        account.setUpdateTime(LocalDateTime.now());
        boolean result = this.updateById(account);
        log.info("切换用户状态: userId={}, {}→{}", userId,
                newStatus == 1 ? "启用" : "禁用",
                newStatus == 1 ? "禁用" : "启用");
        return result;
    }

    @Override
    public boolean updateUserRole(Long userId, String utype) {
        if (userId == null || !StringUtils.hasText(utype)) {
            return false;
        }
        if (!"ROLE_1".equals(utype) && !"ROLE_3".equals(utype)) {
            log.warn("修改角色失败：无效的角色类型 - {}", utype);
            return false;
        }
        Account account = this.getById(userId);
        if (account == null) {
            log.warn("修改角色失败：用户不存在 - userId={}", userId);
            return false;
        }
        account.setUtype(utype);
        account.setUpdateTime(LocalDateTime.now());
        boolean result = this.updateById(account);
        log.info("修改用户角色: userId={}, role={}", userId, utype);
        return result;
    }

    // ==================== 随机数据生成 ====================

    private static final String[] SURNAMES = {"张", "李", "王", "刘", "陈", "杨", "赵", "黄", "周", "吴"};
    private static final String[] NAMES = {"伟", "芳", "娜", "敏", "静", "强", "磊", "洋", "勇", "军"};
    private static final String[] PHONES = {"138", "139", "150", "151", "152", "186", "187", "188"};

    private final java.security.SecureRandom random = new java.security.SecureRandom();

    @Override
    public List<Account> generateRandomAccounts(int count) {
        List<Account> accountList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < count; i++) {
            Account account = new Account();

            // 生成用户名
            String uname = "user" + (1000 + i);
            account.setUname(uname);

            // 生成密码
            account.setPwd("123456");

            // 生成真实姓名
            String surname = SURNAMES[random.nextInt(SURNAMES.length)];
            String name = NAMES[random.nextInt(NAMES.length)];
            account.setRealname(surname + name);

            // 生成手机号
            String phone = PHONES[random.nextInt(PHONES.length)];
            for (int j = 0; j < 8; j++) {
                phone += random.nextInt(10);
            }
            account.setPhoneNumber(phone);

            // 全部生成普通用户
            account.setUtype("ROLE_3");

            // 设置时间
            account.setCreateTime(now.minusDays(random.nextInt(30)));
            account.setUpdateTime(now);

            accountList.add(account);
        }

        // 批量插入
        this.saveBatch(accountList);
        log.info("成功生成 {} 条随机用户数据", count);
        return accountList;
    }
}
