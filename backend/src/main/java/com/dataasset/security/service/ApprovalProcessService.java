package com.dataasset.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dataasset.security.entity.ApprovalProcessDefinition;
import com.dataasset.security.entity.ApprovalProcessInstance;
import com.dataasset.security.entity.ApprovalTask;

import java.util.List;

/**
 * 审批流程Service接口
 */
public interface ApprovalProcessService extends IService<ApprovalProcessInstance> {

    /**
     * 创建流程定义
     */
    ApprovalProcessDefinition createDefinition(ApprovalProcessDefinition definition);

    /**
     * 启动流程实例
     */
    ApprovalProcessInstance startProcess(Long definitionId, Long businessId, String businessType, String businessTitle);

    /**
     * 审批任务
     */
    void approveTask(Long taskId, String approvalResult, String approvalComment);

    /**
     * 获取我的待办任务
     */
    List<ApprovalTask> getMyPendingTasks(Long userId);

    /**
     * 获取我的已办任务
     */
    List<ApprovalTask> getMyCompletedTasks(Long userId);

    /**
     * 获取流程实例详情
     */
    ApprovalProcessInstance getInstanceDetail(Long instanceId);
}
