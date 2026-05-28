package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 脱敏白名单实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mask_whitelist")
public class MaskWhitelist extends BaseEntity {

    /**
     * 白名单ID
     */
    private Long whitelistId;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 字段ID
     */
    private Long fieldId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 白名单类型：USER/ROLE/FIELD/ASSET
     */
    private String whitelistType;

    /**
     * 原因说明
     */
    private String reason;

    /**
     * 生效开始时间
     */
    private java.time.LocalDateTime effectiveStartTime;

    /**
     * 生效结束时间
     */
    private java.time.LocalDateTime effectiveEndTime;

    /**
     * 状态：ACTIVE/INACTIVE
     */
    private String status;
}
