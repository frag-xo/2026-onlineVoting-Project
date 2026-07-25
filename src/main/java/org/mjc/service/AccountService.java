package org.mjc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mjc.entity.Account;

import java.util.List;

/**
 * 账号服务接口
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
public interface AccountService extends IService<Account> {

    /**
     * 用户注册
     *
     * @param uname 用户名
     * @param pwd 密码
     * @param realname 真实姓名
     * @param phoneNumber 手机号
     * @return 注册成功的账号
     */
    Account register(String uname, String pwd, String realname, String phoneNumber);

    /**
     * 用户登录
     *
     * @param uname 用户名
     * @param pwd 密码
     * @return 登录成功的账号，失败返回null
     */
    Account login(String uname, String pwd);

    /**
     * 根据用户名查询账号
     *
     * @param uname 用户名
     * @return 账号信息
     */
    Account getByUname(String uname);

    /**
     * 检查用户名是否存在
     *
     * @param uname 用户名
     * @return 是否存在
     */
    boolean isUnameExists(String uname);

    /**
     * 生成随机用户数据
     *
     * @param count 生成数量
     * @return 生成的用户列表
     */
    List<Account> generateRandomAccounts(int count);

    /**
     * 获取所有用户列表（管理员）
     */
    List<Account> getAllUsers();

    /**
     * 切换用户启用/禁用状态（管理员）
     *
     * @param userId 目标用户ID
     * @return 操作后的状态：true-已启用，false-已禁用
     */
    boolean toggleUserStatus(Long userId);

    /**
     * 修改用户角色（管理员）
     *
     * @param userId 目标用户ID
     * @param utype 角色：ROLE_1-管理员，ROLE_3-普通用户
     */
    boolean updateUserRole(Long userId, String utype);
}
