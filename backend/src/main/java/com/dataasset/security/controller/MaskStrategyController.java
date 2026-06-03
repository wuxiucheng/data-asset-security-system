package com.dataasset.security.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.entity.MaskStrategy;
import com.dataasset.security.entity.MaskWhitelist;
import com.dataasset.security.service.MaskStrategyService;
import com.dataasset.security.service.MaskWhitelistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Autowired
    private MaskWhitelistService maskWhitelistService;

    @GetMapping("/list")
    @Operation(summary = "获取所有策略")
    public Result<List<MaskStrategy>> list() {
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

    // ==================== 白名单管理 ====================

    @GetMapping("/whitelist/page")
    @Operation(summary = "分页查询白名单")
    public Result<Page<MaskWhitelist>> queryWhitelistPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long strategyId,
            @RequestParam(required = false) String whitelistType,
            @RequestParam(required = false) String status) {
        Page<MaskWhitelist> page = new Page<>(current, size);
        Page<MaskWhitelist> result = maskWhitelistService.queryPage(page, strategyId, whitelistType, status);
        return Result.success(result);
    }

    @GetMapping("/whitelist/strategy/{strategyId}")
    @Operation(summary = "根据策略ID获取白名单列表")
    public Result<List<MaskWhitelist>> getWhitelistByStrategyId(@PathVariable Long strategyId) {
        return Result.success(maskWhitelistService.getByStrategyId(strategyId));
    }

    @GetMapping("/whitelist/{whitelistId}")
    @Operation(summary = "获取白名单详情")
    public Result<MaskWhitelist> getWhitelistById(@PathVariable Long whitelistId) {
        return Result.success(maskWhitelistService.getById(whitelistId));
    }

    @PostMapping("/whitelist")
    @Operation(summary = "创建白名单")
    public Result<MaskWhitelist> createWhitelist(@RequestBody MaskWhitelist whitelist) {
        MaskWhitelist created = maskWhitelistService.create(whitelist);
        return Result.success(created);
    }

    @PutMapping("/whitelist")
    @Operation(summary = "更新白名单")
    public Result<MaskWhitelist> updateWhitelist(@RequestBody MaskWhitelist whitelist) {
        MaskWhitelist updated = maskWhitelistService.update(whitelist);
        return Result.success(updated);
    }

    @DeleteMapping("/whitelist/{whitelistId}")
    @Operation(summary = "删除白名单")
    public Result<Void> deleteWhitelist(@PathVariable Long whitelistId) {
        maskWhitelistService.delete(whitelistId);
        return Result.success();
    }

    @PutMapping("/whitelist/{whitelistId}/status")
    @Operation(summary = "启用/禁用白名单")
    public Result<Void> updateWhitelistStatus(@PathVariable Long whitelistId, @RequestParam String status) {
        maskWhitelistService.updateStatus(whitelistId, status);
        return Result.success();
    }

    @GetMapping("/whitelist/check")
    @Operation(summary = "检查用户是否在白名单中")
    public Result<Boolean> checkWhitelist(
            @RequestParam Long strategyId,
            @RequestParam Long userId,
            @RequestParam(required = false) List<Long> roleIds) {
        boolean inWhitelist = maskWhitelistService.isInWhitelist(strategyId, userId, roleIds);
        return Result.success(inWhitelist);
    }
}
