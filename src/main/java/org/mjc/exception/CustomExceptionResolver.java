package org.mjc.exception;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;



import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mjc.dto.DTO;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

//spring会在容器中找 那个bean实现了HandlerExceptionResolver 异常解析器 把所有的异常都交给他处理
//处理就是将认识的异常的信息转换成对应中文的 自定义异常再处理@ControllerAdvice
@ControllerAdvice
public class CustomExceptionResolver{
	static Map<Class,String> exceptionMap = new HashMap<Class,String>();
	static Map<Class,String> redirectExceptionMap = new HashMap<>();
	//如果认识一些系统异常，将异常转换为自定义异常输出，目的是将异常的提示信息换成中文输出
	static{
		exceptionMap.put(BusinessException.class,"");
		exceptionMap.put(MyBatisSystemException.class,"数据库异常请联系管理员");
		exceptionMap.put(CannotGetJdbcConnectionException.class,"数据库异常请联系管理员");
		exceptionMap.put(RecoverableDataAccessException.class,"数据库异常，请联系管理员");
		exceptionMap.put(CommunicationsException.class,"数据库异常，请联系管理员");
		exceptionMap.put(ConnectException.class,"数据库异常，请联系管理员");
		exceptionMap.put(NumberFormatException.class,"数字不正确，非法操作");
		exceptionMap.put(HttpRequestMethodNotSupportedException.class,"方法请求方式不正确");
		exceptionMap.put(DuplicateKeyException.class,"错误的领取记录编号，请查证后再录入");

	}
	//spring 获取到异常就会调用 下面的resolveException 来处理异常
	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public DTO resolveExceptionProcess(HttpServletRequest request, HttpServletResponse response, Object obj,
									   Exception ex) {
		BusinessException ce = changeToCustomException(ex);
		System.out.println("----------------System.out.println(ce.getMessage());//服务器查看一下异常信息，后面换成日志输出------------------");//服务器查看一下异常信息，后面换成日志输出
		System.out.println(ce.getMessage());//服务器查看一下异常信息，后面换成日志输出
		DTO dto = new DTO(409,ce.getMessage(),"error");//返回的dto对象的msg由自定义exception提供
		return dto;

	}
	/**
	 * 将系统异常转为自定义异常就是�? 我们的方�?可能抛出2种异常一�?其它框架提供的异常（IOException，MyBatisSystemException�?还有我们已经自定义的异常（PWDEXCEPTION 密码错误异常�? 首先判断跑出来的是不是自定异常，尝试转换为自定异�?尝试失败说明�?其它框架提供的异常然后在预先认识的map�?找到�?生成自定义异�?提示信息�?预先设置好的，找不到 也生成自定义异常只不过，提示信息为未知异�?	 * @param ex
	 * @return
	 */
	public BusinessException changeToCustomException(Exception ex){
		BusinessException ce;
	ex.printStackTrace();//注释掉后台就看见异常的堆栈追踪
	if(ex instanceof BusinessException){//instanceof 尝试转换是不是自定义异常 如果是直接转换 如果不是说明是其它框架提供的异常 所以需要到map中去匹配换成中文提示
		ce = (BusinessException)ex;
		System.out.println("custom");
	}else {//else成立说明 不是自定义异常，是其它框架提供的异常，所以异常信息需要转换
		String msg = exceptionMap.get(ex.getClass());//如果在map中找到了对应的异常类型，得到中文提示信息
		//System.out.println(msg+"------------------------");
		msg = Optional.ofNullable(msg).orElse("未知异常");
		ce = new BusinessException(msg);
	}
	return ce;
	}
}
