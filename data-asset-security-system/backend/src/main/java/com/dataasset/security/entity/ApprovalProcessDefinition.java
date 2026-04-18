package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 审批流程定义实体类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("approval_process_definition")
public class ApprovalProcessDefinition extends BaseEntity implements Serializable {

    @TableId(value = "definition_id", type = IdType.AUTO)
    private Long definitionId;

    /**
     * 流程定义Key
     */
    private String processDefinitionKey;

    /**
     * 流程定义名称
     */
    private String processDefinitionName;

    /**
     * 流程定义描述
     */
    private String processDefinitionDescription;

    /**
     * 流程类型：CLASSIFICATION_APPROVAL, GRADING_APPROVAL, ASSET_APPROVAL, OTHER
     */
    private String processType;

    /**
     * 流程版本
     */
    private Integer version;

    /**
     * BPMN流程定义ID（Flowable）
     */
    private String bpmnProcessDefinitionId;

    /**
     * BPMN流程定义Key（Flowable）
     */
    private String bpmnProcessDefinitionKey;

    /**
     * 流程部署ID
     */
    private String deploymentId;

    /**
     * 流程资源名称
     */
    private String resourceName;

    /**
     * 流程资源内容（BPMN XML）
     */
    private String bpmnContent;

    /**
     * 流程图图片
     */
    private String processImage;

    /**
     * 状态：DRAFT, ACTIVE, INACTIVE, ARCHIVED
     */
    private String status;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 更新人ID
     */
    private Long updaterId;
}
