package com.dataasset.security.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 敏感识别规则更新DTO
 */
@Data
public class SensitiveIdentRuleUpdateDTO {

    /**
     * 规则ID
     */
    @NotNull(message = "规则ID不能为空")
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
     * 置信度权重(0-1)
     */
    private BigDecimal confidenceWeight;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 状态
     */
    private String status;
}
