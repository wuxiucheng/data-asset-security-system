package com.dataasset.security.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.dto.AuditLogQueryDTO;
import com.dataasset.security.dto.AuditLogStatisticsDTO;
import com.dataasset.security.entity.AuditLog;

/**
 * 审计日志查询服务接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public interface AuditLogQueryService {

    /**
     * 分页查询审计日志
     *
     * @param queryDTO 查询条件
     * @return 审计日志分页结果
     */
    Page<AuditLog> queryAuditLogs(AuditLogQueryDTO queryDTO);

    /**
     * 统计审计日志
     *
     * @param queryDTO 查询条件
     * @return 统计结果
     */
    AuditLogStatisticsDTO statisticsAuditLogs(AuditLogQueryDTO queryDTO);

    /**
     * 导出审计日志
     *
     * @param queryDTO 查询条件
     * @return 导出文件路径
     */
    String exportAuditLogs(AuditLogQueryDTO queryDTO);

    /**
     * 归档审计日志
     *
     * @param beforeTime 归档时间点之前的日志
     * @return 归档的日志数量
     */
    Long archiveAuditLogs(java.time.LocalDateTime beforeTime);

    /**
     * 清理已归档的审计日志
     *
     * @param beforeTime 清理时间点之前的已归档日志
     * @return 清理的日志数量
     */
    Long cleanArchivedAuditLogs(java.time.LocalDateTime beforeTime);
}