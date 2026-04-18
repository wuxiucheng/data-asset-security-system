package com.dataasset.security.common.exception;

import com.dataasset.security.common.result.ResultCode;

/**
 * 参数验证异常类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public class ValidationException extends BaseException {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(ResultCode.BAD_REQUEST.getCode(), message);
    }

    public ValidationException(ResultCode resultCode) {
        super(resultCode);
    }
}
