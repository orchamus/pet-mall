package com.example.back;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 后端服务启动入口类。
 * <p>负责扫描 Mapper 接口、启用 Spring Cache 缓存注解，并启动 Spring Boot 应用。</p>
 *
 * @author petStore
 * @since 1.0
 */
@MapperScan("com.example.back.mapper")
@EnableCaching
@SpringBootApplication
public class BackApplication {

    /**
     * 应用主入口方法。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(BackApplication.class, args);
    }

}
