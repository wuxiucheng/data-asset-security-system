package com.dataasset.security.service;

import com.dataasset.security.entity.ClassificationAssistRule;
import com.dataasset.security.entity.DataField;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * 规则匹配引擎
 */
@Service
public class RuleMatchEngine {

    /**
     * 匹配规则
     */
    public boolean matchRule(ClassificationAssistRule rule, DataField field) {
        if (rule == null || field == null) {
            return false;
        }

        switch (rule.getRuleType()) {
            case "FIELD_NAME":
                return matchFieldName(rule.getFieldNamePattern(), field.getFieldName());
            case "FIELD_PATTERN":
                return matchFieldPattern(rule.getFieldValuePattern(), field);
            case "FIELD_SAMPLE":
                return matchSample(rule.getSampleMatchRule(), field);
            default:
                return false;
        }
    }

    /**
     * 字段名匹配（支持通配符）
     * 例如：*phone* 匹配 user_phone, phone_number, mobile_phone
     */
    private boolean matchFieldName(String pattern, String fieldName) {
        if (pattern == null || fieldName == null) {
            return false;
        }

        // 将通配符模式转换为正则表达式
        // * -> .*
        // 多个模式用逗号分隔
        String[] patterns = pattern.split(",");
        String fieldNameLower = fieldName.toLowerCase();

        for (String p : patterns) {
            String regex = p.trim()
                    .replace("*", ".*")
                    .toLowerCase();
            if (Pattern.matches(regex, fieldNameLower)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 字段值正则匹配
     */
    private boolean matchFieldPattern(String pattern, DataField field) {
        if (pattern == null) {
            return false;
        }

        // 这里可以扩展：从数据库读取样本数据进行匹配
        // 目前简化实现，仅检查字段名是否符合
        try {
            Pattern compiled = Pattern.compile(pattern);
            // 实际应用中，这里应该读取字段样本数据进行匹配
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 样本匹配
     */
    private boolean matchSample(String sampleRule, DataField field) {
        // TODO: 实现样本匹配逻辑
        // 1. 解析sampleRule JSON
        // 2. 从数据库读取字段样本数据
        // 3. 统计匹配比例
        return false;
    }
}
