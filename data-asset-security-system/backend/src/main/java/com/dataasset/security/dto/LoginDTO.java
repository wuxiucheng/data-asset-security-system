package com.dataasset.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
public class LoginDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 验证码
     */
    private String captcha;

    /**
     * 验证码Key
     */
    private String captchaKey;

    /**
     * MFA验证码（如果启用了多因素认证）
     */
    private String mfaCode;

    /**
     * 记住我
     */
    private Boolean rememberMe = false;
}