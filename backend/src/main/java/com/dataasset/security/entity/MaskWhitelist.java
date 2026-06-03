package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;

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
    @TableId
    private Long whitelistId;

    /**
     * 脱敏策略ID
     */
    private Long strategyId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 白名单类型：USER/ROLE
     */
    private String whitelistType;

    /**
     * 生效开始时间
     */
    private LocalDateTime effectiveStart;

    /**
     * 生效结束时间
     */
    private LocalDateTime effectiveEnd;

    /**
     * 状态：ACTIVE/INACTIVE
     */
    private String status;

    /**
     * 备注
     */
    private String remark;
}
