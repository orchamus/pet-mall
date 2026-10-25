package com.example.back.Result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一结果集处理器。
 * <p>封装后端接口返回的统一响应结构，包括状态码、状态标识、提示信息及业务数据，
 * 并提供多个重载的 {@code success}/{@code fail} 工厂方法用于快速构建响应对象。</p>
 *
 * @author petStore
 * @param <T> 响应数据的类型
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("统一结果集处理器")
public class ResponseResult<T> {
    /**
     * 状态码。
     * <p>与 {@link HttpStatusEnum} 中的状态码对应。</p>
     */
    @ApiModelProperty(value = "状态码")
    private Integer code;

    /**
     * 状态信息。
     * <p>{@code true} 表示请求处理成功，{@code false} 表示失败。</p>
     */
    @ApiModelProperty(value = "状态信息")
    private Boolean status;

    /**
     * 返回信息。
     * <p>用于向调用方展示操作结果的提示文案。</p>
     */
    @ApiModelProperty(value = "返回信息")
    private String message;

    /**
     * 数据。
     * <p>业务响应数据，使用泛型 {@code T} 以支持不同类型。</p>
     */
    @ApiModelProperty(value = "数据")
    private T data;

    /**
     * 全参数构造方法（包含数据）。
     *
     * @param code    状态码
     * @param status  状态标识
     * @param message 返回信息
     * @param data    返回数据
     * @param <T>     泛型
     * @return {@link ResponseResult<T>}
     */
    private static <T> ResponseResult<T> response(Integer code, Boolean status, String message, T data) {


        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.setCode(code);
        responseResult.setStatus(status);
        responseResult.setMessage(message);
        responseResult.setData(data);
        return responseResult;
    }

    /**
     * 全参数构造方法（不含数据）。
     *
     * @param code    状态码
     * @param status  状态标识
     * @param message 返回信息
     * @param <T>     泛型
     * @return {@link ResponseResult<T>}
     */
    private static <T> ResponseResult<T> response(Integer code, Boolean status, String message) {


        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.setCode(code);
        responseResult.setStatus(status);
        responseResult.setMessage(message);
        return responseResult;
    }

    /**
     * 成功返回（无参）。
     *
     * @param <T> 泛型
     * @return {@link ResponseResult<T>}
     */
    public static <T> ResponseResult<T> success() {


        return response(HttpStatusEnum.SUCCESS.getCode(), true, HttpStatusEnum.SUCCESS.getMessage(), null);
    }

    /**
     * 成功返回（枚举参数）。
     *
     * @param httpResponseEnum 枚举参数
     * @param <T>              泛型
     * @return {@link ResponseResult<T>}
     */
    public static <T> ResponseResult<T> success(HttpStatusEnum httpResponseEnum) {


        return response(httpResponseEnum.getCode(), true, httpResponseEnum.getMessage());
    }

    /**
     * 成功返回（状态码 + 返回信息）。
     *
     * @param code    状态码
     * @param message 返回信息
     * @param <T>     泛型
     * @return {@link ResponseResult<T>}
     */
    public static <T> ResponseResult<T> success(Integer code, String message) {


        return response(code, true, message);
    }

    /**
     * 成功返回（返回信息 + 数据）。
     *
     * @param message 返回信息
     * @param data    数据
     * @param <T>     泛型
     * @return {@link ResponseResult<T>}
     */
    public static <T> ResponseResult<T> success(String message, T data) {


        return response(HttpStatusEnum.SUCCESS.getCode(), true, message, data);
    }

    /**
     * 成功返回（状态码 + 返回信息 + 数据）。
     *
     * @param code    状态码
     * @param message 返回信息
     * @param data    数据
     * @param <T>     泛型
     * @return {@link ResponseResult<T>}
     */
    public static <T> ResponseResult<T> success(Integer code, String message, T data) {


        return response(code, true, message, data);
    }

    /**
     * 成功返回（数据）。
     *
     * @param data 数据
     * @param <T>  泛型
     * @return {@link ResponseResult<T>}
     */
    public static <T> ResponseResult<T> success(T data) {


        return response(HttpStatusEnum.SUCCESS.getCode(), true, HttpStatusEnum.SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回（返回信息）。
     *
     * @param message 返回信息
     * @param <T>  泛型
     * @return {@link ResponseResult<T>}
     */
    public static <T> ResponseResult<T> success(String message) {


        return response(HttpStatusEnum.SUCCESS.getCode(), true, message, null);
    }

    /**
     * 失败返回（无参）。
     *
     * @param <T> 泛型
     * @return {@link ResponseResult<T>}
     */
    public static <T> ResponseResult<T> fail() {
        return response(HttpStatusEnum.ERROR.getCode(), false, HttpStatusEnum.ERROR.getMessage(), null);
    }

    /**
     * 失败返回（枚举）。
     *
     * @param httpResponseEnum 枚举
     * @param <T>              泛型
     * @return {@link ResponseResult<T>}
     */
    public static <T> ResponseResult<T> fail(HttpStatusEnum httpResponseEnum) {


        return response(httpResponseEnum.getCode(), false, httpResponseEnum.getMessage());
    }

    /**
     * 失败返回（状态码 + 返回信息）。
     *
     * @param code    状态码
     * @param message 返回信息
     * @param <T>     泛型
     * @return {@link ResponseResult<T>}
     */
    public static <T> ResponseResult<T> fail(Integer code, String message) {


        return response(code, false, message);
    }

    /**
     * 失败返回（返回信息 + 数据）。
     *
     * @param message 返回信息
     * @param data    数据
     * @param <T>     泛型
     * @return {@link ResponseResult<T>}
     */
    public static <T> ResponseResult<T> fail(String message, T data) {
        return response(HttpStatusEnum.ERROR.getCode(), false, message, data);
    }

    /**
     * 失败返回（状态码 + 返回信息 + 数据）。
     *
     * @param code    状态码
     * @param message 返回消息
     * @param data    数据
     * @param <T>     泛型
     * @return {@link ResponseResult<T>}
     */
    public static <T> ResponseResult<T> fail(Integer code, String message, T data) {


        return response(code, false, message, data);
    }

    /**
     * 失败返回（数据）。
     *
     * @param data 数据
     * @param <T>  泛型
     * @return {@link ResponseResult<T>}
     */
    public static <T> ResponseResult<T> fail(T data) {


        return response(HttpStatusEnum.ERROR.getCode(), false, HttpStatusEnum.ERROR.getMessage(), data);
    }

    /**
     * 判断请求是否成功。
     *
     * @return 成功返回 true，失败返回 false
     */
    public boolean isSuccess() {
        return Boolean.TRUE.equals(this.status);
    }

    /**
     * 失败返回（返回信息）。
     *
     * @param message 返回信息
     * @param <T>  泛型
     * @return {@link ResponseResult<T>}
     */
    public static <T> ResponseResult<T> fail(String message) {


        return response(HttpStatusEnum.ERROR.getCode(), false, message, null);
    }
}
