package com.dataasset.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * MFA启用DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
public class MfaEnableDTO {

    /**
     * MFA类型：TOTP, SMS, EMAIL
     */
    @NotBlank(message = "MFA类型不能为空")
    @Pattern(regexp = "^(TOTP|SMS|EMAIL)$", message = "MFA类型必须是TOTP、SMS或EMAIL")
    private String mfaType;

    /**
     * 密钥
     */
    @NotBlank(message = "密钥不能为空")
    private String secret;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "验证码必须是6位数字")
    private String verificationCode;
}