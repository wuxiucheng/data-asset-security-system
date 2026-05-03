package com.dataasset.security.common.exception;

/**
 * 限流异常
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public class RateLimitException extends RuntimeException {

    public RateLimitException(String message) {
        super(message);
    }

    public RateLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}
