package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.TableId;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 质量探查结果实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("quality_probe_result")
public class QualityProbeResult extends BaseEntity {

    /**
     * 结果ID
     */
    @TableId
    private Long resultId;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 规则ID
     */
    private Long ruleId;

    /**
     * 规则名称
     */
    private String ruleName;

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
     * 总记录数
     */
    private Long totalCount;

    /**
     * 合格记录数
     */
    private Long passCount;

    /**
     * 不合格记录数
     */
    private Long failCount;

    /**
     * 合格率(0-100)
     */
    private BigDecimal passRate;

    /**
     * 是否达标
     */
    private Boolean isQualified;

    /**
     * 探查时间
     */
    private LocalDateTime probeTime;

    /**
     * 详细信息
     */
    private String detail;
}
