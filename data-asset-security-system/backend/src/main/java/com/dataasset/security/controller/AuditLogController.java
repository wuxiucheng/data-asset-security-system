package com.dataasset.security.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.common.enums.ObjectTypeEnum;
import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.dto.AuditLogQueryDTO;
import com.dataasset.security.dto.AuditLogStatisticsDTO;
import com.dataasset.security.entity.AuditLog;
import com.dataasset.security.service.AuditLogQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 审计日志查询控制器
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@RestController
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
@Tag(name = "审计日志查询", description = "审计日志查询和统计相关接口")
public class AuditLogController {

    private final AuditLogQueryService auditLogQueryService;

    /**
     * 分页查询审计日志
     */
    @PostMapping("/query")
    @Operation(summary = "分页查询审计日志", description = "根据条件分页查询审计日志")
    @com.dataasset.security.common.annotation.AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.AUDIT_LOG, description = "查询审计日志")
    public Result<Page<AuditLog>> queryAuditLogs(@RequestBody AuditLogQueryDTO queryDTO) {
        log.info("分页查询审计日志请求：{}", queryDTO);
        try {
            Page<AuditLog> page = auditLogQueryService.queryAuditLogs(queryDTO);
            return Result.success(page);
        } catch (Exception e) {
            log.error("分页查询审计日志失败：", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 统计审计日志
     */
    @PostMapping("/statistics")
    @Operation(summary = "统计审计日志", description = "根据条件统计审计日志")
    public Result<AuditLogStatisticsDTO> statisticsAuditLogs(@RequestBody AuditLogQueryDTO queryDTO) {
        log.info("统计审计日志请求：{}", queryDTO);
        try {
            AuditLogStatisticsDTO statistics = auditLogQueryService.statisticsAuditLogs(queryDTO);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("统计审计日志失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 导出审计日志
     */
    @PostMapping("/export")
    @Operation(summary = "导出审计日志", description = "导出审计日志到Excel文件")
    @com.dataasset.security.common.annotation.AuditLog(operationType = OperationTypeEnum.EXPORT, objectType = ObjectTypeEnum.AUDIT_LOG, description = "导出审计日志")
    public Result<String> exportAuditLogs(@RequestBody AuditLogQueryDTO queryDTO) {
        log.info("导出审计日志请求：{}", queryDTO);
        try {
            String filePath = auditLogQueryService.exportAuditLogs(queryDTO);
            return Result.success("导出成功", filePath);
        } catch (Exception e) {
            log.error("导出审计日志失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 归档审计日志
     */
    @PostMapping("/archive")
    @Operation(summary = "归档审计日志", description = "归档指定时间之前的审计日志")
    @com.dataasset.security.common.annotation.AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.AUDIT_LOG, description = "归档审计日志")
    public Result<Long> archiveAuditLogs(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime beforeTime) {
        log.info("归档审计日志请求，时间点：{}", beforeTime);
        try {
            Long count = auditLogQueryService.archiveAuditLogs(beforeTime);
            return Result.success("归档成功，共" + count + "条记录", count);
        } catch (Exception e) {
            log.error("归档审计日志失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 清理已归档的审计日志
     */
    @PostMapping("/clean")
    @Operation(summary = "清理已归档的审计日志", description = "清理指定时间之前的已归档审计日志")
    @com.dataasset.security.common.annotation.AuditLog(operationType = OperationTypeEnum.DELETE, objectType = ObjectTypeEnum.AUDIT_LOG, description = "清理已归档审计日志")
    public Result<Long> cleanArchivedAuditLogs(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime beforeTime) {
        log.info("清理已归档审计日志请求，时间点：{}", beforeTime);
        try {
            Long count = auditLogQueryService.cleanArchivedAuditLogs(beforeTime);
            return Result.success("清理成功，共" + count + "条记录", count);
        } catch (Exception e) {
            log.error("清理已归档审计日志失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
