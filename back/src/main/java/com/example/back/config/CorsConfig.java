package com.example.back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域访问配置类。
 * <p>通过 Spring MVC 的 CORS 配置，解决前后端分离场景下的跨域请求问题。</p>
 *
 * @author petStore
 * @since 1.0
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 配置跨域映射规则。
     *
     * @param registry CORS 注册表
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        // 允许所有来源、所有请求头及常用 HTTP 方法，并支持携带 cookie
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*");

    }
}
