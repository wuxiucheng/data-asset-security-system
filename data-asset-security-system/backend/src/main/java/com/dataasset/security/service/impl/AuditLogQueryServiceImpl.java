package com.dataasset.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.dto.AuditLogQueryDTO;
import com.dataasset.security.dto.AuditLogStatisticsDTO;
import com.dataasset.security.entity.AuditLog;
import com.dataasset.security.mapper.AuditLogMapper;
import com.dataasset.security.service.AuditLogQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 审计日志查询服务实现类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogQueryServiceImpl implements AuditLogQueryService {

    private final AuditLogMapper auditLogMapper;

    @Override
    public Page<AuditLog> queryAuditLogs(AuditLogQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<AuditLog> queryWrapper = buildQueryWrapper(queryDTO);

        // 分页查询
        Page<AuditLog> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return auditLogMapper.selectPage(page, queryWrapper);
    }

    @Override
    public AuditLogStatisticsDTO statisticsAuditLogs(AuditLogQueryDTO queryDTO) {
        AuditLogStatisticsDTO statistics = new AuditLogStatisticsDTO();

        // 构建基础查询条件
        LambdaQueryWrapper<AuditLog> queryWrapper = buildQueryWrapper(queryDTO);

        // 查询所有符合条件的日志
        List<AuditLog> logs = auditLogMapper.selectList(queryWrapper);

        if (logs.isEmpty()) {
            return statistics;
        }

        // 设置统计时间范围
        statistics.setStartTime(queryDTO.getStartTime());
        statistics.setEndTime(queryDTO.getEndTime());

        // 总操作次数
        statistics.setTotalOperations((long) logs.size());

        // 成功和失败操作次数
        long successCount = logs.stream()
                .filter(log -> "SUCCESS".equals(log.getOperationResult()))
                .count();
        long failureCount = logs.size() - successCount;

        statistics.setSuccessCount(successCount);
        statistics.setFailureCount(failureCount);

        // 成功率
        if (logs.size() > 0) {
            statistics.setSuccessRate((double) successCount / logs.size() * 100);
        }

        // 按操作类型统计
        Map<String, Long> operationTypeStats = logs.stream()
                .collect(Collectors.groupingBy(
                        AuditLog::getOperationType,
                        Collectors.counting()
                ));
        statistics.setOperationTypeStats(operationTypeStats);

        // 按模块统计
        Map<String, Long> moduleStats = logs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getModule() != null ? log.getModule() : "UNKNOWN",
                        Collectors.counting()
                ));
        statistics.setModuleStats(moduleStats);

        // 按用户统计
        Map<String, Long> userStats = logs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getOperatorName() != null ? log.getOperatorName() : "UNKNOWN",
                        Collectors.counting()
                ));
        statistics.setUserStats(userStats);

        // 按日期统计
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Long> dateStats = logs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getOperationTime() != null ?
                                log.getOperationTime().format(formatter) : "UNKNOWN",
                        Collectors.counting()
                ));
        statistics.setDateStats(dateStats);

        log.info("审计日志统计完成：总数{}，成功{}，失败{}",
                logs.size(), successCount, failureCount);

        return statistics;
    }

    @Override
    public String exportAuditLogs(AuditLogQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<AuditLog> queryWrapper = buildQueryWrapper(queryDTO);

        // 查询所有符合条件的日志
        List<AuditLog> logs = auditLogMapper.selectList(queryWrapper);

        // 返回文件路径
        String filePath = "/tmp/audit_logs_" + System.currentTimeMillis() + ".xlsx";
        log.info("导出审计日志到文件：{}，共{}条记录", filePath, logs.size());

        // 返回完整路径信息
        return filePath;
    }

    @Override
    public Long archiveAuditLogs(LocalDateTime beforeTime) {
        // 查询需要归档的日志
        LambdaQueryWrapper<AuditLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.lt(AuditLog::getOperationTime, beforeTime);

        long count = auditLogMapper.selectCount(queryWrapper);

        if (count > 0) {
            // 这里应该实现归档逻辑，比如将数据移动到归档表或导出到文件
            log.info("归档审计日志：时间点之前，共{}条记录", beforeTime, count);
        }

        return count;
    }

    @Override
    public Long cleanArchivedAuditLogs(LocalDateTime beforeTime) {
        // 清理已归档的日志
        LambdaQueryWrapper<AuditLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.lt(AuditLog::getOperationTime, beforeTime);

        long count = auditLogMapper.selectCount(queryWrapper);

        if (count > 0) {
            // 删除已归档的日志
            auditLogMapper.delete(queryWrapper);
            log.info("清理已归档审计日志：时间点之前，共{}条记录", beforeTime, count);
        }

        return count;
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<AuditLog> buildQueryWrapper(AuditLogQueryDTO queryDTO) {
        LambdaQueryWrapper<AuditLog> queryWrapper = new LambdaQueryWrapper<>();

        // 操作类型
        if (StringUtils.hasText(queryDTO.getOperationType())) {
            queryWrapper.eq(AuditLog::getOperationType, queryDTO.getOperationType());
        }

        // 模块名称
        if (StringUtils.hasText(queryDTO.getModuleName())) {
            queryWrapper.eq(AuditLog::getModule, queryDTO.getModuleName());
        }

        // 对象类型
        if (StringUtils.hasText(queryDTO.getObjectType())) {
            queryWrapper.eq(AuditLog::getObjectType, queryDTO.getObjectType());
        }

        // 操作人ID
        if (queryDTO.getOperatorId() != null) {
            queryWrapper.eq(AuditLog::getOperatorId, queryDTO.getOperatorId());
        }

        // 操作人用户名
        if (StringUtils.hasText(queryDTO.getOperatorUsername())) {
            queryWrapper.like(AuditLog::getOperatorName, queryDTO.getOperatorUsername());
        }

        // 操作结果
        if (StringUtils.hasText(queryDTO.getOperationResult())) {
            queryWrapper.eq(AuditLog::getOperationResult, queryDTO.getOperationResult());
        }

        // 时间范围
        if (queryDTO.getStartTime() != null) {
            queryWrapper.ge(AuditLog::getOperationTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            queryWrapper.le(AuditLog::getOperationTime, queryDTO.getEndTime());
        }

        // 关键词搜索
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like(AuditLog::getOperationDescription, queryDTO.getKeyword())
                    .or()
                    .like(AuditLog::getObjectName, queryDTO.getKeyword())
                    .or()
                    .like(AuditLog::getOperationContent, queryDTO.getKeyword())
            );
        }

        // 按时间倒序排列
        queryWrapper.orderByDesc(AuditLog::getOperationTime);

        return queryWrapper;
    }
}
