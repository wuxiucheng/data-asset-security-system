package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.TableId;
/**
 * 审批流程定义实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("approval_process_definition")
public class ApprovalProcessDefinition extends BaseEntity {

    @TableId
    private Long definitionId;
    private String processDefinitionKey;
    private String processDefinitionName;
    private String processDefinitionDescription;
    private String processType;
    private Integer version;
    private String bpmnProcessDefinitionId;
    private String bpmnProcessDefinitionKey;
    private String deploymentId;
    private String resourceName;
    private String bpmnContent;
    private String processImage;
    private String status;
    private Boolean enabled;
}
