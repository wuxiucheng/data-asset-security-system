package com.dataasset.security.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.entity.QualityRule;
import com.dataasset.security.entity.QualityProbeTask;
import com.dataasset.security.entity.QualityProbeResult;
import com.dataasset.security.service.QualityProbeService;
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
}
