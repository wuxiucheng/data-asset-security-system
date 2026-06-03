package com.dataasset.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.entity.*;
import com.dataasset.security.mapper.*;
import com.dataasset.security.service.QualityProbeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据质量探查Service实现
 */
@Slf4j
@Service
public class QualityProbeServiceImpl extends ServiceImpl<QualityProbeTaskMapper, QualityProbeTask> implements QualityProbeService {

    @Autowired
    private QualityRuleMapper ruleMapper;

    @Autowired
    private QualityProbeResultMapper resultMapper;

    @Autowired
    private DataAssetMapper assetMapper;

    @Autowired
    private DataFieldMapper fieldMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QualityProbeTask createTask(QualityProbeTask task) {
        task.setTaskStatus("PENDING");
        task.setProgress(0);
        task.setCompletedRules(0);
        this.save(task);
        return task;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeTask(Long taskId) {
        QualityProbeTask task = this.getById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        // 更新任务状态
        task.setTaskStatus("RUNNING");
        task.setStartedTime(LocalDateTime.now());
        this.updateById(task);

        try {
            // 获取要执行的规则
            List<QualityRule> rules = getRules(task);
            task.setTotalRules(rules.size());
            this.updateById(task);

            // 执行每个规则
            int completed = 0;
            for (QualityRule rule : rules) {
                executeRule(task, rule);
                completed++;
                task.setCompletedRules(completed);
                task.setProgress((int) ((completed * 100.0) / rules.size()));
                this.updateById(task);
            }

            // 更新任务完成状态
            task.setTaskStatus("COMPLETED");
            task.setCompletedTime(LocalDateTime.now());
            this.updateById(task);

        } catch (Exception e) {
            task.setTaskStatus("FAILED");
            task.setErrorMessage(e.getMessage());
            this.updateById(task);
            log.error("质量探查任务执行失败", e);
        }
    }

    @Override
    public List<QualityProbeResult> getTaskResults(Long taskId) {
        return resultMapper.selectList(new LambdaQueryWrapper<QualityProbeResult>()
                .eq(QualityProbeResult::getTaskId, taskId));
    }

    @Override
    public List<QualityProbeResult> getAssetQualityReport(Long assetId) {
        return resultMapper.selectList(new LambdaQueryWrapper<QualityProbeResult>()
                .eq(QualityProbeResult::getAssetId, assetId)
                .orderByDesc(QualityProbeResult::getProbeTime));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initDefaultRules() {
        long count = ruleMapper.selectCount(null);
        if (count > 0) {
            log.info("质量规则已存在，跳过初始化");
            return;
        }

        // 完整性规则
        createRule("字段完整性检查", "COMPLETENESS", 
                "NOT_NULL", "GLOBAL", null, 
                new BigDecimal("95.00"), 10, 
                "检查字段是否为空");

        // 唯一性规则
        createRule("主键唯一性检查", "UNIQUENESS", 
                "PRIMARY_KEY", "GLOBAL", null, 
                new BigDecimal("100.00"), 10, 
                "检查主键是否唯一");

        // 格式规则
        createRule("日期格式检查", "FORMAT", 
                "DATE_FORMAT", "GLOBAL", null, 
                new BigDecimal("90.00"), 8, 
                "检查日期格式是否正确");

        createRule("邮箱格式检查", "FORMAT", 
                "EMAIL_FORMAT", "GLOBAL", null, 
                new BigDecimal("90.00"), 8, 
                "检查邮箱格式是否正确");

        // 值范围规则
        createRule("数值范围检查", "VALUE_RANGE", 
                "NUMBER_RANGE", "GLOBAL", null, 
                new BigDecimal("95.00"), 7, 
                "检查数值是否在合理范围内");

        log.info("默认质量规则初始化完成");
    }

    private List<QualityRule> getRules(QualityProbeTask task) {
        if (task.getRuleIds() != null && !task.getRuleIds().isEmpty()) {
            String[] ids = task.getRuleIds().split(",");
            List<Long> ruleIdList = new ArrayList<>();
            for (String id : ids) {
                ruleIdList.add(Long.parseLong(id.trim()));
            }
            return ruleMapper.selectBatchIds(ruleIdList);
        } else {
            return ruleMapper.selectList(new LambdaQueryWrapper<QualityRule>()
                    .eq(QualityRule::getStatus, "ENABLED"));
        }
    }

    private void executeRule(QualityProbeTask task, QualityRule rule) {
        // TODO: 实现具体的质量检查逻辑
        // 这里简化处理，实际需要根据规则类型执行不同的检查
        
        QualityProbeResult result = new QualityProbeResult();
        result.setTaskId(task.getTaskId());
        result.setRuleId(rule.getRuleId());
        result.setRuleName(rule.getRuleName());
        result.setProbeTime(LocalDateTime.now());
        
        // 模拟检查结果
        result.setTotalCount(100L);
        result.setPassCount(95L);
        result.setFailCount(5L);
        result.setPassRate(new BigDecimal("95.00"));
        result.setIsQualified(result.getPassRate().compareTo(rule.getThreshold()) >= 0);
        
        resultMapper.insert(result);
    }

    private void createRule(String name, String type, String expression, 
                           String scopeType, Long scopeId, 
                           BigDecimal threshold, Integer weight, 
                           String description) {
        QualityRule rule = new QualityRule();
        rule.setRuleName(name);
        rule.setRuleType(type);
        rule.setRuleExpression(expression);
        rule.setScopeType(scopeType);
        rule.setScopeId(scopeId);
        rule.setThreshold(threshold);
        rule.setWeight(weight);
        rule.setDescription(description);
        rule.setStatus("ENABLED");
        ruleMapper.insert(rule);
    }
}
