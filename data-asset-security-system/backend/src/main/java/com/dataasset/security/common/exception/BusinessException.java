package com.dataasset.security.common.exception;

import com.dataasset.security.common.result.ResultCode;

/**
 * 业务异常类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public class BusinessException extends BaseException {

    private static final long serialVersionUID = 1L;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Integer code, String message) {
        super(code, message);
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getCode(), resultCode.getMessage());
    }
}
