package com.dataasset.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dataasset.security.entity.QualityRule;
import com.dataasset.security.entity.QualityProbeTask;
import com.dataasset.security.entity.QualityProbeResult;

import java.util.List;

/**
 * 数据质量探查Service接口
 */
public interface QualityProbeService extends IService<QualityProbeTask> {

    /**
     * 创建探查任务
     */
    QualityProbeTask createTask(QualityProbeTask task);

    /**
     * 执行探查任务
     */
    void executeTask(Long taskId);

    /**
     * 获取任务结果
     */
    List<QualityProbeResult> getTaskResults(Long taskId);

    /**
     * 获取资产质量报告
     */
    List<QualityProbeResult> getAssetQualityReport(Long assetId);

    /**
     * 初始化默认质量规则
     */
    void initDefaultRules();
}
