package com.dataasset.security.common.enums;

/**
 * 操作类型枚举
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public enum OperationTypeEnum {

    /**
     * 创建
     */
    CREATE("CREATE", "创建"),

    /**
     * 更新
     */
    UPDATE("UPDATE", "更新"),

    /**
     * 删除
     */
    DELETE("DELETE", "删除"),

    /**
     * 查询
     */
    QUERY("QUERY", "查询"),

    /**
     * 审批
     */
    APPROVE("APPROVE", "审批"),

    /**
     * 登录
     */
    LOGIN("LOGIN", "登录"),

    /**
     * 登出
     */
    LOGOUT("LOGOUT", "登出"),

    /**
     * 导出
     */
    EXPORT("EXPORT", "导出"),

    /**
     * 导入
     */
    IMPORT("IMPORT", "导入");

    private final String code;
    private final String description;

    OperationTypeEnum(String code, String description) {
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
