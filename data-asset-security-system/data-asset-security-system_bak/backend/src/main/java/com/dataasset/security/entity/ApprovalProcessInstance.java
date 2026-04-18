package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 审批流程实例实体类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("approval_process_instance")
public class ApprovalProcessInstance extends BaseEntity implements Serializable {

    @TableId(value = "instance_id", type = IdType.AUTO)
    private Long instanceId;

    /**
     * 流程定义ID
     */
    private Long definitionId;

    /**
     * 流程实例ID（Flowable）
     */
    private String processInstanceId;

    /**
     * 流程定义Key
     */
    private String processDefinitionKey;

    /**
     * 流程定义名称
     */
    private String processDefinitionName;

    /**
     * 流程类型：CLASSIFICATION_APPROVAL, GRADING_APPROVAL, ASSET_APPROVAL, OTHER
     */
    private String processType;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 业务ID
     */
    private Long businessId;

    /**
     * 业务标题
     */
    private String businessTitle;

    /**
     * 申请人ID
     */
    private Long applicantId;

    /**
     * 申请人姓名
     */
    private String applicantName;

    /**
     * 申请部门ID
     */
    private Long applicantDepartmentId;

    /**
     * 申请部门名称
     */
    private String applicantDepartmentName;

    /**
     * 申请时间
     */
    private LocalDateTime applyTime;

    /**
     * 当前节点ID
     */
    private String currentTaskId;

    /**
     * 当前节点名称
     */
    private String currentTaskName;

    /**
     * 当前处理人ID
     */
    private Long currentAssigneeId;

    /**
     * 当前处理人姓名
     */
    private String currentAssigneeName;

    /**
     * 流程状态：RUNNING, COMPLETED, CANCELLED, SUSPENDED
     */
    private String processStatus;

    /**
     * 审批结果：PENDING, APPROVED, REJECTED, CANCELLED
     */
    private String approvalResult;

    /**
     * 审批意见
     */
    private String approvalComment;

    /**
     * 完成时间
     */
    private LocalDateTime completedTime;

    /**
     * 流程变量（JSON格式）
     */
    private String processVariables;

    /**
     * 备注
     */
    private String remarks;
}
