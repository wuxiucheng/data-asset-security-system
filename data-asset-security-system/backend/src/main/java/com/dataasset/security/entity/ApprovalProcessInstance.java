package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;

/**
 * 审批流程实例实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("approval_process_instance")
public class ApprovalProcessInstance extends BaseEntity {

    @TableId
    private Long instanceId;
    private Long definitionId;
    private String processInstanceId;
    private String processDefinitionKey;
    private String processDefinitionName;
    private String processType;
    private String businessType;
    private Long businessId;
    private String businessTitle;
    private Long applicantId;
    private String applicantName;
    private Long applicantDepartmentId;
    private String applicantDepartmentName;
    private LocalDateTime applyTime;
    private String currentTaskId;
    private String currentTaskName;
    private Long currentAssigneeId;
    private String currentAssigneeName;
    private String processStatus;
    private String approvalResult;
    private String approvalComment;
    private LocalDateTime completedTime;
    private String processVariables;
    private String remarks;
}
