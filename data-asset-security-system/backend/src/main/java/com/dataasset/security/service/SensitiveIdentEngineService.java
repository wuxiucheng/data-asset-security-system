package com.dataasset.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dataasset.security.entity.SensitiveIdentResult;

import java.util.List;

/**
 * 敏感数据识别引擎Service接口
 */
public interface SensitiveIdentEngineService extends IService<SensitiveIdentResult> {

    /**
     * 扫描资产的所有字段
     */
    void scanAsset(Long assetId);

    /**
     * 扫描指定字段
     */
    SensitiveIdentResult scanField(Long assetId, Long fieldId);

    /**
     * 批量扫描资产
     */
    void batchScanAssets(List<Long> assetIds);

    /**
     * 确认识别结果
     */
    void confirmResult(Long resultId, String confirmStatus, String confirmRemark);

    /**
     * 获取资产的识别结果
     */
    List<SensitiveIdentResult> getResultsByAsset(Long assetId);

    /**
     * 获取字段的识别结果
     */
    List<SensitiveIdentResult> getResultsByField(Long fieldId);
}
