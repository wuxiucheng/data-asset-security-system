package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.TableId;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 敏感数据识别结果实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sensitive_ident_result")
public class SensitiveIdentResult extends BaseEntity {

    /**
     * 结果ID
     */
    @TableId
    private Long resultId;

    /**
     * 资产ID
     */
    private Long assetId;

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
     * 置信度分数(0-1)
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
     * 确认状态：PENDING/CONFIRMED/REJECTED
     */
    private String confirmStatus;

    /**
     * 确认人ID
     */
    private Long confirmerId;

    /**
     * 确认时间
     */
    private LocalDateTime confirmTime;

    /**
     * 确认备注
     */
    private String confirmRemark;
}
