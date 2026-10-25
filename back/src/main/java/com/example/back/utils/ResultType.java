package com.example.back.utils;

/**
 * 请求返回结果类型枚举。
 * <p>定义后端接口常用的业务响应状态码与提示文案。</p>
 *
 * @author bill
 * @since 1.0
 */
public enum ResultType {
    /** 操作成功，状态码 200 */
    SUCCESS("200", "操作成功"),
    /** 操作失败，状态码 400 */
    FAIL("400", "操作失败"),
    /** 权限不足，状态码 401 */
    UNAUTHORIZED("401", "权限不足"),
    /** 接口不存在，状态码 404 */
    NOT_FOUND("404", "接口不存在"),
    /** 服务器内部错误，状态码 500 */
    INTERNAL_SERVER_ERROR("500", "服务器内部错误"),
    /** 需要重新登录，状态码 600 */
    AGAIN_LOGIN("600", "请重新登录");


    /** 状态码 */
    private String code;

    /** 状态名称/提示信息 */
    private String name;

    /**
     * 构造结果类型枚举实例。
     *
     * @param code 状态码
     * @param name 状态名称
     */
    ResultType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 获取状态码。
     *
     * @return 状态码字符串
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取状态名称/提示信息。
     *
     * @return 状态名称
     */
    public String getName() {
        return name;
    }
}
