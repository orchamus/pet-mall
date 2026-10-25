package com.example.back.service;

/**
 * 短信/邮箱验证码服务接口。
 *
 * <p>定义验证码的生成、读取与清除操作，主要用于用户注册、登录等场景的身份校验。</p>
 */
public interface ImsgcodeService {

    /**
     * 向指定邮箱发送验证码。
     *
     * @param email 目标邮箱地址
     * @return 生成的验证码；若邮箱无效或生成失败，可能返回 null
     */
    public String sendMsgCodeByeMail(String email);

    /**
     * 获取指定邮箱当前有效的验证码。
     *
     * @param email 目标邮箱地址
     * @return 未过期的验证码；不存在或已过期则返回 null
     */
    public String getCode(String email);

    /**
     * 清除指定邮箱的验证码记录。
     *
     * @param email 目标邮箱地址
     */
    public void removeCode(String email);
}
