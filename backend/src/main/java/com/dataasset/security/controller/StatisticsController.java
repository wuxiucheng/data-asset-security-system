package com.dataasset.security.controller;

import com.dataasset.security.common.result.Result;
import com.dataasset.security.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/statistics")
@Tag(name = "统计分析", description = "数据统计和分析接口")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取趋势数据
     */
    @GetMapping("/trend")
    @Operation(summary = "获取趋势数据")
    public Result<Map<String, Object>> getTrendData(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String type
    ) {
        Map<String, Object> trendData = new HashMap<>();
        
        // 模拟趋势数据（实际应从数据库查询）
        String[] months = {"1月", "2月", "3月", "4月", "5月", "6月"};
        int[] assetCounts = {120, 150, 180, 220, 280, 350};
        int[] classificationCounts = {80, 100, 130, 160, 200, 250};
        int[] gradingCounts = {60, 80, 100, 130, 170, 220};
        
        // 日期数组
        List<String> dates = new ArrayList<>();
        for (String month : months) {
            dates.add(month);
        }
        trendData.put("dates", dates);
        
        // 资产增长数组
        List<Integer> assetGrowth = new ArrayList<>();
        for (int count : assetCounts) {
            assetGrowth.add(count);
        }
        trendData.put("assetGrowth", assetGrowth);
        
        // 分类增长数组
        List<Integer> classificationGrowth = new ArrayList<>();
        for (int count : classificationCounts) {
            classificationGrowth.add(count);
        }
        trendData.put("classificationGrowth", classificationGrowth);
        
        // 分级增长数组
        List<Integer> gradingGrowth = new ArrayList<>();
        for (int count : gradingCounts) {
            gradingGrowth.add(count);
        }
        trendData.put("gradingGrowth", gradingGrowth);
        
        return Result.success(trendData);
    }
}
