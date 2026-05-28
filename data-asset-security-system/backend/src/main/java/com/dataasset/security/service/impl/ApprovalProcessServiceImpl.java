package com.dataasset.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.entity.*;
import com.dataasset.security.mapper.*;
import com.dataasset.security.service.ApprovalProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审批流程Service实现
 */
@Slf4j
@Service
public class ApprovalProcessServiceImpl extends ServiceImpl<ApprovalProcessInstanceMapper, ApprovalProcessInstance> implements ApprovalProcessService {

    @Autowired
    private ApprovalProcessDefinitionMapper definitionMapper;

    @Autowired
    private ApprovalTaskMapper taskMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApprovalProcessDefinition createDefinition(ApprovalProcessDefinition definition) {
        definition.setStatus("DRAFT");
        definition.setEnabled(false);
        definitionMapper.insert(definition);
        return definition;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApprovalProcessInstance startProcess(Long definitionId, Long businessId, String businessType, String businessTitle) {
        ApprovalProcessDefinition definition = definitionMapper.selectById(definitionId);
        if (definition == null) {
            throw new RuntimeException("流程定义不存在");
        }

        ApprovalProcessInstance instance = new ApprovalProcessInstance();
        instance.setDefinitionId(definitionId);
        instance.setProcessDefinitionKey(definition.getProcessDefinitionKey());
        instance.setProcessDefinitionName(definition.getProcessDefinitionName());
        instance.setProcessType(definition.getProcessType());
        instance.setBusinessId(businessId);
        instance.setBusinessType(businessType);
        instance.setBusinessTitle(businessTitle);
        instance.setApplyTime(LocalDateTime.now());
        instance.setProcessStatus("RUNNING");
        instance.setApprovalResult("PENDING");
        
        // TODO: 设置申请人信息
        // instance.setApplicantId(getCurrentUserId());
        // instance.setApplicantName(getCurrentUserName());
        
        this.save(instance);

        // 创建第一个审批任务
        createFirstTask(instance);
        
        log.info("流程实例创建成功, instanceId={}", instance.getInstanceId());
        return instance;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveTask(Long taskId, String approvalResult, String approvalComment) {
        ApprovalTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        task.setApprovalResult(approvalResult);
        task.setApprovalComment(approvalComment);
        task.setTaskStatus("COMPLETED");
        task.setTaskEndTime(LocalDateTime.now());
        taskMapper.updateById(task);

        // 更新流程实例状态
        ApprovalProcessInstance instance = this.getById(task.getInstanceId());
        if ("APPROVED".equals(approvalResult)) {
            // TODO: 判断是否还有下一个任务
            instance.setProcessStatus("COMPLETED");
            instance.setApprovalResult("APPROVED");
            instance.setApprovalComment(approvalComment);
            instance.setCompletedTime(LocalDateTime.now());
        } else if ("REJECTED".equals(approvalResult)) {
            instance.setProcessStatus("COMPLETED");
            instance.setApprovalResult("REJECTED");
            instance.setApprovalComment(approvalComment);
            instance.setCompletedTime(LocalDateTime.now());
        }
        this.updateById(instance);
    }

    @Override
    public List<ApprovalTask> getMyPendingTasks(Long userId) {
        return taskMapper.selectList(new LambdaQueryWrapper<ApprovalTask>()
                .eq(ApprovalTask::getAssigneeId, userId)
                .eq(ApprovalTask::getTaskStatus, "PENDING")
                .orderByAsc(ApprovalTask::getDueDate));
    }

    @Override
    public List<ApprovalTask> getMyCompletedTasks(Long userId) {
        return taskMapper.selectList(new LambdaQueryWrapper<ApprovalTask>()
                .eq(ApprovalTask::getAssigneeId, userId)
                .eq(ApprovalTask::getTaskStatus, "COMPLETED")
                .orderByDesc(ApprovalTask::getTaskEndTime));
    }

    @Override
    public ApprovalProcessInstance getInstanceDetail(Long instanceId) {
        return this.getById(instanceId);
    }

    private void createFirstTask(ApprovalProcessInstance instance) {
        ApprovalTask task = new ApprovalTask();
        task.setInstanceId(instance.getInstanceId());
        task.setTaskName("审批");
        task.setTaskStatus("PENDING");
        task.setTaskStartTime(LocalDateTime.now());
        task.setPriority("NORMAL");
        
        // TODO: 设置审批人
        // task.setAssigneeId(getApproverId());
        // task.setAssigneeName(getApproverName());
        
        taskMapper.insert(task);
        
        instance.setCurrentTaskId(task.getTaskId().toString());
        instance.setCurrentTaskName(task.getTaskName());
        this.updateById(instance);
    }
}
