package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.TableId;
import java.math.BigDecimal;

/**
 * 质量规则实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("quality_rule")
public class QualityRule extends BaseEntity {

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
     * 规则类型：COMPLETENESS/UNIQUENESS/FORMAT/VALUE_RANGE/CONSISTENCY/CUSTOM
     */
    private String ruleType;

    /**
     * 校验表达式
     */
    private String ruleExpression;

    /**
     * 适用范围：GLOBAL/CLASSIFICATION/ASSET/FIELD
     */
    private String scopeType;

    /**
     * 范围目标ID，GLOBAL时为空
     */
    private Long scopeId;

    /**
     * 阈值(0-100)
     */
    private BigDecimal threshold;

    /**
     * 权重(1-10)
     */
    private Integer weight;

    /**
     * 状态：ENABLED/DISABLED
     */
    private String status;

    /**
     * 规则描述
     */
    private String description;
}
