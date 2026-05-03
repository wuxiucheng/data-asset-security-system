package com.dataasset.security.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.entity.*;
import com.dataasset.security.mapper.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 统计分析控制器
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Tag(name = "统计分析", description = "资产统计和趋势分析")
public class StatisticsController {

    private final DataAssetMapper dataAssetMapper;
    private final DepartmentMapper departmentMapper;
    private final DataClassificationMapper dataClassificationMapper;
    private final DataGradingMapper dataGradingMapper;

    /**
     * 获取资产统计信息
     */
    @GetMapping("/asset")
    @Operation(summary = "资产统计", description = "获取资产统计概览")
    public Result<Map<String, Object>> getAssetStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总资产数
        Long totalAssets = dataAssetMapper.selectCount(new LambdaQueryWrapper<DataAsset>().eq(DataAsset::getDeleted, 0));
        stats.put("totalAssets", totalAssets);
        
        // 按部门统计
        List<Map<String, Object>> byDepartment = new ArrayList<>();
        List<Department> departments = departmentMapper.selectList(new LambdaQueryWrapper<Department>().eq(Department::getDeleted, 0));
        for (Department dept : departments) {
            Long count = dataAssetMapper.selectCount(new LambdaQueryWrapper<DataAsset>()
                .eq(DataAsset::getDepartmentId, dept.getDepartmentId())
                .eq(DataAsset::getDeleted, 0));
            if (count > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", dept.getDepartmentName());
                item.put("value", count);
                byDepartment.add(item);
            }
        }
        stats.put("byDepartment", byDepartment);
        
        // 按分类统计
        List<Map<String, Object>> byClassification = new ArrayList<>();
        List<DataClassification> classifications = dataClassificationMapper.selectList(new LambdaQueryWrapper<DataClassification>().eq(DataClassification::getDeleted, 0));
        for (DataClassification c : classifications) {
            Long count = dataAssetMapper.selectCount(new LambdaQueryWrapper<DataAsset>()
                .eq(DataAsset::getClassificationId, c.getClassificationId())
                .eq(DataAsset::getDeleted, 0));
            if (count > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", c.getClassificationName());
                item.put("value", count);
                byClassification.add(item);
            }
        }
        stats.put("byClassification", byClassification);
        
        // 按分级统计
        List<Map<String, Object>> byGrading = new ArrayList<>();
        List<DataGrading> gradings = dataGradingMapper.selectList(new LambdaQueryWrapper<DataGrading>().eq(DataGrading::getDeleted, 0));
        for (DataGrading g : gradings) {
            Long count = dataAssetMapper.selectCount(new LambdaQueryWrapper<DataAsset>()
                .eq(DataAsset::getGradingId, g.getGradingId())
                .eq(DataAsset::getDeleted, 0));
            if (count > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", g.getGradingName());
                item.put("value", count);
                byGrading.add(item);
            }
        }
        stats.put("byGrading", byGrading);
        
        // 按状态统计
        List<Map<String, Object>> byStatus = new ArrayList<>();
        String[] statuses = {"DRAFT", "ACTIVE", "INACTIVE"};
        for (String status : statuses) {
            Long count = dataAssetMapper.selectCount(new LambdaQueryWrapper<DataAsset>()
                .eq(DataAsset::getStatus, status)
                .eq(DataAsset::getDeleted, 0));
            if (count > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", status);
                item.put("value", count);
                byStatus.add(item);
            }
        }
        stats.put("byStatus", byStatus);
        
        return Result.success(stats);
    }

    /**
     * 获取趋势数据
     */
    @GetMapping("/trend")
    @Operation(summary = "趋势分析", description = "获取资产增长趋势")
    public Result<Map<String, Object>> getTrendData(@RequestParam(required = false) String type) {
        Map<String, Object> trendData = new HashMap<>();
        
        // 模拟最近7天的趋势数据
        List<String> dates = new ArrayList<>();
        List<Integer> assetGrowth = new ArrayList<>();
        List<Integer> classificationGrowth = new ArrayList<>();
        List<Integer> gradingGrowth = new ArrayList<>();
        
        Long totalAssets = dataAssetMapper.selectCount(new LambdaQueryWrapper<DataAsset>().eq(DataAsset::getDeleted, 0));
        Long totalClassifications = dataClassificationMapper.selectCount(new LambdaQueryWrapper<DataClassification>().eq(DataClassification::getDeleted, 0));
        Long totalGradings = dataGradingMapper.selectCount(new LambdaQueryWrapper<DataGrading>().eq(DataGrading::getDeleted, 0));
        
        for (int i = 6; i >= 0; i--) {
            dates.add(java.time.LocalDate.now().minusDays(i).toString());
            assetGrowth.add(totalAssets.intValue());
            classificationGrowth.add(totalClassifications.intValue());
            gradingGrowth.add(totalGradings.intValue());
        }
        
        trendData.put("dates", dates);
        trendData.put("assetGrowth", assetGrowth);
        trendData.put("classificationGrowth", classificationGrowth);
        trendData.put("gradingGrowth", gradingGrowth);
        
        return Result.success(trendData);
    }
}
