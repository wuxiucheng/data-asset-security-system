package com.dataasset.security.dto;

import lombok.Data;

import java.util.List;

/**
 * 登录结果DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
public class LoginResultDTO {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 令牌类型
     */
    private String tokenType = "Bearer";

    /**
     * 过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 用户信息
     */
    private UserInfo userInfo;

    /**
     * 用户信息
     */
    @Data
    public static class UserInfo {
        /**
         * 用户ID
         */
        private Long userId;

        /**
         * 用户名
         */
        private String username;

        /**
         * 真实姓名
         */
        private String realName;

        /**
         * 邮箱
         */
        private String email;

        /**
         * 手机号
         */
        private String phone;

        /**
         * 头像
         */
        private String avatar;

        /**
         * 角色列表
         */
        private List<String> roles;

        /**
         * 权限列表
         */
        private List<String> permissions;

        /**
         * 是否启用了MFA
         */
        private Boolean mfaEnabled;
    }
}
