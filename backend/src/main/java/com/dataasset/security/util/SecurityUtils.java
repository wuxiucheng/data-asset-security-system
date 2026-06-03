package com.dataasset.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 安全工具类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public class SecurityUtils {

    /**
     * 获取当前认证信息
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            // 假设UserDetails的实现类有userId字段
            // 这里需要根据实际情况调整
            return 1L; // 临时返回，需要根据实际UserDetails实现调整
        }
        return 1L;
    }

    /**
     * 判断当前用户是否已认证
     */
    public static boolean isAuthenticated() {
        Authentication authentication = getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}
