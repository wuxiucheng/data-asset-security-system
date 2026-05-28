package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;

/**
 * 质量探查任务实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("quality_probe_task")
public class QualityProbeTask extends BaseEntity {

    /**
     * 任务ID
     */
    @TableId
    private Long taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 探查范围：ASSET/CLASSIFICATION/GRADING
     */
    private String scopeType;

    /**
     * 范围ID列表(逗号分隔)
     */
    private String scopeIds;

    /**
     * 执行规则ID列表(逗号分隔)，空则自动选择
     */
    private String ruleIds;

    /**
     * 执行模式：IMMEDIATE/SCHEDULED
     */
    private String executeMode;

    /**
     * 定时表达式
     */
    private String scheduleCron;

    /**
     * 状态：PENDING/RUNNING/COMPLETED/FAILED/CANCELLED
     */
    private String taskStatus;

    /**
     * 进度(0-100)
     */
    private Integer progress;

    /**
     * 总规则数
     */
    private Integer totalRules;

    /**
     * 已完成规则数
     */
    private Integer completedRules;

    /**
     * 开始时间
     */
    private LocalDateTime startedTime;

    /**
     * 完成时间
     */
    private LocalDateTime completedTime;

    /**
     * 错误信息
     */
    private String errorMessage;
}
