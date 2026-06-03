package com.dataasset.security.service;

import com.dataasset.security.entity.AuthSession;

import java.util.List;

/**
 * 会话管理服务接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public interface SessionService {

    /**
     * 创建会话
     *
     * @param userId       用户ID
     * @param username     用户名
     * @param token        访问令牌
     * @param refreshToken 刷新令牌
     * @param request      HTTP请求
     * @return 会话信息
     */
    AuthSession createSession(Long userId, String username, String token, String refreshToken,
                              jakarta.servlet.http.HttpServletRequest request);

    /**
     * 根据Token获取会话
     *
     * @param token Token
     * @return 会话信息
     */
    AuthSession getSessionByToken(String token);

    /**
     * 验证会话
     *
     * @param token Token
     * @return 是否有效
     */
    boolean validateSession(String token);

    /**
     * 失效会话
     *
     * @param sessionId 会话ID
     */
    void invalidateSession(Long sessionId);

    /**
     * 失效用户所有会话
     *
     * @param userId 用户ID
     */
    void invalidateAllUserSessions(Long userId);

    /**
     * 获取用户活跃会话列表
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    List<AuthSession> getUserActiveSessions(Long userId);

    /**
     * 强制下线指定会话
     *
     * @param sessionId 会话ID
     */
    void forceLogoutSession(Long sessionId);

    /**
     * 清理过期会话
     */
    void cleanExpiredSessions();

    /**
     * 更新会话最后访问时间
     *
     * @param token Token
     */
    void updateLastAccessTime(String token);
}