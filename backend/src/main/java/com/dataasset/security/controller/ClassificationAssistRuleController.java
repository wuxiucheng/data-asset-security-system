package com.dataasset.security.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.entity.ClassificationAssistRule;
import com.dataasset.security.service.ClassificationAssistRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 分类分级辅助规则Controller
 */
@RestController
@RequestMapping("/classificationAssistRule")
public class ClassificationAssistRuleController {

    @Autowired
    private ClassificationAssistRuleService ruleService;

    /**
     * 分页查询
     */
    @PostMapping("/page")
    public Result<Page<ClassificationAssistRule>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestBody ClassificationAssistRule query) {
        Page<ClassificationAssistRule> page = new Page<>(pageNum, pageSize);
        return Result.success(ruleService.page(page, query));
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{ruleId}")
    public Result<ClassificationAssistRule> getById(@PathVariable Long ruleId) {
        return Result.success(ruleService.getById(ruleId));
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> save(@RequestBody ClassificationAssistRule rule) {
        ruleService.save(rule);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping
    public Result<Void> update(@RequestBody ClassificationAssistRule rule) {
        ruleService.updateById(rule);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ruleId}")
    public Result<Void> delete(@PathVariable Long ruleId) {
        ruleService.removeById(ruleId);
        return Result.success();
    }

    /**
     * 启用
     */
    @PutMapping("/{ruleId}/enable")
    public Result<Void> enable(@PathVariable Long ruleId) {
        ruleService.enable(ruleId);
        return Result.success();
    }

    /**
     * 禁用
     */
    @PutMapping("/{ruleId}/disable")
    public Result<Void> disable(@PathVariable Long ruleId) {
        ruleService.disable(ruleId);
        return Result.success();
    }

    /**
     * 获取启用的规则列表
     */
    @GetMapping("/active")
    public Result<?> getActiveRules() {
        return Result.success(ruleService.getActiveRules());
    }
}
