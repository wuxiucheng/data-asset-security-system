package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 敏感数据识别规则实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sensitive_ident_rule")
public class SensitiveIdentRule extends BaseEntity {

    /**
     * 规则ID
     */
    @TableId
    private Long ruleId;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 敏感类型标签
     */
    private String sensitiveType;

    /**
     * 匹配模式：FIELD_NAME/REGEX/SAMPLE
     */
    private String matchMode;

    /**
     * 匹配表达式
     */
    private String matchExpression;

    /**
     * 置信度权重(0-1)
     */
    private BigDecimal confidenceWeight;

    /**
     * 优先级(越小越高)
     */
    private Integer priority;

    /**
     * 是否内置规则
     */
    private Boolean isBuiltin;

    /**
     * 状态：ENABLED/DISABLED
     */
    private String status;
}
