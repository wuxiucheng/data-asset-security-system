package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;

/**
 * 审批任务实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("approval_task")
public class ApprovalTask extends BaseEntity {

    @TableId
    private Long taskId;
    private Long instanceId;
    private String processInstanceId;
    private String taskIdFlowable;
    private String taskName;
    private String taskDefinitionKey;
    private Long assigneeId;
    private String assigneeName;
    private String candidateGroups;
    private String taskStatus;
    private LocalDateTime taskStartTime;
    private LocalDateTime taskEndTime;
    private String approvalResult;
    private String approvalComment;
    private String priority;
    private LocalDateTime dueDate;
    private String taskVariables;
}
