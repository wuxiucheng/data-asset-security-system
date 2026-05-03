package com.dataasset.security.dto;

import lombok.Data;

/**
 * MFA设置DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
public class MfaSetupDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * MFA类型：TOTP, SMS, EMAIL
     */
    private String mfaType;

    /**
     * 密钥
     */
    private String secret;

    /**
     * 二维码URL
     */
    private String qrCodeUrl;

    /**
     * 二维码图片（Base64）
     */
    private String qrCodeImage;

    /**
     * 是否已启用
     */
    private Boolean enabled;

    /**
     * 备用码
     */
    private String[] backupCodes;
}