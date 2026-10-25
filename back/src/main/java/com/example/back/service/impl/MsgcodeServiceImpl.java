package com.example.back.service.impl;

import com.example.back.service.ImsgcodeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 验证码服务实现类。
 *
 * <p>基于 JVM 内存中的 {@link ConcurrentHashMap} 缓存邮箱验证码，默认有效期 15 分钟。
 * 该实现适用于单实例部署；多实例环境下需替换为 Redis 等分布式缓存。</p>
 */
@Service
public class MsgcodeServiceImpl implements ImsgcodeService {
    // 验证码有效期，单位：分钟
    private static final long EXPIRE_MINUTES = 15;
    // 邮箱 -> 验证码条目的内存缓存
    private final Map<String, CodeEntry> codeStore = new ConcurrentHashMap<>();

    /**
     * 向指定邮箱发送验证码。
     *
     * <p>生成 6 位随机数字验证码，并写入缓存，设置过期时间为当前时间 + 15 分钟。</p>
     *
     * @param email 目标邮箱地址
     * @return 生成的验证码；邮箱为空时返回 null
     */
    @Override
    public String sendMsgCodeByeMail(String email) {
        if (!StringUtils.hasLength(email)) {
            return null;
        }
        // 生成 100000-999999 之间的 6 位随机数字
        int randomCode = ThreadLocalRandom.current().nextInt(100000, 1000000);
        String code = String.valueOf(randomCode);
        codeStore.put(email, new CodeEntry(code, LocalDateTime.now().plusMinutes(EXPIRE_MINUTES)));
        return code;
    }

    /**
     * 获取指定邮箱当前有效的验证码。
     *
     * <p>若验证码已过期，则自动清理该条目。</p>
     *
     * @param email 目标邮箱地址
     * @return 未过期的验证码；不存在或已过期则返回 null
     */
    @Override
    public String getCode(String email) {
        CodeEntry entry = codeStore.get(email);
        if (entry == null) {
            return null;
        }
        // 校验验证码是否过期，过期则移除
        if (LocalDateTime.now().isAfter(entry.expireTime)) {
            codeStore.remove(email);
            return null;
        }
        return entry.code;
    }

    /**
     * 清除指定邮箱的验证码记录。
     *
     * @param email 目标邮箱地址
     */
    @Override
    public void removeCode(String email) {
        codeStore.remove(email);
    }

    /**
     * 验证码缓存条目，包含验证码与过期时间。
     */
    private static class CodeEntry {
        private final String code;
        private final LocalDateTime expireTime;

        /**
         * 构造验证码缓存条目。
         *
         * @param code       验证码
         * @param expireTime 过期时间
         */
        CodeEntry(String code, LocalDateTime expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }
    }
}
