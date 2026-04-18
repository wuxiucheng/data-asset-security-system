package com.dataasset.security.common.exception;

import com.dataasset.security.common.result.ResultCode;

/**
 * 资源不存在异常类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public class ResourceNotFoundException extends BaseException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(ResultCode.NOT_FOUND.getCode(), message);
    }

    public ResourceNotFoundException(ResultCode resultCode) {
        super(resultCode);
    }
}
