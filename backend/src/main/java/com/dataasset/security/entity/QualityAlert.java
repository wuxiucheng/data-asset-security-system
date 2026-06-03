package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;

/**
 * 质量告警实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("quality_alert")
public class QualityAlert extends BaseEntity {

    /**
     * 告警ID
     */
    @TableId
    private Long alertId;

    /**
     * 探查任务ID
     */
    private Long taskId;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 字段ID
     */
    private Long fieldId;

    /**
     * 规则ID
     */
    private Long ruleId;

    /**
     * 告警类型
     */
    private String alertType;

    /**
     * 告警级别：INFO/WARNING/ERROR/CRITICAL
     */
    private String alertLevel;

    /**
     * 告警标题
     */
    private String alertTitle;

    /**
     * 告警内容
     */
    private String alertContent;

    /**
     * 告警状态：PENDING/PROCESSING/RESOLVED/IGNORED
     */
    private String alertStatus;

    /**
     * 处理人ID
     */
    private Long assigneeId;

    /**
     * 解决时间
     */
    private LocalDateTime resolvedTime;

    /**
     * 解决备注
     */
    private String resolvedRemark;
}
