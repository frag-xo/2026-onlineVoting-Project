package org.mjc.utils;


//import org.lg.exception.CustomException;
//import org.lg.exception.NoPermissionExecption;
import org.mjc.exception.BusinessException;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ExceptionUtils {
    static Map<Class,String> exceptionMap = new HashMap<Class,String>();
    static Map<Class,String> redirectExceptionMap = new HashMap<>();
    //如果认识一些系统异常，将异常转换为自定义异常输出，目的是将异常的提示信息换成中文输出
    static{
        //exceptionMap.put(CustomException.class,"");
        exceptionMap.put(MyBatisSystemException.class,"数据库异常请联系管理员");
        exceptionMap.put(CannotGetJdbcConnectionException.class,"数据库异常请联系管理员");
        exceptionMap.put(RecoverableDataAccessException.class,"数据库异常，请联系管理员");

        //redirectExceptionMap.put(NoPermissionExecption.class, "没有登录;login");
		//redirectExceptionMap.put(AuthorizationException.class, "没有登录;info");

    }
    /**
     * 将系统异常转为自定义异常就是�? 我们的方�?可能抛出2种异常一�?其它框架提供的异常（IOException，MyBatisSystemException�?还有我们已经自定义的异常（PWDEXCEPTION 密码错误异常�? 首先判断跑出来的是不是自定异常，尝试转换为自定异�?尝试失败说明�?其它框架提供的异常然后在预先认识的map�?找到�?生成自定义异�?提示信息�?预先设置好的，找不到 也生成自定义异常只不过，提示信息为未知异�?
     * @param ex
     * @return
     */
    public static Exception changeToCustomException(Exception ex){
		for(Map.Entry<Class,String> entry:exceptionMap.entrySet()){
			System.out.println(entry.getKey().getName() +  "   "+ ex.getClass().getName());
		}
//		System.out.println("-----------------------------");
        //ex.printStackTrace();
//		System.out.println("-----------------------------");
        Exception e = null;
        if(ex instanceof BusinessException){//instanceof 尝试转换是不是自定义异常 如果�?直接转换 如果不是说明是其它框架提供的异常 所�?需要到map中去匹配换成中文提示
            //ex.printStackTrace();
            e = (BusinessException)ex;
            System.out.println("custom");
        }else {//else成立说明 不是自定义异常，是其它框架提供的异常，所以异常信息需要转换
            //如果发现异常类型是跳转map中，则生成跳转异常，否则转换成普通自定义异常
            Optional<String> msgOp = Optional.ofNullable(redirectExceptionMap.get(ex.getClass()));
            if(msgOp.isPresent()){
                  //e =   new RedirectException(msgOp.get());
                e =   new BusinessException(msgOp.get());
                //默认跳转是request 如果要为response跳转 只需在view前加  redirect:xx 以跳转到xx.jsp
            }else {
                  // 从exceptionMap中获取异常信息，如果没有则使用默认信息
                  String msg = exceptionMap.get(ex.getClass());
                  msg = Optional.ofNullable(msg).orElse("未知异常");
                  e =   new BusinessException(msg);
                  e.printStackTrace();
            }
        }
        return e;
    }
}
