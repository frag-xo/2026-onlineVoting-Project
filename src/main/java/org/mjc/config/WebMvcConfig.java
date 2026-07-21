package org.mjc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置 - 静态资源映射
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /upload/** 请求映射到项目根目录下的 upload 文件夹
        String projectPath = System.getProperty("user.dir");
        String uploadPath = "file:" + projectPath + "/upload/";
        
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(uploadPath);
    }
}