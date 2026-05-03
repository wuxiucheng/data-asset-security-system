package com.dataasset.security.common.aspect;

import com.dataasset.security.common.annotation.RequirePermission;
import com.dataasset.security.common.exception.PermissionDeniedException;
import com.dataasset.security.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限验证切面
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    @Before("@annotation(requirePermission)")
    public void doBefore(JoinPoint joinPoint, RequirePermission requirePermission) {
        // 获取当前用户信息
        CustomUserDetails userDetails = getCurrentUserDetails();
        if (userDetails == null) {
            throw new PermissionDeniedException("未登录或会话已过期");
        }

        // 获取用户权限列表
        List<String> userPermissionList = userDetails.getPermissions();
        Set<String> userPermissions = userPermissionList != null ? new HashSet<>(userPermissionList) : new HashSet<>();
        if (userPermissions.isEmpty()) {
            throw new PermissionDeniedException("用户没有任何权限");
        }

        // 获取需要的权限
        String[] requiredPermissions = requirePermission.value();
        if (requiredPermissions.length == 0) {
            // 没有指定权限，默认通过
            return;
        }

        // 验证权限
        boolean hasPermission = checkPermission(userPermissions, requiredPermissions, requirePermission.logical());

        if (!hasPermission) {
            log.warn("用户{}权限验证失败，需要权限：{}，用户权限：{}",
                    userDetails.getUsername(),
                    Arrays.toString(requiredPermissions),
                    userPermissions);
            throw new PermissionDeniedException(requirePermission.message());
        }

        log.debug("用户{}权限验证通过", userDetails.getUsername());
    }

    /**
     * 检查权限
     */
    private boolean checkPermission(Set<String> userPermissions, String[] requiredPermissions, String logical) {
        List<String> requiredList = Arrays.asList(requiredPermissions);

        if ("AND".equalsIgnoreCase(logical)) {
            // AND模式：所有权限都必须满足
            return userPermissions.containsAll(requiredList);
        } else {
            // OR模式：满足任意一个权限即可
            return requiredList.stream().anyMatch(userPermissions::contains);
        }
    }

    /**
     * 获取当前用户详情
     */
    private CustomUserDetails getCurrentUserDetails() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                return (CustomUserDetails) authentication.getPrincipal();
            }
        } catch (Exception e) {
            log.error("获取当前用户信息失败：{}", e.getMessage());
        }
        return null;
    }
}
