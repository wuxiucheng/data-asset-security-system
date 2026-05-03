package com.dataasset.security.service;

import com.dataasset.security.dto.LoginDTO;
import com.dataasset.security.dto.LoginResultDTO;
import com.dataasset.security.dto.TokenRefreshDTO;

/**
 * 认证服务接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求
     * @param request  HTTP请求
     * @return 登录结果
     */
    LoginResultDTO login(LoginDTO loginDTO, jakarta.servlet.http.HttpServletRequest request);

    /**
     * 用户登出
     *
     * @param request HTTP请求
     */
    void logout(jakarta.servlet.http.HttpServletRequest request);

    /**
     * 刷新Token
     *
     * @param refreshDTO 刷新Token请求
     * @return 新的登录结果
     */
    LoginResultDTO refreshToken(TokenRefreshDTO refreshDTO);

    /**
     * 获取当前用户信息
     *
     * @return 当前用户信息
     */
    LoginResultDTO.UserInfo getCurrentUserInfo();

    /**
     * 验证Token是否有效
     *
     * @param token Token
     * @return 是否有效
     */
    boolean validateToken(String token);

    /**
     * 撤销Token
     *
     * @param token Token
     */
    void revokeToken(String token);

    /**
     * 修改密码
     */
    void changePassword(String username, String oldPassword, String newPassword);
}