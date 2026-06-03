package com.dataasset.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.entity.DataField;
import com.dataasset.security.entity.SensitiveIdentResult;
import com.dataasset.security.entity.SensitiveIdentRule;
import com.dataasset.security.mapper.DataFieldMapper;
import com.dataasset.security.mapper.SensitiveIdentResultMapper;
import com.dataasset.security.service.SensitiveIdentEngineService;
import com.dataasset.security.service.SensitiveIdentRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 敏感数据识别引擎Service实现
 */
@Slf4j
@Service
public class SensitiveIdentEngineServiceImpl extends ServiceImpl<SensitiveIdentResultMapper, SensitiveIdentResult> implements SensitiveIdentEngineService {

    @Autowired
    private SensitiveIdentRuleService ruleService;

    @Autowired
    private DataFieldMapper fieldMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void scanAsset(Long assetId) {
        // 获取资产的所有字段
        List<DataField> fields = fieldMapper.selectList(new LambdaQueryWrapper<DataField>()
                .eq(DataField::getAssetId, assetId));
        
        for (DataField field : fields) {
            scanField(assetId, field.getFieldId());
        }
        
        log.info("资产扫描完成, assetId={}, 字段数={}", assetId, fields.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SensitiveIdentResult scanField(Long assetId, Long fieldId) {
        // 获取字段信息
        DataField field = fieldMapper.selectById(fieldId);
        if (field == null) {
            throw new RuntimeException("字段不存在");
        }
        
        // 删除旧的识别结果
        this.remove(new LambdaQueryWrapper<SensitiveIdentResult>()
                .eq(SensitiveIdentResult::getFieldId, fieldId));
        
        // 获取所有启用的规则
        List<SensitiveIdentRule> rules = ruleService.listEnabled();
        
        // 匹配规则
        SensitiveIdentResult bestResult = null;
        BigDecimal maxScore = BigDecimal.ZERO;
        
        for (SensitiveIdentRule rule : rules) {
            BigDecimal score = matchRule(field, rule);
            if (score != null && score.compareTo(BigDecimal.ZERO) > 0) {
                // 综合置信度 = 匹配分数 * 规则置信度权重
                BigDecimal finalScore = score.multiply(rule.getConfidenceWeight());
                
                if (finalScore.compareTo(maxScore) > 0) {
                    maxScore = finalScore;
                    bestResult = createResult(assetId, field, rule, finalScore);
                }
            }
        }
        
        // 保存最佳匹配结果
        if (bestResult != null) {
            this.save(bestResult);
            log.info("字段识别完成, fieldId={}, fieldName={}, sensitiveType={}, score={}", 
                    fieldId, field.getFieldName(), bestResult.getSensitiveType(), maxScore);
        }
        
        return bestResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchScanAssets(List<Long> assetIds) {
        for (Long assetId : assetIds) {
            try {
                scanAsset(assetId);
            } catch (Exception e) {
                log.error("资产扫描失败, assetId={}", assetId, e);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmResult(Long resultId, String confirmStatus, String confirmRemark) {
        SensitiveIdentResult result = this.getById(resultId);
        if (result == null) {
            throw new RuntimeException("识别结果不存在");
        }
        
        result.setConfirmStatus(confirmStatus);
        result.setConfirmRemark(confirmRemark);
        result.setConfirmTime(LocalDateTime.now());
        // TODO: 设置确认人ID
        // result.setConfirmerId(getCurrentUserId());
        
        this.updateById(result);
    }

    @Override
    public List<SensitiveIdentResult> getResultsByAsset(Long assetId) {
        return this.list(new LambdaQueryWrapper<SensitiveIdentResult>()
                .eq(SensitiveIdentResult::getAssetId, assetId)
                .orderByDesc(SensitiveIdentResult::getConfidenceScore));
    }

    @Override
    public List<SensitiveIdentResult> getResultsByField(Long fieldId) {
        return this.list(new LambdaQueryWrapper<SensitiveIdentResult>()
                .eq(SensitiveIdentResult::getFieldId, fieldId)
                .orderByDesc(SensitiveIdentResult::getConfidenceScore));
    }

    /**
     * 匹配规则
     */
    private BigDecimal matchRule(DataField field, SensitiveIdentRule rule) {
        String matchMode = rule.getMatchMode();
        String expression = rule.getMatchExpression();
        
        switch (matchMode) {
            case "FIELD_NAME":
                return matchFieldName(field.getFieldName(), expression);
            case "REGEX":
                return matchRegex(field.getFieldName(), expression);
            case "SAMPLE":
                // TODO: 样本匹配需要实际数据
                return null;
            default:
                return null;
        }
    }

    /**
     * 字段名匹配
     */
    private BigDecimal matchFieldName(String fieldName, String expression) {
        if (fieldName == null || expression == null) {
            return null;
        }
        
        // 表达式是逗号分隔的字段名列表
        String[] names = expression.toLowerCase().split(",");
        String lowerFieldName = fieldName.toLowerCase();
        
        for (String name : names) {
            name = name.trim();
            // 完全匹配
            if (lowerFieldName.equals(name)) {
                return BigDecimal.ONE;
            }
            // 包含匹配
            if (lowerFieldName.contains(name) || name.contains(lowerFieldName)) {
                return new BigDecimal("0.8");
            }
        }
        
        return null;
    }

    /**
     * 正则表达式匹配
     */
    private BigDecimal matchRegex(String fieldName, String expression) {
        if (fieldName == null || expression == null) {
            return null;
        }
        
        try {
            // 对字段名进行正则匹配
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            if (pattern.matcher(fieldName).find()) {
                return BigDecimal.ONE;
            }
        } catch (Exception e) {
            log.warn("正则表达式匹配失败, pattern={}, error={}", expression, e.getMessage());
        }
        
        return null;
    }

    /**
     * 创建识别结果
     */
    private SensitiveIdentResult createResult(Long assetId, DataField field, 
                                              SensitiveIdentRule rule, BigDecimal score) {
        SensitiveIdentResult result = new SensitiveIdentResult();
        result.setAssetId(assetId);
        result.setFieldId(field.getFieldId());
        result.setFieldName(field.getFieldName());
        result.setRuleId(rule.getRuleId());
        result.setRuleName(rule.getRuleName());
        result.setSensitiveType(rule.getSensitiveType());
        result.setMatchMode(rule.getMatchMode());
        result.setConfidenceScore(score);
        result.setMatchDetail("匹配规则: " + rule.getRuleName());
        result.setIdentifyTime(LocalDateTime.now());
        result.setConfirmStatus("PENDING");
        return result;
    }
}
