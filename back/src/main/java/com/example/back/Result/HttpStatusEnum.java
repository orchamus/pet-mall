package com.example.back.Result;

import lombok.Getter;

/**
 * HTTP 状态及业务状态枚举。
 * <p>定义常用 HTTP 状态码与对应提示文案，供 {@link ResponseResult} 统一响应使用。</p>
 *
 * @author petStore
 * @since 1.0
 */
@Getter
public enum HttpStatusEnum {
    /**
     * 操作成功。
     * <p>HTTP 状态码 200。</p>
     */
    SUCCESS(200, "操作成功"),
    /**
     * 对象创建成功。
     * <p>HTTP 状态码 201。</p>
     */
    CREATED(201, "对象创建成功"),
    /**
     * 请求已经被接受。
     * <p>HTTP 状态码 202。</p>
     */
    ACCEPTED(202, "请求已经被接受"),
    /**
     * 操作已经执行成功，但是没有返回数据。
     * <p>HTTP 状态码 204。</p>
     */
    NO_CONTENT(204, "操作已经执行成功，但是没有返回数据"),
    /**
     * 资源已被移除。
     * <p>HTTP 状态码 301。</p>
     */
    MOVED_PERM(301, "资源已被移除"),
    /**
     * 重定向。
     * <p>HTTP 状态码 303。</p>
     */
    SEE_OTHER(303, "重定向"),
    /**
     * 资源没有被修改。
     * <p>HTTP 状态码 304。</p>
     */
    NOT_MODIFIED(304, "资源没有被修改"),
    /**
     * 参数列表错误（缺少，格式不匹配）。
     * <p>HTTP 状态码 400。</p>
     */
    BAD_REQUEST(400, "参数列表错误（缺少，格式不匹配）"),
    /**
     * 未授权。
     * <p>HTTP 状态码 401。</p>
     */
    UNAUTHORIZED(401, "未授权"),
    /**
     * 访问受限，授权过期。
     * <p>HTTP 状态码 403。</p>
     */
    FORBIDDEN(403, "访问受限，授权过期"),
    /**
     * 资源，服务未找到。
     * <p>HTTP 状态码 404。</p>
     */
    NOT_FOUND(404, "资源，服务未找！"),
    /**
     * 不允许的 http 方法。
     * <p>HTTP 状态码 405。</p>
     */
    BAD_METHOD(405, "不允许的http方法"),
    /**
     * 资源冲突，或者资源被锁。
     * <p>HTTP 状态码 409。</p>
     */
    CONFLICT(409, "资源冲突，或者资源被锁"),
    /**
     * 不支持的数据，媒体类型。
     * <p>HTTP 状态码 415。</p>
     */
    UNSUPPORTED_TYPE(415, "不支持的数据，媒体类型"),
    /**
     * 系统内部错误。
     * <p>HTTP 状态码 500。</p>
     */
    ERROR(500, "系统内部错误"),
    /**
     * 接口未实现。
     * <p>HTTP 状态码 501。</p>
     */
    NOT_IMPLEMENTED(501, "接口未实现"),
    /**
     * 系统警告消息。
     * <p>业务自定义状态码 601。</p>
     */
    WARN(601, "系统警告消息");

    /**
     * 状态码。
     * <p>对应 HTTP 或业务状态数值。</p>
     */
    private final Integer code;

    /**
     * 状态提示信息。
     */
    private final String message;

    /**
     * 构造状态枚举实例。
     *
     * @param code    状态码
     * @param message 状态提示信息
     */
    HttpStatusEnum(Integer code, String message) {


        this.code = code;
        this.message = message;
    }
}
