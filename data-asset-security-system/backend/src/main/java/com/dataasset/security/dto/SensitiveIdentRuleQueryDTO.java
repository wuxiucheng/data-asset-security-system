package com.dataasset.security.dto;

import lombok.Data;

/**
 * 敏感识别规则查询DTO
 */
@Data
public class SensitiveIdentRuleQueryDTO {

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 敏感类型
     */
    private String sensitiveType;

    /**
     * 匹配模式
     */
    private String matchMode;

    /**
     * 是否内置规则
     */
    private Boolean isBuiltin;

    /**
     * 状态
     */
    private String status;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}
