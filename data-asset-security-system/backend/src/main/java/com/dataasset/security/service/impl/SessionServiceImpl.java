package com.dataasset.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dataasset.security.entity.AuthSession;
import com.dataasset.security.mapper.AuthSessionMapper;
import com.dataasset.security.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 会话管理服务实现类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final AuthSessionMapper authSessionMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SESSION_CACHE_PREFIX = "session:";
    private static final long SESSION_CACHE_EXPIRE_SECONDS = 7200; // 2小时

    @Override
    @Transactional
    public AuthSession createSession(Long userId, String username, String token, String refreshToken,
                                      jakarta.servlet.http.HttpServletRequest request) {
        AuthSession session = new AuthSession();
        session.setUserId(userId);
        session.setUsername(username);
        session.setToken(token);
        session.setRefreshToken(refreshToken);
        session.setLoginTime(LocalDateTime.now());
        session.setLastAccessTime(LocalDateTime.now());
        session.setExpireTime(LocalDateTime.now().plusSeconds(86400)); // 24小时后过期
        session.setLoginIp(getClientIp(request));
        session.setDeviceInfo(getDeviceInfo(request));
        session.setBrowserInfo(getBrowserInfo(request));
        session.setStatus("ACTIVE");

        authSessionMapper.insert(session);

        // 缓存会话信息
        cacheSession(session);

        log.info("创建会话成功，用户ID：{}，会话ID：{}", userId, session.getSessionId());
        return session;
    }

    @Override
    public AuthSession getSessionByToken(String token) {
        // 先从缓存中获取
        String cacheKey = SESSION_CACHE_PREFIX + token;
        AuthSession cachedSession = (AuthSession) redisTemplate.opsForValue().get(cacheKey);
        if (cachedSession != null) {
            return cachedSession;
        }

        // 从数据库中获取
        AuthSession session = authSessionMapper.selectOne(
                new LambdaQueryWrapper<AuthSession>()
                        .eq(AuthSession::getToken, token)
                        .eq(AuthSession::getStatus, "ACTIVE")
        );

        if (session != null) {
            // 缓存会话信息
            cacheSession(session);
        }

        return session;
    }

    @Override
    public boolean validateSession(String token) {
        AuthSession session = getSessionByToken(token);
        if (session == null) {
            return false;
        }

        if (!"ACTIVE".equals(session.getStatus())) {
            return false;
        }

        // 检查是否过期
        if (session.getExpireTime().isBefore(LocalDateTime.now())) {
            invalidateSession(session.getSessionId());
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public void invalidateSession(Long sessionId) {
        AuthSession session = authSessionMapper.selectById(sessionId);
        if (session != null) {
            session.setStatus("INVALID");
            authSessionMapper.updateById(session);

            // 清除缓存
            String cacheKey = SESSION_CACHE_PREFIX + session.getToken();
            redisTemplate.delete(cacheKey);

            log.info("失效会话，会话ID：{}", sessionId);
        }
    }

    @Override
    @Transactional
    public void invalidateAllUserSessions(Long userId) {
        List<AuthSession> sessions = authSessionMapper.selectList(
                new LambdaQueryWrapper<AuthSession>()
                        .eq(AuthSession::getUserId, userId)
                        .eq(AuthSession::getStatus, "ACTIVE")
        );

        for (AuthSession session : sessions) {
            invalidateSession(session.getSessionId());
        }

        log.info("失效用户所有会话，用户ID：{}，会话数：{}", userId, sessions.size());
    }

    @Override
    public List<AuthSession> getUserActiveSessions(Long userId) {
        return authSessionMapper.selectList(
                new LambdaQueryWrapper<AuthSession>()
                        .eq(AuthSession::getUserId, userId)
                        .eq(AuthSession::getStatus, "ACTIVE")
                        .orderByDesc(AuthSession::getLoginTime)
        );
    }

    @Override
    @Transactional
    public void forceLogoutSession(Long sessionId) {
        invalidateSession(sessionId);
        log.info("强制下线会话，会话ID：{}", sessionId);
    }

    @Override
    @Transactional
    public void cleanExpiredSessions() {
        List<AuthSession> expiredSessions = authSessionMapper.selectList(
                new LambdaQueryWrapper<AuthSession>()
                        .eq(AuthSession::getStatus, "ACTIVE")
                        .lt(AuthSession::getExpireTime, LocalDateTime.now())
        );

        for (AuthSession session : expiredSessions) {
            invalidateSession(session.getSessionId());
        }

        log.info("清理过期会话，清理数量：{}", expiredSessions.size());
    }

    @Override
    @Transactional
    public void updateLastAccessTime(String token) {
        AuthSession session = getSessionByToken(token);
        if (session != null) {
            session.setLastAccessTime(LocalDateTime.now());
            authSessionMapper.updateById(session);

            // 更新缓存
            cacheSession(session);
        }
    }

    /**
     * 缓存会话信息
     */
    private void cacheSession(AuthSession session) {
        String cacheKey = SESSION_CACHE_PREFIX + session.getToken();
        redisTemplate.opsForValue().set(cacheKey, session, SESSION_CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(jakarta.servlet.http.HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        return ipAddress;
    }

    /**
     * 获取设备信息
     */
    private String getDeviceInfo(jakarta.servlet.http.HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        // 简化处理，返回User-Agent
        return userAgent != null ? userAgent.substring(0, Math.min(255, userAgent.length())) : "Unknown";
    }

    /**
     * 获取浏览器信息
     */
    private String getBrowserInfo(jakarta.servlet.http.HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        // 简化处理，返回User-Agent
        return userAgent != null ? userAgent.substring(0, Math.min(255, userAgent.length())) : "Unknown";
    }
}
