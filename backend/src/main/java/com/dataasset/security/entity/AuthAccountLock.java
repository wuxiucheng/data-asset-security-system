package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 账户锁定记录实体类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@TableName("auth_account_lock")
public class AuthAccountLock implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 锁定ID
     */
    @TableId(value = "lock_id", type = IdType.AUTO)
    private Long lockId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 锁定类型：PASSWORD_FAILURE, ADMIN_LOCK, SECURITY_RISK
     */
    private String lockType;

    /**
     * 锁定原因
     */
    private String lockReason;

    /**
     * 锁定时间
     */
    private LocalDateTime lockTime;

    /**
     * 解锁时间
     */
    private LocalDateTime unlockTime;

    /**
     * 锁定时长（分钟）
     */
    private Integer lockDuration;

    /**
     * 状态：LOCKED, UNLOCKED
     */
    private String status;

    /**
     * 解锁操作人ID
     */
    private Long unlockOperatorId;

    /**
     * 解锁操作人姓名
     */
    private String unlockOperatorName;

    /**
     * 解锁原因
     */
    private String unlockReason;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}