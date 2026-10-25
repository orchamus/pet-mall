package com.example.back.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * 文件上传资源访问配置类。
 * <p>将 {@code /files/**} 请求映射到配置文件指定的本地上传目录，并启用资源链缓存。</p>
 *
 * @author petStore
 * @since 1.0
 */
@Configuration
public class fileConfig implements WebMvcConfigurer {

    /**
     * 文件上传目录路径。
     * <p>读取自配置项 {@code file.upload-path}。</p>
     */
    @Value("${file.upload-path}")
    private String uploadPath;

    /**
     * 配置文件资源处理器。
     *
     * @param registry 资源处理器注册表
     */
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 将 /files/** 映射到配置的上传目录，缓存 3600 秒
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + uploadPath + "/")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }
}
