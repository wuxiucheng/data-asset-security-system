package com.dataasset.security.common.annotation;

import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.common.enums.ObjectTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 审计日志注解
 * 用于标记需要记录审计日志的方法
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLog {

    /**
     * 操作类型
     */
    OperationTypeEnum operationType() default OperationTypeEnum.QUERY;

    /**
     * 操作对象类型
     */
    ObjectTypeEnum objectType() default ObjectTypeEnum.OTHER;

    /**
     * 操作描述
     */
    String description() default "";
}
