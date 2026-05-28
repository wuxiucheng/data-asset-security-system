package com.dataasset.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dataasset.security.entity.MaskStrategy;

import java.util.List;

/**
 * 脱敏策略Service接口
 */
public interface MaskStrategyService extends IService<MaskStrategy> {

    /**
     * 根据敏感类型获取策略
     */
    MaskStrategy getBySensitiveType(String sensitiveType);

    /**
     * 初始化默认策略
     */
    void initDefaultStrategies();

    /**
     * 应用脱敏
     */
    String applyMask(String value, String sensitiveType, String algorithm, String params);
}
