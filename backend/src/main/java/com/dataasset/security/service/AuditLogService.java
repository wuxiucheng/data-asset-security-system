package com.dataasset.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.dto.AuditLogQueryDTO;
import com.dataasset.security.entity.AuditLog;
import com.dataasset.security.vo.AuditLogVO;

/**
 * 审计日志 Service 接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public interface AuditLogService extends IService<AuditLog> {

    /**
     * 保存审计日志（异步）
     *
     * @param auditLog 审计日志
     */
    void saveLog(AuditLog auditLog);

    /**
     * 分页查询审计日志
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<AuditLogVO> queryAuditLogs(AuditLogQueryDTO queryDTO);
}
