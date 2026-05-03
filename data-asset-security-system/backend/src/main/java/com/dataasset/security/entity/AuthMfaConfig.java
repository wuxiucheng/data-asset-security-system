package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 多因素认证配置实体类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@TableName("auth_mfa_config")
public class AuthMfaConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    @TableId(value = "config_id", type = IdType.AUTO)
    private Long configId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * MFA类型：TOTP, SMS, EMAIL
     */
    private String mfaType;

    /**
     * 密钥
     */
    private String secretKey;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 二维码URL
     */
    private String qrCodeUrl;

    /**
     * 备用码
     */
    private String backupCodes;

    /**
     * 状态：ACTIVE, INACTIVE
     */
    private String status;

    /**
     * 启用时间
     */
    private LocalDateTime enabledTime;

    /**
     * 最后验证时间
     */
    private LocalDateTime lastVerifiedTime;

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