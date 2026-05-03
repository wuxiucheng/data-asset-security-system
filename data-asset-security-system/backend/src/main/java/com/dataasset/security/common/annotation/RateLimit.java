package com.dataasset.security.common.annotation;

import java.lang.annotation.*;

/**
 * 接口限流注解
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流key
     */
    String key() default "";

    /**
     * 限流时间窗口（秒）
     */
    int time() default 60;

    /**
     * 限流次数
     */
    int count() default 100;

    /**
     * 限流类型：IP, USER, API
     */
    String limitType() default "API";

    /**
     * 限流提示信息
     */
    String message() default "访问过于频繁，请稍后再试";
}