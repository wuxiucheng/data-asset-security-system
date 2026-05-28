package com.dataasset.security.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.entity.MaskStrategy;
import com.dataasset.security.service.MaskStrategyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 脱敏策略Controller
 */
@Slf4j
@RestController
@RequestMapping("/mask-strategy")
@Tag(name = "脱敏策略管理", description = "脱敏策略的增删改查")
public class MaskStrategyController {

    @Autowired
    private MaskStrategyService maskStrategyService;

    @GetMapping("/list")
    @Operation(summary = "获取所有策略")
    public Result<java.util.List<MaskStrategy>> list() {
        return Result.success(maskStrategyService.list());
    }

    @GetMapping("/{strategyId}")
    @Operation(summary = "获取策略详情")
    public Result<MaskStrategy> getById(@PathVariable Long strategyId) {
        return Result.success(maskStrategyService.getById(strategyId));
    }

    @GetMapping("/type/{sensitiveType}")
    @Operation(summary = "根据敏感类型获取策略")
    public Result<MaskStrategy> getBySensitiveType(@PathVariable String sensitiveType) {
        return Result.success(maskStrategyService.getBySensitiveType(sensitiveType));
    }

    @PostMapping
    @Operation(summary = "创建策略")
    public Result<MaskStrategy> create(@RequestBody MaskStrategy strategy) {
        maskStrategyService.save(strategy);
        return Result.success(strategy);
    }

    @PutMapping
    @Operation(summary = "更新策略")
    public Result<MaskStrategy> update(@RequestBody MaskStrategy strategy) {
        maskStrategyService.updateById(strategy);
        return Result.success(strategy);
    }

    @DeleteMapping("/{strategyId}")
    @Operation(summary = "删除策略")
    public Result<Void> delete(@PathVariable Long strategyId) {
        maskStrategyService.removeById(strategyId);
        return Result.success();
    }

    @PostMapping("/init-default")
    @Operation(summary = "初始化默认策略")
    public Result<Void> initDefaultStrategies() {
        maskStrategyService.initDefaultStrategies();
        return Result.success();
    }

    @PostMapping("/apply")
    @Operation(summary = "应用脱敏")
    public Result<String> applyMask(@RequestParam String value,
                                    @RequestParam String sensitiveType,
                                    @RequestParam String algorithm,
                                    @RequestParam(required = false) String params) {
        String masked = maskStrategyService.applyMask(value, sensitiveType, algorithm, params);
        return Result.success(masked);
    }
}
