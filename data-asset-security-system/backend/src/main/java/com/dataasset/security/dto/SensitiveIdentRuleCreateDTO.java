package com.dataasset.security.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 敏感识别规则创建DTO
 */
@Data
public class SensitiveIdentRuleCreateDTO {

    /**
     * 规则名称
     */
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;

    /**
     * 敏感类型标签
     */
    @NotBlank(message = "敏感类型不能为空")
    private String sensitiveType;

    /**
     * 匹配模式：FIELD_NAME/REGEX/SAMPLE
     */
    @NotBlank(message = "匹配模式不能为空")
    private String matchMode;

    /**
     * 匹配表达式
     */
    @NotBlank(message = "匹配表达式不能为空")
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
     * 状态：ENABLED/DISABLED
     */
    private String status;
}
