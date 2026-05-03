package com.dataasset.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * MFA验证DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
public class MfaVerifyDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 验证码（6位数字）
     */
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "验证码必须是6位数字")
    private String code;

    /**
     * 是否使用备用码
     */
    private Boolean useBackupCode = false;
}