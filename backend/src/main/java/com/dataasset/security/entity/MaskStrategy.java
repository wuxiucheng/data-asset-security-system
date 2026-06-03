package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.TableId;
/**
 * 脱敏策略实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mask_strategy")
public class MaskStrategy extends BaseEntity {

    /**
     * 策略ID
     */
    @TableId
    private Long strategyId;

    /**
     * 敏感类型
     */
    private String sensitiveType;

    /**
     * 脱敏算法：MASK/REPLACE/HASH/ENCRYPT/TRUNCATE/SHUFFLE
     */
    private String algorithm;

    /**
     * 算法参数(JSON)
     */
    private String algorithmParams;

    /**
     * 分级覆盖配置(JSON)
     */
    private String gradingOverride;

    /**
     * 策略描述
     */
    private String description;

    /**
     * 状态：ENABLED/DISABLED
     */
    private String status;
}
