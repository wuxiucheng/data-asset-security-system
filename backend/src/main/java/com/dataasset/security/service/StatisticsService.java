package com.dataasset.security.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dataasset.security.entity.*;
import com.dataasset.security.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 统计分析服务
 */
@Service
public class StatisticsService {

    @Autowired
    private ClassificationAssistRuleMapper ruleMapper;

    @Autowired
    private ClassificationAssistTaskMapper taskMapper;

    @Autowired
    private ClassificationAssistResultMapper resultMapper;

    @Autowired
    private DataAssetMapper dataAssetMapper;

    @Autowired
    private DataClassificationMapper dataClassificationMapper;

    @Autowired
    private DataGradingMapper dataGradingMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    /**
     * 获取规则效果统计
     */
    public Map<String, Object> getRuleStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 规则总数
        Long totalRules = ruleMapper.selectCount(
                new LambdaQueryWrapper<ClassificationAssistRule>()
                        .eq(ClassificationAssistRule::getDeleted, 0)
        );
        stats.put("totalRules", totalRules);

        // 启用的规则数
        Long activeRules = ruleMapper.selectCount(
                new LambdaQueryWrapper<ClassificationAssistRule>()
                        .eq(ClassificationAssistRule::getDeleted, 0)
                        .eq(ClassificationAssistRule::getStatus, "ACTIVE")
        );
        stats.put("activeRules", activeRules);

        // 各规则的匹配次数
        List<Map<String, Object>> ruleMatchCounts = new ArrayList<>();
        List<ClassificationAssistRule> rules = ruleMapper.selectList(
                new LambdaQueryWrapper<ClassificationAssistRule>()
                        .eq(ClassificationAssistRule::getDeleted, 0)
        );

        for (ClassificationAssistRule rule : rules) {
            Long matchCount = resultMapper.selectCount(
                    new LambdaQueryWrapper<ClassificationAssistResult>()
                            .eq(ClassificationAssistResult::getRuleId, rule.getRuleId())
            );
            Map<String, Object> ruleStat = new HashMap<>();
            ruleStat.put("ruleId", rule.getRuleId());
            ruleStat.put("ruleName", rule.getRuleName());
            ruleStat.put("matchCount", matchCount);
            ruleMatchCounts.add(ruleStat);
        }
        stats.put("ruleMatchCounts", ruleMatchCounts);

