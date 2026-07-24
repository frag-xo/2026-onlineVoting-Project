package org.mjc.aop;

/**
 * Log4j配置及使用
 * 一、log4j.properties配置文件
 *
 * Log4j默认的配置文件是log4j.properties，将该文件置于classpath下，容器启动时会初始化Log4j。Log4j把日志级别由低到高依次分为ALL、TRACE、DEBUG、INFO、WARN、ERROR、FITAL和OFF等。其中，级别高的会屏蔽低的信息。如果设置为WARN，则INFO、DEBUG都不会输出
 *
 * 三个重要的概念
 *
 * Log4j配置中有5个重要的概念：日志记录器（Logger）、根记录器（rootLogger）、类别（category）、输出地（Appender）以及日志格式化器（Layout）。其中，Logger负责记录日志；rootLogger是所有记录器的父亲，任何记录器都可继承rooLogger的配置；category可以设置类别下所有的Logger，类似于java中的包，效果与Logger名字等价；Appender负责输出到什么地方；Layout负责以什么格式输出、输出哪些附加信息（比如：时间、类名、方法名、所在行数等）。在log4j.properties配置中，log4j.logger后面配置的是Logger，log4j.appender后面配置的是Appender，rootLogger直接用log4j.rootLogger配置
 */


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mjc.annotation.SystemLog;
import org.mjc.dto.DTO;
import org.mjc.exception.CustomExceptionResolver;
import org.mjc.log.CustomLogger;
import org.mjc.log.Log;
import org.mjc.utils.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // 导入 slf4j 的包
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * LG
 */
@Component
@Aspect
public class LogAspect {

   @Resource
   CustomExceptionResolver customExceptionResolverl;
   //log4j工具类
   public static Logger logger = LoggerFactory.getLogger(LogAspect.class);
   //自定义级别工具类
   Logger customLogger = CustomLogger.getInstance().createLogger("d:/log/controller/", "controller", "--->", false);
   Logger customLogger1 = CustomLogger.getInstance().createLogger("d:/log/service/", "service", "--->", false);

   // 获取开始时间
   private long BEGIN_TIME;

   // 获取结束时间
   private long END_TIME;

   // 定义本次log实体
   private Log log = new Log();


    @Pointcut("execution(public * org.dlxt.controller.*.*(..))")
   private void controllerAspect() {
   }

   /**
    * 方法开始执行
    */
   @Before("controllerAspect()")
   public void doBefore() {
      BEGIN_TIME = new Date().getTime();
      System.out.println("开始");
   }

   /**
    * 方法结束执行
    */
   @After("controllerAspect()")
   public void after() throws Throwable {
      END_TIME = new Date().getTime();
      System.out.println("结束");
   }

   /**
    * 方法结束执行后的操作
    */
   @AfterReturning("controllerAspect()")
   public void doAfter(JoinPoint jp) {

      if (log.getState() == 1 || log.getState() == -1) {
         log.setActionTime(END_TIME - BEGIN_TIME);
         log.setGmtCreate(new Date(BEGIN_TIME));
         //System.out.println(log);
         //System.out.println(">>>>>>>>>>存入到数据库");
      } else {
         //System.out.println(log);
         //System.out.println(">>>>>>>>不存入到数据库");
      }
   }

   /**
    * 方法有异常时的操作
    */
   @AfterThrowing(pointcut="controllerAspect()",throwing="e")

   public void AfterThrow(Exception e) throws IOException {
      System.out.println("例外通知-----------------------------------");
      Exception customException = ExceptionUtils.changeToCustomException(e);
      System.out.println(customException.getMessage());
      HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
            .getResponse();
      try (PrintWriter out = response.getWriter()) {
          ObjectMapper mapper = new ObjectMapper();
          DTO dto = new DTO(409, "操作失败");
          out.print(mapper.writeValueAsString(dto));
          out.flush();
      }
   }

   /**
    * 方法执行
    *
    * @param pjp
    * @return
    * @throws Throwable
    */
   @Around("controllerAspect()")
   public Object around(ProceedingJoinPoint pjp) throws Throwable {
      // 日志实体对象
      HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
            .getRequest();
      // 获取当前登陆用户信息
//    User loginUser = SessionUtil.getLoginSession(request);
//    if (loginUser == null) {
//       log.setLoginAccount("未知用户");
//    } else {
//       log.setLoginAccount(loginUser.getUserAuth().getIdentity());
//    }

      // 拦截的实体类，就是当前正在执行的controller
      Object target = pjp.getTarget();
      // 拦截的方法名称。当前正在执行的方法
      String methodName = pjp.getSignature().getName();
      // 拦截的方法参�?
      Object[] args = pjp.getArgs();
      // 拦截的放参数类型
      Signature sig = pjp.getSignature();
      MethodSignature msig = null;
      if (!(sig instanceof MethodSignature)) {
         throw new IllegalArgumentException("该注解只能用于方法");
      }
      msig = (MethodSignature) sig;
      Class[] parameterTypes = msig.getMethod().getParameterTypes();

      Object object = null;

      Method method = null;
      try {
         method = target.getClass().getMethod(methodName, parameterTypes);
      } catch (NoSuchMethodException e1) {
         // TODO Auto-generated catch block
         //e1.printStackTrace();
      } catch (SecurityException e1) {
         // TODO Auto-generated catch block
         //e1.printStackTrace();
      }

      if (null != method) {
         // 判断是否包含自定义的注解，说明一下这里的SystemLog就是我自己自定义的注�?
         if (method.isAnnotationPresent(SystemLog.class)) {
            SystemLog systemlog = method.getAnnotation(SystemLog.class);
            log.setModule(systemlog.module());
            log.setMethod(systemlog.methods());
            log.setLoginIp(getIp(request));
            log.setActionUrl(request.getRequestURI());
            log.setActionTime(new Date().getTime());

            try {
               object = pjp.proceed();
               log.setDescription("执行成功");
               System.out.println("执行成功");
               logger.info(log.printfomat());
               CustomLogger.getInstance().customLog(customLogger,log.printfomat());
//             CustomLogger.getInstance().customLog(customLogger1,log.printfomat());
               //CustomLogger.getInstance().customLog(customLogger,log.printfomatHDFS());
               log.setState((short) 1);
            } catch (Throwable e) {
               // TODO Auto-generated catch block
               log.setDescription("执行失败");
               log.setState((short) -1);
            }
         } else {// 没有包含注解
            object = pjp.proceed();
            log.setDescription("此操作不包含注解");
            System.out.println("此操作不包含注解");
            log.setState((short) 0);
         }
      } else { // 不需要拦截直接执行
         object = pjp.proceed();
         System.out.println("不需要拦截直接执行");
         log.setDescription("不需要拦截直接执行");
         log.setState((short) 0);
      }
      return object;
   }

   /**
    * 获取ip地址
    *
    * @param request
    * @return
    */
   private String getIp(HttpServletRequest request) {
      if (request.getHeader("x-forwarded-for") == null) {
         return request.getRemoteAddr();
      }
      return request.getHeader("x-forwarded-for");
   }
}
