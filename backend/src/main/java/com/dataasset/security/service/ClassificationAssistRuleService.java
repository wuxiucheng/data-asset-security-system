package com.dataasset.security.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.entity.ClassificationAssistRule;
import com.dataasset.security.mapper.ClassificationAssistRuleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类分级辅助规则Service
 */
@Service
public class ClassificationAssistRuleService extends ServiceImpl<ClassificationAssistRuleMapper, ClassificationAssistRule> {

    /**
     * 分页查询规则
     */
    public Page<ClassificationAssistRule> page(Page<ClassificationAssistRule> page, ClassificationAssistRule query) {
        LambdaQueryWrapper<ClassificationAssistRule> wrapper = new LambdaQueryWrapper<>();
        
        if (query.getRuleName() != null && !query.getRuleName().isEmpty()) {
            wrapper.like(ClassificationAssistRule::getRuleName, query.getRuleName());
        }
        if (query.getRuleCode() != null && !query.getRuleCode().isEmpty()) {
            wrapper.like(ClassificationAssistRule::getRuleCode, query.getRuleCode());
        }
        if (query.getRuleType() != null && !query.getRuleType().isEmpty()) {
            wrapper.eq(ClassificationAssistRule::getRuleType, query.getRuleType());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(ClassificationAssistRule::getStatus, query.getStatus());
        }
        
        wrapper.orderByAsc(ClassificationAssistRule::getPriority);
        return page(page, wrapper);
    }

    /**
     * 获取启用的规则列表（按优先级排序）
     */
    public List<ClassificationAssistRule> getActiveRules() {
        LambdaQueryWrapper<ClassificationAssistRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassificationAssistRule::getStatus, "ACTIVE")
               .orderByAsc(ClassificationAssistRule::getPriority);
        return list(wrapper);
    }

    /**
     * 启用规则
     */
    @Transactional
    public void enable(Long ruleId) {
        ClassificationAssistRule rule = getById(ruleId);
        if (rule != null) {
            rule.setStatus("ACTIVE");
            updateById(rule);
        }
    }

    /**
     * 禁用规则
     */
    @Transactional
    public void disable(Long ruleId) {
        ClassificationAssistRule rule = getById(ruleId);
        if (rule != null) {
            rule.setStatus("INACTIVE");
            updateById(rule);
        }
    }

    /**
     * 更新应用统计
     */
    @Transactional
    public void updateApplyStats(Long ruleId) {
        ClassificationAssistRule rule = getById(ruleId);
        if (rule != null) {
            rule.setApplyCount(rule.getApplyCount() + 1);
            rule.setLastApplyTime(LocalDateTime.now());
            updateById(rule);
        }
    }
}
