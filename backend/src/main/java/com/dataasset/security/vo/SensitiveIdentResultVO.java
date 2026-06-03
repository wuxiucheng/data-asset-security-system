package com.dataasset.security.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 敏感识别结果VO
 */
@Data
public class SensitiveIdentResultVO {

    /**
     * 结果ID
     */
    private Long resultId;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 资产名称
     */
    private String assetName;

    /**
     * 字段ID
     */
    private Long fieldId;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 匹配的规则ID
     */
    private Long ruleId;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 敏感类型
     */
    private String sensitiveType;

    /**
     * 匹配模式
     */
    private String matchMode;

    /**
     * 置信度分数
     */
    private BigDecimal confidenceScore;

    /**
     * 匹配详情
     */
    private String matchDetail;

    /**
     * 识别时间
     */
    private LocalDateTime identifyTime;

    /**
     * 确认状态
     */
    private String confirmStatus;

    /**
     * 确认人ID
     */
    private Long confirmerId;

    /**
     * 确认人姓名
     */
    private String confirmerName;

    /**
     * 确认时间
     */
    private LocalDateTime confirmTime;

    /**
     * 确认备注
     */
    private String confirmRemark;
}
