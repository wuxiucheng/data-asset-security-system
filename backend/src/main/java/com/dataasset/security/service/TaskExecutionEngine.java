package com.dataasset.security.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dataasset.security.entity.ClassificationAssistResult;
import com.dataasset.security.entity.ClassificationAssistRule;
import com.dataasset.security.entity.ClassificationAssistTask;
import com.dataasset.security.entity.DataField;
import com.dataasset.security.mapper.DataFieldMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 任务执行引擎
 */
@Service
public class TaskExecutionEngine {

    private static final Logger logger = LoggerFactory.getLogger(TaskExecutionEngine.class);

    @Autowired
    private ClassificationAssistTaskService taskService;

    @Autowired
    private ClassificationAssistResultService resultService;

    @Autowired
    private ClassificationAssistRuleService ruleService;

    @Autowired
    private RuleMatchEngine matchEngine;

    @Autowired
    private DataFieldMapper fieldMapper;

    /**
     * 异步执行任务
     */
    @Async
    public void executeTask(Long taskId) {
        logger.info("开始执行任务: {}", taskId);

        ClassificationAssistTask task = taskService.getById(taskId);
        if (task == null) {
            logger.error("任务不存在: {}", taskId);
            return;
        }

        try {
            // 更新任务状态为执行中
            taskService.startTask(taskId);

            // 获取执行范围
            List<DataField> fields = getFieldsByScope(task);
            logger.info("任务 {} 扫描字段数: {}", taskId, fields.size());

            // 获取启用的规则
            List<ClassificationAssistRule> rules = getRules(task);
            logger.info("任务 {} 使用规则数: {}", taskId, rules.size());

            // 执行匹配
            String executeBatch = UUID.randomUUID().toString();
            List<ClassificationAssistResult> results = new ArrayList<>();
            int matchedCount = 0;

            for (DataField field : fields) {
                for (ClassificationAssistRule rule : rules) {
                    if (matchEngine.matchRule(rule, field)) {
                        // 创建匹配结果
                        ClassificationAssistResult result = new ClassificationAssistResult();
                        result.setRuleId(rule.getRuleId());
                        result.setAssetId(field.getAssetId());
                        result.setFieldId(field.getFieldId());
                        result.setMatchType(rule.getRuleType());
                        result.setMatchValue(field.getFieldName());
                        result.setOriginalGradingId(field.getGradingId() != null ? field.getGradingId().longValue() : null);
                        result.setSuggestGradingId(rule.getSuggestGradingId());
                        result.setGradingChanged(field.getGradingId() == null ||
                                !field.getGradingId().equals(rule.getSuggestGradingId().intValue()) ? 1 : 0);
                        result.setStatus("PENDING");
                        result.setExecuteTime(LocalDateTime.now());
                        result.setExecuteBatch(executeBatch);
                        result.setCreatedTime(LocalDateTime.now());

                        results.add(result);
                        matchedCount++;

                        // 批量保存，每100条保存一次
                        if (results.size() >= 100) {
                            resultService.saveBatch(results);
                            results.clear();
                        }
                    }
                }
            }

            // 保存剩余结果
            if (!results.isEmpty()) {
                resultService.saveBatch(results);
            }

            // 完成任务
            taskService.completeTask(taskId, fields.size(), matchedCount);
            logger.info("任务 {} 执行完成，匹配数: {}", taskId, matchedCount);

        } catch (Exception e) {
            logger.error("任务 {} 执行失败: {}", taskId, e.getMessage(), e);
            taskService.failTask(taskId, e.getMessage());
        }
    }

    /**
     * 根据范围获取字段
     */
    private List<DataField> getFieldsByScope(ClassificationAssistTask task) {
        LambdaQueryWrapper<DataField> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataField::getDeleted, 0);

        String scopeType = task.getScopeType();
        if ("DATASOURCE".equals(scopeType)) {
            // 指定数据源
            // TODO: 解析scopeConfig获取数据源ID
        } else if ("ASSET".equals(scopeType)) {
            // 指定资产
            // TODO: 解析scopeConfig获取资产ID
        }
        // ALL: 全部资产，不需要额外条件

        return fieldMapper.selectList(wrapper);
    }

    /**
     * 获取执行的规则
     */
    private List<ClassificationAssistRule> getRules(ClassificationAssistTask task) {
        if (task.getRuleIds() != null && !task.getRuleIds().isEmpty()) {
            // 指定规则
            List<Long> ruleIds = Arrays.stream(task.getRuleIds().split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            return ruleService.listByIds(ruleIds);
        } else {
            // 使用所有启用的规则
            return ruleService.getActiveRules();
        }
    }
}
