package com.dataasset.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dataasset.security.dto.LoginDTO;
import com.dataasset.security.dto.LoginResultDTO;
import com.dataasset.security.dto.TokenRefreshDTO;
import com.dataasset.security.entity.*;
import com.dataasset.security.mapper.*;
import com.dataasset.security.security.CustomUserDetails;
import com.dataasset.security.service.AuthService;
import com.dataasset.security.service.SysUserService;
import com.dataasset.security.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 认证服务实现类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final SysUserService sysUserService;
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final AuthSessionMapper authSessionMapper;
    private final AuthLoginLogMapper authLoginLogMapper;
    private final AuthPasswordHistoryMapper authPasswordHistoryMapper;
    private final AuthAccountLockMapper authAccountLockMapper;
    private final AuthMfaConfigMapper authMfaConfigMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    private static final String LOGIN_FAILURE_PREFIX = "login:failure:";
    private static final String CAPTCHA_PREFIX = "captcha:";
    private static final long TOKEN_EXPIRATION_SECONDS = 86400; // 24小时
    private static final long REFRESH_TOKEN_EXPIRATION_SECONDS = 604800; // 7天
    private static final int MAX_LOGIN_FAILURE_COUNT = 5; // 最大登录失败次数
    private static final long ACCOUNT_LOCK_DURATION_MINUTES = 30; // 账户锁定时长（分钟）

    @Override
    @Transactional
    public LoginResultDTO login(LoginDTO loginDTO, HttpServletRequest request) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        // 检查账户是否被锁定
        checkAccountLocked(username);

        // 验证验证码（如果启用）
        if (StringUtils.hasText(loginDTO.getCaptchaKey())) {
            validateCaptcha(loginDTO.getCaptchaKey(), loginDTO.getCaptcha());
        }

        // 查询用户信息
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
        );

        if (user == null) {
            recordLoginFailure(username, "用户不存在", request);
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查用户状态
        if (!"ACTIVE".equals(user.getStatus())) {
            if ("LOCKED".equals(user.getStatus())) {
                throw new RuntimeException("账户已被锁定，请联系管理员");
            } else {
                throw new RuntimeException("账户已被禁用，请联系管理员");
            }
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            handleLoginFailure(user, request);
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查是否启用了MFA
        boolean mfaEnabled = checkMfaEnabled(user.getUserId());

        // 如果启用了MFA但没有提供MFA验证码，则返回提示
        if (mfaEnabled && !StringUtils.hasText(loginDTO.getMfaCode())) {
            LoginResultDTO result = new LoginResultDTO();
            result.setTokenType("Bearer");
            result.setExpiresIn(0L);
            LoginResultDTO.UserInfo userInfo = new LoginResultDTO.UserInfo();
            userInfo.setUserId(user.getUserId());
            userInfo.setUsername(user.getUsername());
            userInfo.setRealName(user.getRealName());
            userInfo.setMfaEnabled(true);
            result.setUserInfo(userInfo);
            return result;
        }

        // 如果启用了MFA，验证MFA验证码
        if (mfaEnabled) {
            if (!verifyMfaCode(user.getUserId(), loginDTO.getMfaCode())) {
                handleLoginFailure(user, request);
                throw new RuntimeException("MFA验证码错误，请确认验证码是否正确且未过期（验证码每30秒刷新一次）");
            }
        }

        // 生成Token
        String accessToken = jwtUtils.generateToken(user.getUserId(), user.getUsername());
        String refreshToken = generateRefreshToken(user.getUserId(), user.getUsername());

        // 创建会话
        createSession(user, accessToken, refreshToken, request);

        // 记录登录成功日志
        recordLoginSuccess(user, request);

        // 清除登录失败计数
        clearLoginFailureCount(username);

        // 构建返回结果
        return buildLoginResult(user, accessToken, refreshToken);
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request) {
        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String token = getTokenFromRequest(request);

            if (StringUtils.hasText(token)) {
                // 撤销Token
                revokeToken(token);

                // 失效会话
                invalidateUserSessions(userDetails.getUserId());

                // 记录登出日志
                recordLogout(userDetails.getUserId(), userDetails.getUsername(), request);
            }
        }

        // 清除Security上下文
        SecurityContextHolder.clearContext();
    }

    @Override
    @Transactional
    public LoginResultDTO refreshToken(TokenRefreshDTO refreshDTO) {
        String refreshToken = refreshDTO.getRefreshToken();

        // 验证刷新令牌
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new RuntimeException("刷新令牌无效或已过期");
        }

        // 检查刷新令牌是否在黑名单中
        if (isTokenBlacklisted(refreshToken)) {
            throw new RuntimeException("刷新令牌已被撤销");
        }

        // 从令牌中获取用户信息
        Long userId = jwtUtils.getUserIdFromToken(refreshToken);
        String username = jwtUtils.getUsernameFromToken(refreshToken);

        if (userId == null || username == null) {
            throw new RuntimeException("刷新令牌无效");
        }

        // 查询用户信息
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || !"ACTIVE".equals(user.getStatus())) {
            throw new RuntimeException("用户不存在或已被禁用");
        }

        // 生成新的Token
        String newAccessToken = jwtUtils.generateToken(userId, username);
        String newRefreshToken = generateRefreshToken(userId, username);

        // 更新会话
        updateSessionToken(userId, refreshToken, newAccessToken, newRefreshToken);

        // 将旧的刷新令牌加入黑名单
        revokeToken(refreshToken);

        // 构建返回结果
        return buildLoginResult(user, newAccessToken, newRefreshToken);
    }

    @Override
    public LoginResultDTO.UserInfo getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            SysUser user = sysUserMapper.selectById(userDetails.getUserId());

            if (user != null) {
                LoginResultDTO.UserInfo userInfo = new LoginResultDTO.UserInfo();
                userInfo.setUserId(user.getUserId());
                userInfo.setUsername(user.getUsername());
                userInfo.setRealName(user.getRealName());
                userInfo.setEmail(user.getEmail());
                userInfo.setPhone(user.getPhone());

                // 获取用户角色
                List<String> roles = userDetails.getAuthorities().stream()
                        .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                        .collect(Collectors.toList());
                userInfo.setRoles(roles);

                // 获取用户权限
                List<String> permissions = userDetails.getPermissions();
                userInfo.setPermissions(permissions);

                // 检查是否启用了MFA
                userInfo.setMfaEnabled(checkMfaEnabled(user.getUserId()));

                return userInfo;
            }
        }
        return null;
    }

    @Override
    public boolean validateToken(String token) {
        // 检查Token是否在黑名单中
        if (isTokenBlacklisted(token)) {
            return false;
        }

        // 验证Token格式和签名
        return jwtUtils.validateToken(token);
    }

    @Override
    @Transactional
    public void revokeToken(String token) {
        try {
            // 将Token加入黑名单
            Long expireTime = jwtUtils.getExpirationFromToken(token);
            long ttl = expireTime - System.currentTimeMillis();

            if (ttl > 0) {
                String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
                redisTemplate.opsForValue().set(blacklistKey, "revoked", ttl, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            log.error("撤销Token失败：{}", e.getMessage());
        }
    }

    /**
     * 检查账户是否被锁定
     */
    private void checkAccountLocked(String username) {
        String failureKey = LOGIN_FAILURE_PREFIX + username;
        Integer failureCount = (Integer) redisTemplate.opsForValue().get(failureKey);

        if (failureCount != null && failureCount >= MAX_LOGIN_FAILURE_COUNT) {
            throw new RuntimeException("登录失败次数过多，账户已被锁定" + ACCOUNT_LOCK_DURATION_MINUTES + "分钟");
        }
    }

    /**
     * 验证验证码
     */
    private void validateCaptcha(String captchaKey, String captcha) {
        String cacheKey = CAPTCHA_PREFIX + captchaKey;
        String cachedCaptcha = (String) redisTemplate.opsForValue().get(cacheKey);

        if (!StringUtils.hasText(cachedCaptcha)) {
            throw new RuntimeException("验证码已过期");
        }

        if (!cachedCaptcha.equalsIgnoreCase(captcha)) {
            throw new RuntimeException("验证码错误");
        }

        // 验证成功后删除验证码
        redisTemplate.delete(cacheKey);
    }

    /**
     * 检查是否启用了MFA
     */
    private boolean checkMfaEnabled(Long userId) {
        try {
            AuthMfaConfig config = authMfaConfigMapper.selectOne(
                    new LambdaQueryWrapper<AuthMfaConfig>()
                            .eq(AuthMfaConfig::getUserId, userId)
                            .eq(AuthMfaConfig::getStatus, "ACTIVE")
            );
            return config != null;
        } catch (Exception e) {
            log.error("检查MFA状态失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 验证MFA验证码
     */
    private boolean verifyMfaCode(Long userId, String mfaCode) {
        try {
            AuthMfaConfig config = authMfaConfigMapper.selectOne(
                    new LambdaQueryWrapper<AuthMfaConfig>()
                            .eq(AuthMfaConfig::getUserId, userId)
                            .eq(AuthMfaConfig::getStatus, "ACTIVE")
            );
            if (config == null) {
                return false;
            }

            // 使用TOTP验证
            com.warrenstrange.googleauth.GoogleAuthenticator gAuth =
                    new com.warrenstrange.googleauth.GoogleAuthenticator();
            int code = Integer.parseInt(mfaCode);

            // 允许±1时间窗口
            if (gAuth.authorize(config.getSecretKey(), code)) {
                return true;
            }
            return gAuth.authorize(config.getSecretKey(), code, 1);
        } catch (Exception e) {
            log.error("MFA验证码验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 生成刷新令牌
     */
    private String generateRefreshToken(Long userId, String username) {
        // 刷新令牌的生成逻辑与访问令牌类似，但过期时间更长
        return jwtUtils.generateToken(userId, username);
    }

    /**
     * 创建会话
     */
    private void createSession(SysUser user, String accessToken, String refreshToken, HttpServletRequest request) {
        AuthSession session = new AuthSession();
        session.setUserId(user.getUserId());
        session.setUsername(user.getUsername());
        session.setToken(accessToken);
        session.setRefreshToken(refreshToken);
        session.setLoginTime(LocalDateTime.now());
        session.setLastAccessTime(LocalDateTime.now());
        session.setExpireTime(LocalDateTime.now().plusSeconds(TOKEN_EXPIRATION_SECONDS));
        session.setLoginIp(getClientIp(request));
        session.setDeviceInfo(getDeviceInfo(request));
        session.setBrowserInfo(getBrowserInfo(request));
        session.setStatus("ACTIVE");

        authSessionMapper.insert(session);
    }

    /**
     * 更新会话Token
     */
    private void updateSessionToken(Long userId, String oldRefreshToken, String newAccessToken, String newRefreshToken) {
        AuthSession session = authSessionMapper.selectOne(
                new LambdaQueryWrapper<AuthSession>()
                        .eq(AuthSession::getUserId, userId)
                        .eq(AuthSession::getRefreshToken, oldRefreshToken)
                        .eq(AuthSession::getStatus, "ACTIVE")
        );

        if (session != null) {
            session.setToken(newAccessToken);
            session.setRefreshToken(newRefreshToken);
            session.setLastAccessTime(LocalDateTime.now());
            session.setExpireTime(LocalDateTime.now().plusSeconds(TOKEN_EXPIRATION_SECONDS));
            authSessionMapper.updateById(session);
        }
    }

    /**
     * 失效用户所有会话
     */
    private void invalidateUserSessions(Long userId) {
        authSessionMapper.update(null,
                new LambdaUpdateWrapper<AuthSession>()
                        .eq(AuthSession::getUserId, userId)
                        .set(AuthSession::getStatus, "INVALID")
        );
    }

    /**
     * 处理登录失败
     */
    private void handleLoginFailure(SysUser user, HttpServletRequest request) {
        String username = user.getUsername();
        String failureKey = LOGIN_FAILURE_PREFIX + username;

        // 增加失败计数
        Integer failureCount = (Integer) redisTemplate.opsForValue().get(failureKey);
        if (failureCount == null) {
            failureCount = 0;
        }
        failureCount++;

        // 设置失败计数和过期时间
        redisTemplate.opsForValue().set(failureKey, failureCount, ACCOUNT_LOCK_DURATION_MINUTES, TimeUnit.MINUTES);

        // 记录登录失败日志
        recordLoginFailure(username, "密码错误", request);

        // 检查是否需要锁定账户
        if (failureCount >= MAX_LOGIN_FAILURE_COUNT) {
            lockAccount(user, "密码错误次数过多");
        }
    }

    /**
     * 锁定账户
     */
    private void lockAccount(SysUser user, String reason) {
        // 更新用户状态
        user.setStatus("LOCKED");
        sysUserMapper.updateById(user);

        // 记录账户锁定信息
        AuthAccountLock lockRecord = new AuthAccountLock();
        lockRecord.setUserId(user.getUserId());
        lockRecord.setUsername(user.getUsername());
        lockRecord.setLockType("PASSWORD_FAILURE");
        lockRecord.setLockReason(reason);
        lockRecord.setLockTime(LocalDateTime.now());
        lockRecord.setLockDuration((int) ACCOUNT_LOCK_DURATION_MINUTES);
        lockRecord.setStatus("LOCKED");
        authAccountLockMapper.insert(lockRecord);

        log.warn("账户{}已被锁定，原因：{}", user.getUsername(), reason);
    }

    /**
     * 清除登录失败计数
     */
    private void clearLoginFailureCount(String username) {
        String failureKey = LOGIN_FAILURE_PREFIX + username;
        redisTemplate.delete(failureKey);
    }

    /**
     * 记录登录成功日志
     */
    private void recordLoginSuccess(SysUser user, HttpServletRequest request) {
        AuthLoginLog loginLog = new AuthLoginLog();
        loginLog.setUserId(user.getUserId());
        loginLog.setUsername(user.getUsername());
        loginLog.setRealName(user.getRealName());
        loginLog.setLoginTime(LocalDateTime.now());
        loginLog.setLoginIp(getClientIp(request));
        loginLog.setDeviceInfo(getDeviceInfo(request));
        loginLog.setBrowserInfo(getBrowserInfo(request));
        loginLog.setLoginStatus("SUCCESS");
        authLoginLogMapper.insert(loginLog);

        // 更新用户最后登录信息
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(getClientIp(request));
        sysUserMapper.updateById(user);
    }

    /**
     * 记录登录失败日志
     */
    private void recordLoginFailure(String username, String reason, HttpServletRequest request) {
        AuthLoginLog loginLog = new AuthLoginLog();
        loginLog.setUsername(username);
        loginLog.setLoginTime(LocalDateTime.now());
        loginLog.setLoginIp(getClientIp(request));
        loginLog.setDeviceInfo(getDeviceInfo(request));
        loginLog.setBrowserInfo(getBrowserInfo(request));
        loginLog.setLoginStatus("FAILURE");
        loginLog.setFailureReason(reason);
        authLoginLogMapper.insert(loginLog);
    }

    /**
     * 记录登出日志
     */
    private void recordLogout(Long userId, String username, HttpServletRequest request) {
        AuthLoginLog loginLog = new AuthLoginLog();
        loginLog.setUserId(userId);
        loginLog.setUsername(username);
        loginLog.setLoginTime(LocalDateTime.now());
        loginLog.setLoginIp(getClientIp(request));
        loginLog.setLoginStatus("LOGOUT");
        authLoginLogMapper.insert(loginLog);
    }

    /**
     * 检查Token是否在黑名单中
     */
    private boolean isTokenBlacklisted(String token) {
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey));
    }

    /**
     * 构建登录结果
     */
    private LoginResultDTO buildLoginResult(SysUser user, String accessToken, String refreshToken) {
        LoginResultDTO result = new LoginResultDTO();
        result.setAccessToken(accessToken);
        result.setRefreshToken(refreshToken);
        result.setTokenType("Bearer");
        result.setExpiresIn(TOKEN_EXPIRATION_SECONDS);

        // 构建用户信息
        LoginResultDTO.UserInfo userInfo = new LoginResultDTO.UserInfo();
        userInfo.setUserId(user.getUserId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setEmail(user.getEmail());
        userInfo.setPhone(user.getPhone());
        userInfo.setMfaEnabled(checkMfaEnabled(user.getUserId()));

        // 获取用户角色和权限
        List<String> roles = getUserRoles(user.getUserId());
        List<String> permissions = getUserPermissions(user.getUserId());
        userInfo.setRoles(roles);
        userInfo.setPermissions(permissions);

        result.setUserInfo(userInfo);
        return result;
    }

    /**
     * 获取用户角色
     */
    private List<String> getUserRoles(Long userId) {
        return sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, userId)
        ).stream().map(ur -> {
            SysRole role = sysRoleMapper.selectById(ur.getRoleId());
            return role != null ? role.getRoleCode() : null;
        }).filter(role -> role != null).collect(Collectors.toList());
    }

    /**
     * 获取用户权限
     */
    private List<String> getUserPermissions(Long userId) {
        // 这里应该通过角色权限关联表获取用户权限
        // 简化实现，返回空列表
        return List.of();
    }

    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
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
    private String getDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        // 简化处理，返回User-Agent
        return userAgent;
    }

    /**
     * 获取浏览器信息
     */
    private String getBrowserInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        // 简化处理，返回User-Agent
        return userAgent;
    }
    
    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        // 查询用户
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
            .eq(SysUser::getUsername, username)
            .eq(SysUser::getDeleted, 0));
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        sysUserMapper.updateById(user);
    }
}
