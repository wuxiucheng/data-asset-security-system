package com.dataasset.security.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 审计日志统计DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
public class AuditLogStatisticsDTO {

    /**
     * 总操作次数
     */
    private Long totalOperations;

    /**
     * 成功操作次数
     */
    private Long successCount;

    /**
     * 失败操作次数
     */
    private Long failureCount;

    /**
     * 成功率
     */
    private Double successRate;

    /**
     * 按操作类型统计
     */
    private Map<String, Long> operationTypeStats;

    /**
     * 按模块统计
     */
    private Map<String, Long> moduleStats;

    /**
     * 按用户统计
     */
    private Map<String, Long> userStats;

    /**
     * 按日期统计
     */
    private Map<String, Long> dateStats;

    /**
     * 统计开始时间
     */
    private LocalDateTime startTime;

    /**
     * 统计结束时间
     */
    private LocalDateTime endTime;
}