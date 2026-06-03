package com.dataasset.security.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.entity.QualityRule;
import com.dataasset.security.entity.QualityProbeTask;
import com.dataasset.security.entity.QualityProbeResult;
import com.dataasset.security.entity.QualityAlert;
import com.dataasset.security.service.QualityProbeService;
import com.dataasset.security.service.QualityAlertService;
import com.dataasset.security.mapper.QualityRuleMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据质量探查Controller
 */
@Slf4j
@RestController
@RequestMapping("/quality-probe")
@Tag(name = "数据质量探查", description = "数据质量探查管理")
public class QualityProbeController {

    @Autowired
    private QualityProbeService qualityProbeService;

    @Autowired
    private QualityAlertService qualityAlertService;

    @Autowired
    private QualityRuleMapper qualityRuleMapper;

    // ==================== 质量规则管理 ====================

    @GetMapping("/rule/list")
    @Operation(summary = "获取所有质量规则")
    public Result<List<QualityRule>> listRules() {
        return Result.success(qualityRuleMapper.selectList(null));
    }

    @PostMapping("/rule")
    @Operation(summary = "创建质量规则")
    public Result<QualityRule> createRule(@RequestBody QualityRule rule) {
        qualityRuleMapper.insert(rule);
        return Result.success(rule);
    }

    @PutMapping("/rule")
    @Operation(summary = "更新质量规则")
    public Result<QualityRule> updateRule(@RequestBody QualityRule rule) {
        qualityRuleMapper.updateById(rule);
        return Result.success(rule);
    }

    @DeleteMapping("/rule/{ruleId}")
    @Operation(summary = "删除质量规则")
    public Result<Void> deleteRule(@PathVariable Long ruleId) {
        qualityRuleMapper.deleteById(ruleId);
        return Result.success();
    }

    @PostMapping("/rule/init-default")
    @Operation(summary = "初始化默认规则")
    public Result<Void> initDefaultRules() {
        qualityProbeService.initDefaultRules();
        return Result.success();
    }

    // ==================== 探查任务管理 ====================

    @PostMapping("/task")
    @Operation(summary = "创建探查任务")
    public Result<QualityProbeTask> createTask(@RequestBody QualityProbeTask task) {
        QualityProbeTask created = qualityProbeService.createTask(task);
        return Result.success(created);
    }

    @PostMapping("/task/{taskId}/execute")
    @Operation(summary = "执行探查任务")
    public Result<Void> executeTask(@PathVariable Long taskId) {
        qualityProbeService.executeTask(taskId);
        return Result.success();
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "获取任务详情")
    public Result<QualityProbeTask> getTask(@PathVariable Long taskId) {
        return Result.success(qualityProbeService.getById(taskId));
    }

    @GetMapping("/task/{taskId}/results")
    @Operation(summary = "获取任务结果")
    public Result<List<QualityProbeResult>> getTaskResults(@PathVariable Long taskId) {
        return Result.success(qualityProbeService.getTaskResults(taskId));
    }

    // ==================== 质量报告 ====================

    @GetMapping("/report/asset/{assetId}")
    @Operation(summary = "获取资产质量报告")
    public Result<List<QualityProbeResult>> getAssetQualityReport(@PathVariable Long assetId) {
        return Result.success(qualityProbeService.getAssetQualityReport(assetId));
    }

    // ==================== 质量告警管理 ====================

    @GetMapping("/alert/page")
    @Operation(summary = "分页查询告警")
    public Result<Page<QualityAlert>> queryAlertPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) String alertLevel,
            @RequestParam(required = false) String alertStatus) {
        Page<QualityAlert> page = new Page<>(current, size);
        Page<QualityAlert> result = qualityAlertService.queryPage(page, taskId, alertLevel, alertStatus);
        return Result.success(result);
    }

    @GetMapping("/alert/task/{taskId}")
    @Operation(summary = "根据任务ID获取告警列表")
    public Result<List<QualityAlert>> getAlertsByTaskId(@PathVariable Long taskId) {
        return Result.success(qualityAlertService.getByTaskId(taskId));
    }

    @GetMapping("/alert/pending-count")
    @Operation(summary = "获取待处理告警数量")
    public Result<Long> getPendingAlertCount() {
        return Result.success(qualityAlertService.getPendingCount());
    }

    @GetMapping("/alert/my-pending")
    @Operation(summary = "获取我的待处理告警")
    public Result<List<QualityAlert>> getMyPendingAlerts(@RequestParam Long userId) {
        return Result.success(qualityAlertService.getMyPendingAlerts(userId));
    }

    @GetMapping("/alert/{alertId}")
    @Operation(summary = "获取告警详情")
    public Result<QualityAlert> getAlertById(@PathVariable Long alertId) {
        return Result.success(qualityAlertService.getById(alertId));
    }

    @PostMapping("/alert")
    @Operation(summary = "创建告警")
    public Result<QualityAlert> createAlert(@RequestBody QualityAlert alert) {
        QualityAlert created = qualityAlertService.create(alert);
        return Result.success(created);
    }

    @PutMapping("/alert/{alertId}/assign")
    @Operation(summary = "分配告警处理人")
    public Result<Void> assignAlert(@PathVariable Long alertId, @RequestParam Long assigneeId) {
        qualityAlertService.assign(alertId, assigneeId);
        return Result.success();
    }

    @PutMapping("/alert/{alertId}/resolve")
    @Operation(summary = "解决告警")
    public Result<Void> resolveAlert(
            @PathVariable Long alertId,
            @RequestParam(required = false) String resolvedRemark) {
        qualityAlertService.resolve(alertId, resolvedRemark);
        return Result.success();
    }

    @PutMapping("/alert/{alertId}/ignore")
    @Operation(summary = "忽略告警")
    public Result<Void> ignoreAlert(
            @PathVariable Long alertId,
            @RequestParam(required = false) String resolvedRemark) {
        qualityAlertService.ignore(alertId, resolvedRemark);
        return Result.success();
    }

    @PutMapping("/alert/batch-resolve")
    @Operation(summary = "批量解决告警")
    public Result<Void> batchResolveAlerts(
            @RequestBody List<Long> alertIds,
            @RequestParam(required = false) String resolvedRemark) {
        qualityAlertService.batchResolve(alertIds, resolvedRemark);
        return Result.success();
    }
}
