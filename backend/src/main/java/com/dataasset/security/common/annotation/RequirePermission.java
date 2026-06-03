package com.dataasset.security.common.annotation;

import java.lang.annotation.*;

/**
 * 权限验证注解
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 需要的权限编码
     */
    String[] value() default {};

    /**
     * 权限验证模式：AND（所有权限都必须满足），OR（满足任意一个权限即可）
     */
    String logical() default "OR";

    /**
     * 权限验证失败时的提示信息
     */
    String message() default "权限不足";
}