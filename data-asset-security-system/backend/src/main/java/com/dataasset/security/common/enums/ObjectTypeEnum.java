package com.dataasset.security.common.enums;

/**
 * 操作对象类型枚举
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public enum ObjectTypeEnum {

    /**
     * 数据资产
     */
    DATA_ASSET("DATA_ASSET", "数据资产"),

    /**
     * 数据字段
     */
    DATA_FIELD("DATA_FIELD", "数据字段"),

    /**
     * 数据源配置
     */
    DATA_SOURCE("DATA_SOURCE", "数据源配置"),

    /**
     * 部门
     */
    DEPARTMENT("DEPARTMENT", "部门"),

    /**
     * 责任人
     */
    OWNER("OWNER", "责任人"),

    /**
     * 分类
     */
    CLASSIFICATION("CLASSIFICATION", "分类"),

    /**
     * 数据分类
     */
    DATA_CLASSIFICATION("DATA_CLASSIFICATION", "数据分类"),

    /**
     * 分类标准
     */
    CLASSIFICATION_STANDARD("CLASSIFICATION_STANDARD", "分类标准"),

    /**
     * 分级标准
     */
    GRADING_STANDARD("GRADING_STANDARD", "分级标准"),

    /**
     * 组织
     */
    ORGANIZATION("ORGANIZATION", "组织"),

    /**
     * 审批
     */
    APPROVAL("APPROVAL", "审批"),

    /**
     * 用户
     */
    USER("USER", "用户"),

    /**
     * 角色
     */
    ROLE("ROLE", "角色"),

    /**
     * 权限
     */
    PERMISSION("PERMISSION", "权限"),

    /**
     * 数据分级
     */
    DATA_GRADING("DATA_GRADING", "数据分级"),

    /**
     * 会话
     */
    SESSION("SESSION", "会话"),

    /**
     * 报告
     */
    REPORT("REPORT", "报告"),

    /**
     * 审计日志
     */
    AUDIT_LOG("AUDIT_LOG", "审计日志"),

    /**
     * 其他
     */
    OTHER("OTHER", "其他");

    private final String code;
    private final String description;

    ObjectTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
