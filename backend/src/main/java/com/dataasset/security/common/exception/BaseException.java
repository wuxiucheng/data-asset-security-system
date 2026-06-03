package com.dataasset.security.common.exception;

import com.dataasset.security.common.result.ResultCode;
import lombok.Getter;

/**
 * 基础异常类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Getter
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;

    public BaseException(String message) {
        super(message);
        this.code = ResultCode.ERROR.getCode();
        this.message = message;
    }

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BaseException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public BaseException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.message = message;
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResultCode.ERROR.getCode();
        this.message = message;
    }
}
