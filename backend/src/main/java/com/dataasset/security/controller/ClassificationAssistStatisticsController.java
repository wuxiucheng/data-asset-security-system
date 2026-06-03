package com.dataasset.security.controller;

import com.dataasset.security.common.result.Result;
import com.dataasset.security.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/classificationAssistStatistics")
@Tag(name = "分类辅助统计", description = "分类辅助统计接口")
public class ClassificationAssistStatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 测试接口
     */
    @GetMapping("/test")
    @Operation(summary = "测试接口")
    public Result<String> test() {
        return Result.success("StatisticsController is working!");
    }

    /**
     * 获取规则效果统计
     */
    @GetMapping("/rule")
    @Operation(summary = "获取规则效果统计")
    public Result<Map<String, Object>> getRuleStatistics() {
        return Result.success(statisticsService.getRuleStatistics());
    }

    /**
     * 获取任务执行统计
     */
    @GetMapping("/task")
    @Operation(summary = "获取任务执行统计")
    public Result<Map<String, Object>> getTaskStatistics() {
        return Result.success(statisticsService.getTaskStatistics());
    }

    /**
     * 获取审核统计
     */
    @GetMapping("/review")
    @Operation(summary = "获取审核统计")
    public Result<Map<String, Object>> getReviewStatistics() {
        return Result.success(statisticsService.getReviewStatistics());
    }

    /**
     * 获取综合统计
     */
    @GetMapping("/overview")
    @Operation(summary = "获取综合统计")
    public Result<Map<String, Object>> getOverview() {
        return Result.success(statisticsService.getOverview());
    }
}