        return stats;
    }

    /**
     * 获取任务执行统计
     */
    public Map<String, Object> getTaskStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 任务总数
        Long totalTasks = taskMapper.selectCount(
                new LambdaQueryWrapper<ClassificationAssistTask>()
                        .eq(ClassificationAssistTask::getDeleted, 0)
        );
        stats.put("totalTasks", totalTasks);

        // 各状态任务数
        Map<String, Long> statusCounts = new HashMap<>();
        String[] statuses = {"PENDING", "RUNNING", "COMPLETED", "FAILED"};
        for (String status : statuses) {
            Long count = taskMapper.selectCount(
                    new LambdaQueryWrapper<ClassificationAssistTask>()
                            .eq(ClassificationAssistTask::getDeleted, 0)
                            .eq(ClassificationAssistTask::getStatus, status)
            );
            statusCounts.put(status, count);
        }
        stats.put("statusCounts", statusCounts);

        return stats;
    }

    /**
     * 获取审核统计
     */
    public Map<String, Object> getReviewStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 结果总数
        Long totalResults = resultMapper.selectCount(null);
        stats.put("totalResults", totalResults);

        // 各状态结果数
        Map<String, Long> statusCounts = new HashMap<>();
        String[] statuses = {"PENDING", "APPROVED", "REJECTED"};
        for (String status : statuses) {
            Long count = resultMapper.selectCount(
                    new LambdaQueryWrapper<ClassificationAssistResult>()
                            .eq(ClassificationAssistResult::getStatus, status)
            );
            statusCounts.put(status, count);
        }
        stats.put("statusCounts", statusCounts);

        // 审核通过率
        Long approved = statusCounts.getOrDefault("APPROVED", 0L);
        Long rejected = statusCounts.getOrDefault("REJECTED", 0L);
        if (approved + rejected > 0) {
            double approvalRate = (double) approved / (approved + rejected) * 100;
            stats.put("approvalRate", String.format("%.2f", approvalRate));
        } else {
            stats.put("approvalRate", "0.00");
        }

        return stats;
    }

    /**
     * 获取综合统计
     */
    public Map<String, Object> getOverview() {
        Map<String, Object> overview = new HashMap<>();

        // 资产总数
        Long totalAssets = dataAssetMapper.selectCount(
                new LambdaQueryWrapper<DataAsset>()
                        .eq(DataAsset::getDeleted, 0)
        );
        overview.put("totalAssets", totalAssets);

        // 按部门分布
        List<Map<String, Object>> byDepartment = new ArrayList<>();
        List<DataAsset> assets = dataAssetMapper.selectList(
                new LambdaQueryWrapper<DataAsset>()
                        .eq(DataAsset::getDeleted, 0)
                        .select(DataAsset::getDepartmentId)
        );
        
        Map<Long, Long> deptCountMap = new HashMap<>();
        for (DataAsset asset : assets) {
            Long deptId = asset.getDepartmentId();
            deptCountMap.put(deptId, deptCountMap.getOrDefault(deptId, 0L) + 1);
        }
        
        for (Map.Entry<Long, Long> entry : deptCountMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            Department dept = departmentMapper.selectById(entry.getKey());
            item.put("name", dept != null ? dept.getDepartmentName() : "未知部门");
            item.put("value", entry.getValue());
            byDepartment.add(item);
        }
        overview.put("byDepartment", byDepartment);

        // 按分类分布
        List<Map<String, Object>> byClassification = new ArrayList<>();
        List<DataAsset> assetsWithClassification = dataAssetMapper.selectList(
                new LambdaQueryWrapper<DataAsset>()
                        .eq(DataAsset::getDeleted, 0)
                        .isNotNull(DataAsset::getClassificationId)
                        .select(DataAsset::getClassificationId)
        );
        
        Map<Long, Long> classificationCountMap = new HashMap<>();
        for (DataAsset asset : assetsWithClassification) {
            Long classificationId = asset.getClassificationId();
            if (classificationId != null) {
                classificationCountMap.put(classificationId, classificationCountMap.getOrDefault(classificationId, 0L) + 1);
            }
        }
        
        for (Map.Entry<Long, Long> entry : classificationCountMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            DataClassification classification = dataClassificationMapper.selectById(entry.getKey());
            item.put("name", classification != null ? classification.getClassificationName() : "未分类");
            item.put("value", entry.getValue());
            byClassification.add(item);
        }
        overview.put("byClassification", byClassification);

        // 按分级分布
        List<Map<String, Object>> byGrading = new ArrayList<>();
        List<DataAsset> assetsWithGrading = dataAssetMapper.selectList(
                new LambdaQueryWrapper<DataAsset>()
                        .eq(DataAsset::getDeleted, 0)
                        .isNotNull(DataAsset::getGradingId)
                        .select(DataAsset::getGradingId)
        );
        
        Map<Long, Long> gradingCountMap = new HashMap<>();
        for (DataAsset asset : assetsWithGrading) {
            Long gradingId = asset.getGradingId();
            if (gradingId != null) {
                gradingCountMap.put(gradingId, gradingCountMap.getOrDefault(gradingId, 0L) + 1);
            }
        }
        
        for (Map.Entry<Long, Long> entry : gradingCountMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            DataGrading grading = dataGradingMapper.selectById(entry.getKey());
            item.put("name", grading != null ? grading.getGradingName() : "未分级");
            item.put("value", entry.getValue());
            byGrading.add(item);
        }
        overview.put("byGrading", byGrading);

        // 按状态分布
        List<Map<String, Object>> byStatus = new ArrayList<>();
        String[] statuses = {"DRAFT", "ACTIVE", "INACTIVE", "ARCHIVED"};
        for (String status : statuses) {
            Long count = dataAssetMapper.selectCount(
                    new LambdaQueryWrapper<DataAsset>()
                            .eq(DataAsset::getDeleted, 0)
                            .eq(DataAsset::getStatus, status)
            );
            if (count > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", status);
                item.put("value", count);
                byStatus.add(item);
            }
        }
        overview.put("byStatus", byStatus);

        return overview;
    }
}
