package com.dataasset.security.controller;

import com.dataasset.security.common.result.Result;
import com.dataasset.security.dto.LoginDTO;
import com.dataasset.security.security.CustomUserDetails;
import com.dataasset.security.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
@Tag(name = "认证管理", description = "用户登录、登出等认证相关接口")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户使用用户名和密码登录系统")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("用户登录：{}", loginDTO.getUsername());

        // 创建认证对象
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());

        // 执行认证
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 设置认证信息到Security上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 获取用户详情
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 生成Token
        String token = jwtUtils.generateToken(userDetails.getUserId(), userDetails.getUsername());

        // 构建响应数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", Map.of(
                "userId", userDetails.getUserId(),
                "username", userDetails.getUsername(),
                "realName", userDetails.getRealName(),
                "email", userDetails.getEmail(),
                "phone", userDetails.getPhone(),
                "authorities", userDetails.getAuthorities()
        ));

        return Result.success("登录成功", data);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户退出登录")
    public Result<Void> logout() {
        log.info("用户登出");
        SecurityContextHolder.clearContext();
        return Result.success("登出成功");
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current-user")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public Result<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            Map<String, Object> data = new HashMap<>();
            data.put("userId", userDetails.getUserId());
            data.put("username", userDetails.getUsername());
            data.put("realName", userDetails.getRealName());
            data.put("email", userDetails.getEmail());
            data.put("phone", userDetails.getPhone());
            data.put("authorities", userDetails.getAuthorities());

            return Result.success(data);
        }

        return Result.error("未登录");
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refresh-token")
    @Operation(summary = "刷新Token", description = "使用当前Token刷新获取新的Token")
    public Result<Map<String, String>> refreshToken(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        String newToken = jwtUtils.refreshToken(token);

        if (newToken != null) {
            Map<String, String> data = new HashMap<>();
            data.put("token", newToken);
            return Result.success("Token刷新成功", data);
        }

        return Result.error("Token刷新失败");
    }
}
