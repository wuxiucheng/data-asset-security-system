package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分类分级辅助规则
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("classification_assist_rule")
public class ClassificationAssistRule extends BaseEntity {

    /**
     * 规则ID
     */
    private Long ruleId;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则代码
     */
    private String ruleCode;

    /**
     * 规则类型：FIELD_NAME-字段名匹配, FIELD_PATTERN-字段模式, FIELD_SAMPLE-样本匹配
     */
    private String ruleType;

    /**
     * 字段名匹配模式（支持通配符）
     */
    private String fieldNamePattern;

    /**
     * 字段值匹配模式（正则表达式）
     */
    private String fieldValuePattern;

    /**
     * 样本匹配规则JSON
     */
    private String sampleMatchRule;

    /**
     * 建议分级ID
     */
    private Long suggestGradingId;

    /**
     * 分级原因说明
     */
    private String suggestGradingReason;

    /**
     * 规则优先级（数字越小优先级越高）
     */
    private Integer priority;

    /**
     * 是否自动应用：0-否，1-是
     */
    private Integer autoApply;

    /**
     * 是否需要人工审核：0-否，1-是
     */
    private Integer needReview;

    /**
     * 状态：ACTIVE-启用，INACTIVE-禁用
     */
    private String status;

    /**
     * 应用次数
     */
    private Integer applyCount;

    /**
     * 最后应用时间
     */
    private java.time.LocalDateTime lastApplyTime;
}
