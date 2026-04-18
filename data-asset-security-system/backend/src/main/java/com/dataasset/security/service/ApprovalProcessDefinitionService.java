package com.dataasset.security.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dataasset.security.dto.ApprovalProcessDefinitionCreateDTO;
import com.dataasset.security.dto.ApprovalProcessDefinitionUpdateDTO;
import com.dataasset.security.entity.ApprovalProcessDefinition;

/**
 * 审批流程定义管理Service接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public interface ApprovalProcessDefinitionService extends IService<ApprovalProcessDefinition> {

    /**
     * 创建审批流程定义
     *
     * @param createDTO 创建审批流程定义请求
     * @return 流程定义ID
     */
    Long createApprovalProcessDefinition(ApprovalProcessDefinitionCreateDTO createDTO);

    /**
     * 更新审批流程定义
     *
     * @param updateDTO 更新审批流程定义请求
     */
    void updateApprovalProcessDefinition(ApprovalProcessDefinitionUpdateDTO updateDTO);

    /**
     * 删除审批流程定义
     *
     * @param definitionId 流程定义ID
     */
    void deleteApprovalProcessDefinition(Long definitionId);

    /**
     * 获取审批流程定义详情
     *
     * @param definitionId 流程定义ID
     * @return 流程定义详情
     */
    ApprovalProcessDefinition getApprovalProcessDefinitionDetail(Long definitionId);

    /**
     * 启用审批流程定义
     *
     * @param definitionId 流程定义ID
     */
    void enableApprovalProcessDefinition(Long definitionId);

    /**
     * 禁用审批流程定义
     *
     * @param definitionId 流程定义ID
     */
    void disableApprovalProcessDefinition(Long definitionId);

    /**
     * 获取流程图
     *
     * @param definitionId 流程定义ID
     * @return 流程图
     */
    String getProcessDiagram(Long definitionId);

    /**
     * 根据流程类型查询流程定义列表
     *
     * @param processType 流程类型
     * @return 流程定义列表
     */
    java.util.List<ApprovalProcessDefinition> getApprovalProcessDefinitionsByType(String processType);
}
