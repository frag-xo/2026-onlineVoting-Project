package org.mjc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.mjc.entity.Account;

/**
 * 账号 Mapper 接口
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {

    /**
     * 根据用户名查询账号
     */
    @Select("SELECT * FROM account WHERE uname = #{uname} AND deleted = 0")
    Account selectByUname(@Param("uname") String uname);

    /**
     * 根据用户名和密码查询账号（登录）
     */
    @Select("SELECT * FROM account WHERE uname = #{uname} AND pwd = #{pwd} AND deleted = 0")
    Account selectByUnameAndPwd(@Param("uname") String uname, @Param("pwd") String pwd);
}
