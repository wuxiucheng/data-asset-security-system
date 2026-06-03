package com.dataasset.security.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.entity.QualityAlert;
import com.dataasset.security.mapper.QualityAlertMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 质量告警Service
 */
@Slf4j
@Service
public class QualityAlertService extends ServiceImpl<QualityAlertMapper, QualityAlert> {

    /**
     * 分页查询告警
     */
    public Page<QualityAlert> queryPage(Page<QualityAlert> page, Long taskId, String alertLevel, String alertStatus) {
        LambdaQueryWrapper<QualityAlert> wrapper = new LambdaQueryWrapper<>();
        if (taskId != null) {
            wrapper.eq(QualityAlert::getTaskId, taskId);
        }
        if (alertLevel != null && !alertLevel.isEmpty()) {
            wrapper.eq(QualityAlert::getAlertLevel, alertLevel);
        }
        if (alertStatus != null && !alertStatus.isEmpty()) {
            wrapper.eq(QualityAlert::getAlertStatus, alertStatus);
        }
        wrapper.orderByDesc(QualityAlert::getCreatedTime);
        return this.page(page, wrapper);
    }

    /**
     * 根据任务ID获取告警列表
     */
    public List<QualityAlert> getByTaskId(Long taskId) {
        LambdaQueryWrapper<QualityAlert> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QualityAlert::getTaskId, taskId)
               .orderByDesc(QualityAlert::getAlertLevel)
               .orderByDesc(QualityAlert::getCreatedTime);
        return this.list(wrapper);
    }

    /**
     * 获取待处理告警数量
     */
    public long getPendingCount() {
        LambdaQueryWrapper<QualityAlert> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QualityAlert::getAlertStatus, "PENDING");
        return this.count(wrapper);
    }

    /**
     * 获取我的待处理告警
     */
    public List<QualityAlert> getMyPendingAlerts(Long userId) {
        LambdaQueryWrapper<QualityAlert> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QualityAlert::getAlertStatus, "PENDING")
               .and(w -> w.isNull(QualityAlert::getAssigneeId)
                         .or()
                         .eq(QualityAlert::getAssigneeId, userId))
               .orderByDesc(QualityAlert::getAlertLevel)
               .orderByDesc(QualityAlert::getCreatedTime);
        return this.list(wrapper);
    }

    /**
     * 创建告警
     */
    @Transactional(rollbackFor = Exception.class)
    public QualityAlert create(QualityAlert alert) {
        // 设置默认状态
        if (alert.getAlertStatus() == null) {
            alert.setAlertStatus("PENDING");
        }
        
        // 验证告警级别
        if (!isValidAlertLevel(alert.getAlertLevel())) {
            throw new IllegalArgumentException("告警级别必须是INFO、WARNING、ERROR或CRITICAL");
        }

        this.save(alert);
        return alert;
    }

    /**
     * 分配告警处理人
     */
    @Transactional(rollbackFor = Exception.class)
    public void assign(Long alertId, Long assigneeId) {
        QualityAlert alert = this.getById(alertId);
        if (alert == null) {
            throw new IllegalArgumentException("告警不存在");
        }
        
        if (!"PENDING".equals(alert.getAlertStatus())) {
            throw new IllegalArgumentException("只有待处理的告警才能分配");
        }

        alert.setAssigneeId(assigneeId);
        alert.setAlertStatus("PROCESSING");
        this.updateById(alert);
    }

    /**
     * 解决告警
     */
    @Transactional(rollbackFor = Exception.class)
    public void resolve(Long alertId, String resolvedRemark) {
        QualityAlert alert = this.getById(alertId);
        if (alert == null) {
            throw new IllegalArgumentException("告警不存在");
        }

        alert.setAlertStatus("RESOLVED");
        alert.setResolvedTime(LocalDateTime.now());
        alert.setResolvedRemark(resolvedRemark);
        this.updateById(alert);
    }

    /**
     * 忽略告警
     */
    @Transactional(rollbackFor = Exception.class)
    public void ignore(Long alertId, String resolvedRemark) {
        QualityAlert alert = this.getById(alertId);
        if (alert == null) {
            throw new IllegalArgumentException("告警不存在");
        }

        alert.setAlertStatus("IGNORED");
        alert.setResolvedTime(LocalDateTime.now());
        alert.setResolvedRemark(resolvedRemark);
        this.updateById(alert);
    }

    /**
     * 批量解决告警
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchResolve(List<Long> alertIds, String resolvedRemark) {
        for (Long alertId : alertIds) {
            try {
                resolve(alertId, resolvedRemark);
            } catch (Exception e) {
                log.error("解决告警失败: {}", alertId, e);
            }
        }
    }

    /**
     * 验证告警级别
     */
    private boolean isValidAlertLevel(String level) {
        return "INFO".equals(level) || "WARNING".equals(level) || 
               "ERROR".equals(level) || "CRITICAL".equals(level);
    }
}
