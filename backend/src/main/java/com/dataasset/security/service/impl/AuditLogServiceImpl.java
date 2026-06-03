package com.dataasset.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.dto.AuditLogQueryDTO;
import com.dataasset.security.entity.AuditLog;
import com.dataasset.security.mapper.AuditLogMapper;
import com.dataasset.security.service.AuditLogService;
import com.dataasset.security.vo.AuditLogVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 审计日志 Service 实现类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl extends ServiceImpl<AuditLogMapper, AuditLog> implements AuditLogService {

    /**
     * 保存审计日志（异步）
     */
    @Async
    @Override
    public void saveLog(AuditLog auditLog) {
        try {
            this.save(auditLog);
            log.debug("审计日志保存成功：{}", auditLog.getOperationContent());
        } catch (Exception e) {
            log.error("保存审计日志失败：{}", e.getMessage());
        }
    }

    /**
     * 分页查询审计日志
     */
    @Override
    public Page<AuditLogVO> queryAuditLogs(AuditLogQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<AuditLog> wrapper = new LambdaQueryWrapper<>();

        // 操作类型
        if (StringUtils.hasText(queryDTO.getOperationType())) {
            wrapper.eq(AuditLog::getOperationType, queryDTO.getOperationType());
        }

        // 操作人ID
        if (queryDTO.getOperatorId() != null) {
            wrapper.eq(AuditLog::getOperatorId, queryDTO.getOperatorId());
        }

        // 操作对象类型
        if (StringUtils.hasText(queryDTO.getObjectType())) {
            wrapper.eq(AuditLog::getObjectType, queryDTO.getObjectType());
        }

        // 操作对象ID
        if (queryDTO.getObjectId() != null) {
            wrapper.eq(AuditLog::getObjectId, queryDTO.getObjectId());
        }

        // 操作结果
        if (StringUtils.hasText(queryDTO.getOperationResult())) {
            wrapper.eq(AuditLog::getOperationResult, queryDTO.getOperationResult());
        }

        // 时间范围
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(AuditLog::getOperationTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(AuditLog::getOperationTime, queryDTO.getEndTime());
        }

        // 按操作时间倒序排列
        wrapper.orderByDesc(AuditLog::getOperationTime);

        // 分页查询
        Page<AuditLog> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<AuditLog> auditLogPage = this.page(page, wrapper);

        // 转换为VO
        Page<AuditLogVO> voPage = new Page<>(auditLogPage.getCurrent(), auditLogPage.getSize(), auditLogPage.getTotal());
        voPage.setRecords(auditLogPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());

        return voPage;
    }

    /**
     * 转换为VO
     */
    private AuditLogVO convertToVO(AuditLog auditLog) {
        AuditLogVO vo = new AuditLogVO();
        vo.setLogId(auditLog.getLogId());
        vo.setOperationType(auditLog.getOperationType());
        vo.setOperatorId(auditLog.getOperatorId());
        vo.setOperatorName(auditLog.getOperatorName());
        vo.setOperationTime(auditLog.getOperationTime());
        vo.setObjectType(auditLog.getObjectType());
        vo.setObjectId(auditLog.getObjectId());
        vo.setObjectName(auditLog.getObjectName());
        vo.setOperationContent(auditLog.getOperationContent());
        vo.setOperationResult(auditLog.getOperationResult());
        vo.setIpAddress(auditLog.getIpAddress());
        vo.setUserAgent(auditLog.getUserAgent());
        vo.setRequestMethod(auditLog.getRequestMethod());
        vo.setRequestUrl(auditLog.getRequestUrl());
        return vo;
    }
}
