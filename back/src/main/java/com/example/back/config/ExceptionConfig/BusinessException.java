package com.example.back.config.ExceptionConfig;

/**
 * 业务异常类。
 * <p>用于封装业务层出现的可预期异常，通过 {@link RuntimeException} 抛出，便于全局异常处理。</p>
 *
 * @author petStore
 * @since 1.0
 */
public class BusinessException extends RuntimeException {

    /**
     * 根据提示信息构造业务异常。
     *
     * @param message 异常提示信息
     */
    public BusinessException(String message) {
        super(message);
    }
}
