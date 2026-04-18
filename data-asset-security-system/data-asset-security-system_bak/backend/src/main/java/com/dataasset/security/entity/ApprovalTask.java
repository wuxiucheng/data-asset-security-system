package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 审批任务实体类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("approval_task")
public class ApprovalTask extends BaseEntity implements Serializable {

    @TableId(value = "task_id", type = IdType.AUTO)
    private Long taskId;

    /**
     * 流程实例ID
     */
    private Long instanceId;

    /**
     * 流程实例ID（Flowable）
     */
    private String processInstanceId;

    /**
     * 任务ID（Flowable）
     */
    private String taskIdFlowable;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务定义Key
     */
    private String taskDefinitionKey;

    /**
     * 处理人ID
     */
    private Long assigneeId;

    /**
     * 处理人姓名
     */
    private String assigneeName;

    /**
     * 候选人组
     */
    private String candidateGroups;

    /**
     * 任务状态：PENDING, COMPLETED, DELEGATED, CANCELLED
     */
    private String taskStatus;

    /**
     * 任务开始时间
     */
    private LocalDateTime taskStartTime;

    /**
     * 任务完成时间
     */
    private LocalDateTime taskEndTime;

    /**
     * 审批结果：PENDING, APPROVED, REJECTED, DELEGATED
     */
    private String approvalResult;

    /**
     * 审批意见
     */
    private String approvalComment;

    /**
     * 优先级：LOW, NORMAL, HIGH, URGENT
     */
    private String priority;

    /**
     * 到期时间
     */
    private LocalDateTime dueDate;

    /**
     * 任务变量（JSON格式）
     */
    private String taskVariables;
}
