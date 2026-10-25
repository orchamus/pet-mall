package com.example.back.utils;


import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * 请求返回结果对象。
 * <p>封装后端接口返回的状态码、提示信息及业务数据，并提供设置各类业务状态的方法。</p>
 *
 * @author bill.lin
 * @param <T> 业务数据类型
 * @since 1.0
 */
@Schema(name = "Result后端返回结构")
public class Result<T> implements Serializable {

    /** 状态码，默认取 {@link ResultType#SUCCESS} 对应状态码 */
    @Schema(description = "状态码")
    private String statusCode = ResultType.SUCCESS.getCode();

    /** 返回消息，默认取 {@link ResultType#SUCCESS} 对应名称 */
    @Schema(description = "返回消息")
    private String message = ResultType.SUCCESS.getName();

    /** 返回结果数据 */
    @Schema(description = "返回结果")
    private T data = null;


    /**
     * 获取状态编码。
     *
     * @return 状态编码
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * 设置状态编码。
     *
     * @param statusCode 状态编码
     */
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * 获取提示信息。
     *
     * @return 提示信息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置提示信息。
     *
     * @param message 提示信息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取返回结果。
     *
     * @return 返回结果数据
     */
    public T getData() {
        return data;
    }

    /**
     * 设置返回结果。
     *
     * @param data 返回结果数据
     */
    public void setData(T data) {
        this.data = data;
    }


    /**
     * 标记为服务器内部错误，使用默认提示信息。
     */
    public void addError() {
        this.addError("");
    }

    /**
     * 标记为服务器内部错误。
     * <p>当传入 message 为空时，使用 {@link ResultType#INTERNAL_SERVER_ERROR} 的默认名称。</p>
     *
     * @param message 自定义提示信息
     */
    public void addError(String message) {
        this.message = message;
        this.statusCode = ResultType.INTERNAL_SERVER_ERROR.getCode();
        // 若未传入提示信息，则使用枚举默认文案
        if (this.message == null || "".equals(this.message)) {
            this.message = ResultType.INTERNAL_SERVER_ERROR.getName();
        }
    }

    /**
     * 标记为成功，使用默认提示信息。
     */
    public void success() {
        this.success("");
    }

    /**
     * 标记为成功。
     * <p>当传入 message 为空时，使用 {@link ResultType#SUCCESS} 的默认名称。</p>
     *
     * @param message 自定义提示信息
     */
    public void success(String message) {
        this.message = message;
        this.statusCode = ResultType.SUCCESS.getCode();
        // 若未传入提示信息，则使用枚举默认文案
        if (this.message == null || "".equals(this.message)) {
            this.message = ResultType.SUCCESS.getName();
        }
    }

    /**
     * 标记为操作失败，使用默认提示信息。
     */
    public void fail() {
        this.fail("");
    }

    /**
     * 标记为操作失败。
     * <p>当传入 message 为空时，使用 {@link ResultType#FAIL} 的默认名称。</p>
     *
     * @param message 自定义提示信息
     */
    public void fail(String message) {
        this.message = message;
        this.statusCode = ResultType.FAIL.getCode();
        // 若未传入提示信息，则使用枚举默认文案
        if (this.message == null || "".equals(this.message)) {
            this.message = ResultType.FAIL.getName();
        }
    }

    /**
     * 标记为权限不足，使用默认提示信息。
     */
    public void unauthorized() {
        this.unauthorized("");
    }

    /**
     * 标记为权限不足。
     * <p>当传入 message 为空时，使用 {@link ResultType#UNAUTHORIZED} 的默认名称。</p>
     *
     * @param message 自定义提示信息
     */
    public void unauthorized(String message) {
        this.message = message;
        this.statusCode = ResultType.UNAUTHORIZED.getCode();
        // 若未传入提示信息，则使用枚举默认文案
        if (this.message == null || "".equals(this.message)) {
            this.message = ResultType.UNAUTHORIZED.getName();
        }
    }

    /**
     * 标记为接口不存在，使用默认提示信息。
     */
    public void notFound() {
        this.notFound("");
    }

    /**
     * 标记为接口不存在。
     * <p>当传入 message 为空时，使用 {@link ResultType#NOT_FOUND} 的默认名称。</p>
     *
     * @param message 自定义提示信息
     */
    public void notFound(String message) {
        this.message = message;
        this.statusCode = ResultType.NOT_FOUND.getCode();
        // 若未传入提示信息，则使用枚举默认文案
        if (this.message == null || "".equals(this.message)) {
            this.message = ResultType.NOT_FOUND.getName();
        }
    }

    /**
     * 标记为需要重新登录，使用默认提示信息。
     */
    public void againLogin() {
        this.againLogin("");
    }

    /**
     * 标记为需要重新登录。
     * <p>当传入 message 为空时，使用 {@link ResultType#AGAIN_LOGIN} 的默认名称。</p>
     *
     * @param message 自定义提示信息
     */
    public void againLogin(String message) {
        this.message = message;
        this.statusCode = ResultType.AGAIN_LOGIN.getCode();
        // 若未传入提示信息，则使用枚举默认文案
        if (this.message == null || "".equals(this.message)) {
            this.message = ResultType.AGAIN_LOGIN.getName();
        }
    }
}
