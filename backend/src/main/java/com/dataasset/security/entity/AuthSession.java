package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 认证会话实体类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@TableName("auth_session")
public class AuthSession implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    @TableId(value = "session_id", type = IdType.AUTO)
    private Long sessionId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 访问令牌
     */
    private String token;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 最后访问时间
     */
    private LocalDateTime lastAccessTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * 浏览器信息
     */
    private String browserInfo;

    /**
     * 状态：ACTIVE, INVALID, EXPIRED
     */
    private String status;

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