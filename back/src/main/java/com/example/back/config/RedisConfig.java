package com.example.back.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 模板配置类。
 * <p>配置 {@link RedisTemplate} 的 key/value 序列化方式，使用 String 序列化 key，
 * 使用 Jackson JSON 序列化 value，便于对象存储与读取。</p>
 *
 * @author petStore
 * @since 1.0
 */
@Configuration
public class RedisConfig {

    /**
     * 创建并配置 RedisTemplate 实例。
     *
     * @param redisConnectionFactory Redis 连接工厂
     * @return 配置完成的 RedisTemplate 实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        // 配置 Redis 键值序列化器：key 使用字符串，value 使用 Jackson JSON
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}
