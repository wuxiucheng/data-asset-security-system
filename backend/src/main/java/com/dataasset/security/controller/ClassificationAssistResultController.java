package com.dataasset.security.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.entity.ClassificationAssistResult;
import com.dataasset.security.service.ClassificationAssistResultService;
import com.dataasset.security.service.GradingApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classificationAssistResult")
public class ClassificationAssistResultController {

    @Autowired
    private ClassificationAssistResultService resultService;

    @Autowired
    private GradingApplicationService gradingService;

    /**
     * 分页查询结果
     */
    @PostMapping("/page")
    public Result<Page<ClassificationAssistResult>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long ruleId) {
        return Result.success(resultService.page(current, size, status, ruleId));
    }

    /**
     * 获取结果详情
     */
    @GetMapping("/{resultId}")
    public Result<ClassificationAssistResult> getById(@PathVariable Long resultId) {
        return Result.success(resultService.getById(resultId));
    }

    /**
     * 批准分级建议
     */
    @PostMapping("/{resultId}/approve")
    public Result<Boolean> approve(
            @PathVariable Long resultId,
            @RequestParam(required = false) String comment) {
        // TODO: 从上下文获取当前用户ID
        Long reviewerId = 1L;
        resultService.approve(resultId, reviewerId, comment);
        return Result.success(true);
    }

    /**
     * 拒绝分级建议
     */
    @PostMapping("/{resultId}/reject")
    public Result<Boolean> reject(
            @PathVariable Long resultId,
            @RequestParam(required = false) String comment) {
        // TODO: 从上下文获取当前用户ID
        Long reviewerId = 1L;
        resultService.reject(resultId, reviewerId, comment);
        return Result.success(true);
    }

    /**
     * 批量审核
     */
    @PostMapping("/batchReview")
    public Result<Boolean> batchReview(
            @RequestBody List<Long> resultIds,
            @RequestParam String status,
            @RequestParam(required = false) String comment) {
        // TODO: 从上下文获取当前用户ID
        Long reviewerId = 1L;
        resultService.batchReview(resultIds, status, reviewerId, comment);
        return Result.success(true);
    }

    /**
     * 获取待审核数量
     */
    @GetMapping("/pendingCount")
    public Result<Long> getPendingCount() {
        return Result.success((long) resultService.getPendingResults().size());
    }

    /**
     * 应用分级到字段
     */
    @PostMapping("/{resultId}/apply")
    public Result<Boolean> applyGrading(@PathVariable Long resultId) {
        return Result.success(gradingService.applyGrading(resultId));
    }

    /**
     * 批量应用分级
     */
    @PostMapping("/batchApply")
    public Result<Integer> batchApplyGrading(@RequestBody List<Long> resultIds) {
        return Result.success(gradingService.batchApplyGrading(resultIds));
    }
}
