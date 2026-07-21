package org.mjc.annotation;

import java.lang.annotation.*;

//系统日志注解，有这个注解记录日志
@Target({ ElementType.PARAMETER, ElementType.METHOD })
   @Retention(RetentionPolicy.RUNTIME)
   @Documented
   public @interface SystemLog {
      String module() default "";
      String methods() default "";
   }
