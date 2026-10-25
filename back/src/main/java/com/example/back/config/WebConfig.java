package com.example.back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 静态资源映射配置类。
 * <p>将 {@code /media/**} 请求映射到本地磁盘上传目录，用于直接访问上传的媒体文件。</p>
 *
 * @author petStore
 * @since 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源处理器。
     *
     * @param registry 资源处理器注册表
     */
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 将 /media/** 映射到本地文件系统 D:/petShop/upload/ 目录
        registry.addResourceHandler("/media/**")
                .addResourceLocations("file:D:/petShop/upload/");
    }
}
