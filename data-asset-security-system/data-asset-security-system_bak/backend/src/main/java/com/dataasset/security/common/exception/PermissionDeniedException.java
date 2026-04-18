package com.dataasset.security.common.exception;

import com.dataasset.security.common.result.ResultCode;

/**
 * 权限不足异常类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public class PermissionDeniedException extends BaseException {

    private static final long serialVersionUID = 1L;

    public PermissionDeniedException(String message) {
        super(ResultCode.FORBIDDEN.getCode(), message);
    }

    public PermissionDeniedException(ResultCode resultCode) {
        super(resultCode);
    }
}
