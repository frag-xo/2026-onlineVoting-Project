package org.mjc.config;

import org.mjc.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.Resource;

/**
 * Web MVC 配置 - 静态资源映射 + 拦截器
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private AuthInterceptor authInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /upload/** 请求映射到项目根目录下的 upload 文件夹
        String projectPath = System.getProperty("user.dir");
        String uploadPath = "file:" + projectPath + "/upload/";

        registry.addResourceHandler("/upload/**")
                .addResourceLocations(uploadPath);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")  // 拦截所有/api/开头的请求
                .excludePathPatterns(         // 排除不需要登录的接口
                        "/api/account/login",      // 登录
                        "/api/account/register"    // 注册
                );
    }
}