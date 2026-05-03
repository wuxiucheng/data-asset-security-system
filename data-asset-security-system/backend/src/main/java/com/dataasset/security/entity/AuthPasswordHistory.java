package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 密码历史实体类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@TableName("auth_password_history")
public class AuthPasswordHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 历史ID
     */
    @TableId(value = "history_id", type = IdType.AUTO)
    private Long historyId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 密码（加密）
     */
    private String password;

    /**
     * 修改时间
     */
    private LocalDateTime changeTime;

    /**
     * 修改类型：INIT, RESET, MODIFY, ADMIN_RESET
     */
    private String changeType;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}