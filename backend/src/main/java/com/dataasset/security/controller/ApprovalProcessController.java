package com.dataasset.security.controller;

import com.dataasset.security.common.result.Result;
import com.dataasset.security.entity.ApprovalProcessDefinition;
import com.dataasset.security.entity.ApprovalProcessInstance;
import com.dataasset.security.entity.ApprovalTask;
import com.dataasset.security.service.ApprovalProcessService;
import com.dataasset.security.mapper.ApprovalProcessDefinitionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 审批流程Controller
 */
@Slf4j
@RestController
@RequestMapping("/approval")
@Tag(name = "审批流程管理", description = "审批流程的创建、执行、查询")
public class ApprovalProcessController {

    @Autowired
    private ApprovalProcessService approvalProcessService;

    @Autowired
    private ApprovalProcessDefinitionMapper definitionMapper;

    // ==================== 流程定义 ====================

    @GetMapping("/definition/list")
    @Operation(summary = "获取所有流程定义")
    public Result<List<ApprovalProcessDefinition>> listDefinitions() {
        return Result.success(definitionMapper.selectList(null));
    }

    @PostMapping("/definition")
    @Operation(summary = "创建流程定义")
    public Result<ApprovalProcessDefinition> createDefinition(@RequestBody ApprovalProcessDefinition definition) {
        ApprovalProcessDefinition created = approvalProcessService.createDefinition(definition);
        return Result.success(created);
    }

    @PutMapping("/definition/{definitionId}/enable")
    @Operation(summary = "启用流程定义")
    public Result<Void> enableDefinition(@PathVariable Long definitionId) {
        ApprovalProcessDefinition definition = definitionMapper.selectById(definitionId);
        definition.setEnabled(true);
        definition.setStatus("ACTIVE");
        definitionMapper.updateById(definition);
        return Result.success();
    }

    // ==================== 流程实例 ====================

    @PostMapping("/instance/start")
    @Operation(summary = "启动流程实例")
    public Result<ApprovalProcessInstance> startProcess(
            @RequestParam Long definitionId,
            @RequestParam Long businessId,
            @RequestParam String businessType,
            @RequestParam String businessTitle) {
        ApprovalProcessInstance instance = approvalProcessService.startProcess(definitionId, businessId, businessType, businessTitle);
        return Result.success(instance);
    }

    @GetMapping("/instance/{instanceId}")
    @Operation(summary = "获取流程实例详情")
    public Result<ApprovalProcessInstance> getInstanceDetail(@PathVariable Long instanceId) {
        return Result.success(approvalProcessService.getInstanceDetail(instanceId));
    }

    // ==================== 审批任务 ====================

    @GetMapping("/task/pending")
    @Operation(summary = "获取我的待办任务")
    public Result<List<ApprovalTask>> getMyPendingTasks(@RequestParam Long userId) {
        return Result.success(approvalProcessService.getMyPendingTasks(userId));
    }

    @GetMapping("/task/completed")
    @Operation(summary = "获取我的已办任务")
    public Result<List<ApprovalTask>> getMyCompletedTasks(@RequestParam Long userId) {
        return Result.success(approvalProcessService.getMyCompletedTasks(userId));
    }

    @PostMapping("/task/{taskId}/approve")
    @Operation(summary = "审批任务")
    public Result<Void> approveTask(
            @PathVariable Long taskId,
            @RequestParam String approvalResult,
            @RequestParam(required = false) String approvalComment) {
        approvalProcessService.approveTask(taskId, approvalResult, approvalComment);
        return Result.success();
    }
}
