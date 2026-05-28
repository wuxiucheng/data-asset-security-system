package com.dataasset.security.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.dto.SensitiveIdentRuleCreateDTO;
import com.dataasset.security.dto.SensitiveIdentRuleQueryDTO;
import com.dataasset.security.dto.SensitiveIdentRuleUpdateDTO;
import com.dataasset.security.entity.SensitiveIdentRule;
import com.dataasset.security.service.SensitiveIdentRuleService;
import com.dataasset.security.vo.SensitiveIdentRuleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 敏感识别规则Controller
 */
@Slf4j
@RestController
@RequestMapping("/sensitive-rule")
@Tag(name = "敏感识别规则管理", description = "敏感数据识别规则的增删改查")
public class SensitiveIdentRuleController {

    @Autowired
    private SensitiveIdentRuleService sensitiveIdentRuleService;

    @PostMapping("/page")
    @Operation(summary = "分页查询规则")
    public Result<Page<SensitiveIdentRuleVO>> queryPage(@RequestBody SensitiveIdentRuleQueryDTO queryDTO) {
        Page<SensitiveIdentRuleVO> page = sensitiveIdentRuleService.queryPage(queryDTO);
        return Result.success(page);
    }

    @GetMapping("/list")
    @Operation(summary = "获取所有启用的规则")
    public Result<List<SensitiveIdentRule>> listEnabled() {
        List<SensitiveIdentRule> list = sensitiveIdentRuleService.listEnabled();
        return Result.success(list);
    }

    @GetMapping("/{ruleId}")
    @Operation(summary = "获取规则详情")
    public Result<SensitiveIdentRule> getById(@PathVariable Long ruleId) {
        SensitiveIdentRule rule = sensitiveIdentRuleService.getById(ruleId);
        return Result.success(rule);
    }

    @PostMapping
    @Operation(summary = "创建规则")
    public Result<SensitiveIdentRule> create(@RequestBody SensitiveIdentRuleCreateDTO createDTO) {
        SensitiveIdentRule rule = sensitiveIdentRuleService.create(createDTO);
        return Result.success(rule);
    }

    @PutMapping
    @Operation(summary = "更新规则")
    public Result<SensitiveIdentRule> update(@RequestBody SensitiveIdentRuleUpdateDTO updateDTO) {
        SensitiveIdentRule rule = sensitiveIdentRuleService.update(updateDTO);
        return Result.success(rule);
    }

    @DeleteMapping("/{ruleId}")
    @Operation(summary = "删除规则")
    public Result<Void> delete(@PathVariable Long ruleId) {
        sensitiveIdentRuleService.delete(ruleId);
        return Result.success();
    }

    @PutMapping("/{ruleId}/status")
    @Operation(summary = "启用/禁用规则")
    public Result<Void> updateStatus(@PathVariable Long ruleId, @RequestParam String status) {
        sensitiveIdentRuleService.updateStatus(ruleId, status);
        return Result.success();
    }

    @PostMapping("/init-builtin")
    @Operation(summary = "初始化内置规则")
    public Result<Void> initBuiltinRules() {
        sensitiveIdentRuleService.initBuiltinRules();
        return Result.success();
    }

    @Autowired
    private com.dataasset.security.service.SensitiveIdentEngineService engineService;

    @PostMapping("/scan/asset/{assetId}")
    @Operation(summary = "扫描资产")
    public Result<Void> scanAsset(@PathVariable Long assetId) {
        engineService.scanAsset(assetId);
        return Result.success();
    }

    @PostMapping("/scan/field/{fieldId}")
    @Operation(summary = "扫描字段")
    public Result<SensitiveIdentRule> scanField(@RequestParam Long assetId, @PathVariable Long fieldId) {
        com.dataasset.security.entity.SensitiveIdentResult result = engineService.scanField(assetId, fieldId);
        return Result.success(result != null ? sensitiveIdentRuleService.getById(result.getRuleId()) : null);
    }

    @GetMapping("/result/asset/{assetId}")
    @Operation(summary = "获取资产识别结果")
    public Result<List<com.dataasset.security.entity.SensitiveIdentResult>> getResultsByAsset(@PathVariable Long assetId) {
        return Result.success(engineService.getResultsByAsset(assetId));
    }

    @GetMapping("/result/field/{fieldId}")
    @Operation(summary = "获取字段识别结果")
    public Result<List<com.dataasset.security.entity.SensitiveIdentResult>> getResultsByField(@PathVariable Long fieldId) {
        return Result.success(engineService.getResultsByField(fieldId));
    }

    @PutMapping("/result/{resultId}/confirm")
    @Operation(summary = "确认识别结果")
    public Result<Void> confirmResult(@PathVariable Long resultId, 
                                      @RequestParam String confirmStatus,
                                      @RequestParam(required = false) String confirmRemark) {
        engineService.confirmResult(resultId, confirmStatus, confirmRemark);
        return Result.success();
    }
}
