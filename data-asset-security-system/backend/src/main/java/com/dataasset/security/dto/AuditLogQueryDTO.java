package com.dataasset.security.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审计日志查询DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
public class AuditLogQueryDTO {

    /**
     * 操作类型：LOGIN, LOGOUT, CREATE, UPDATE, DELETE, QUERY, APPROVE
     */
    private String operationType;

    /**
     * 模块名称：USER, ROLE, PERMISSION, ASSET, CLASSIFICATION, GRADING
     */
    private String moduleName;

    /**
     * 对象类型
     */
    private String objectType;

    /**
     * 对象ID
     */
    private Long objectId;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人用户名
     */
    private String operatorUsername;

    /**
     * 操作结果：SUCCESS, FAILURE
     */
    private String operationResult;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 关键词搜索
     */
    private String keyword;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 20;

    /**
     * 当前页（别名）
     */
    public Integer getCurrent() {
        return pageNum;
    }

    /**
     * 每页大小（别名）
     */
    public Integer getSize() {
        return pageSize;
    }

    // 兼容前端page参数
    public void setPage(Integer page) {
        this.pageNum = page;
    }

    // 兼容前端size参数
    public void setSize(Integer size) {
        this.pageSize = size;
    }
}
