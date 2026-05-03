package com.dataasset.security.controller;

import com.dataasset.security.common.annotation.AuditLog;
import com.dataasset.security.common.enums.ObjectTypeEnum;
import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.dto.LoginDTO;
import com.dataasset.security.dto.LoginResultDTO;
import com.dataasset.security.dto.TokenRefreshDTO;
import com.dataasset.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户认证相关接口")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录，支持MFA验证")
    @AuditLog(operationType = OperationTypeEnum.LOGIN, objectType = ObjectTypeEnum.USER, description = "用户登录")
    public Result<LoginResultDTO> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        log.info("用户登录请求：{}", loginDTO.getUsername());
        try {
            LoginResultDTO result = authService.login(loginDTO, request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("用户登录失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出，失效当前会话")
    @AuditLog(operationType = OperationTypeEnum.LOGOUT, objectType = ObjectTypeEnum.USER, description = "用户登出")
    public Result<Void> logout(HttpServletRequest request) {
        log.info("用户登出请求");
        try {
            authService.logout(request);
            return Result.success();
        } catch (Exception e) {
            log.error("用户登出失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refresh-token")
    @Operation(summary = "刷新Token", description = "使用刷新令牌获取新的访问令牌")
    public Result<LoginResultDTO> refreshToken(@Valid @RequestBody TokenRefreshDTO refreshDTO) {
        log.info("刷新Token请求");
        try {
            LoginResultDTO result = authService.refreshToken(refreshDTO);
            return Result.success(result);
        } catch (Exception e) {
            log.error("刷新Token失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current-user")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public Result<LoginResultDTO.UserInfo> getCurrentUserInfo() {
        log.info("获取当前用户信息请求");
        try {
            LoginResultDTO.UserInfo userInfo = authService.getCurrentUserInfo();
            if (userInfo != null) {
                return Result.success(userInfo);
            } else {
                return Result.error("未登录或会话已过期");
            }
        } catch (Exception e) {
            log.error("获取当前用户信息失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 验证Token
     */
    @PostMapping("/validate-token")
    @Operation(summary = "验证Token", description = "验证Token是否有效")
    public Result<Boolean> validateToken(@RequestHeader("Authorization") String authorization) {
        log.info("验证Token请求");
        try {
            String token = extractToken(authorization);
            boolean isValid = authService.validateToken(token);
            return Result.success(isValid);
        } catch (Exception e) {
            log.error("验证Token失败：{}", e.getMessage());
            return Result.success(false);
        }
    }

    /**
     * 撤销Token
     */
    @PostMapping("/revoke-token")
    @Operation(summary = "撤销Token", description = "撤销指定的Token")
    @AuditLog(operationType = OperationTypeEnum.LOGOUT, objectType = ObjectTypeEnum.SESSION, description = "撤销Token")
    public Result<Void> revokeToken(@RequestHeader("Authorization") String authorization) {
        log.info("撤销Token请求");
        try {
            String token = extractToken(authorization);
            authService.revokeToken(token);
            return Result.success();
        } catch (Exception e) {
            log.error("撤销Token失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 从Authorization头部提取Token
     */
    private String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return authorization;
    
    }
    /**
     * 修改密码
     */
    @PostMapping("/changePassword")
    @Operation(summary = "修改密码", description = "修改用户密码")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.USER, description = "修改密码")
    public Result<Void> changePassword(@RequestBody java.util.Map<String, String> params) {
        String username = params.get("username");
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        log.info("修改密码请求：{}", username);
        try {
            authService.changePassword(username, oldPassword, newPassword);
            return Result.success("密码修改成功");
        } catch (Exception e) {
            log.error("修改密码失败：", e);
            return Result.error(e.getMessage());
        }
    }
}
