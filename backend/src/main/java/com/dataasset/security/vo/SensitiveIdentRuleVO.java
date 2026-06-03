package com.dataasset.security.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 敏感识别规则VO
 */
@Data
public class SensitiveIdentRuleVO {

    /**
     * 规则ID
     */
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
     * 匹配模式
     */
    private String matchMode;

    /**
     * 匹配表达式
     */
    private String matchExpression;

    /**
     * 置信度权重
     */
    private BigDecimal confidenceWeight;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 是否内置规则
     */
    private Boolean isBuiltin;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}
