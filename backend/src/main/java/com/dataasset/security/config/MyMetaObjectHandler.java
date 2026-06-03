package com.dataasset.security.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus自动填充配置
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "createdBy", Long.class, getCurrentUserId());
        this.strictInsertFill(metaObject, "updatedBy", Long.class, getCurrentUserId());
    }

    /**
     * 更新时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updatedBy", Long.class, getCurrentUserId());
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        try {
            // 从Spring Security上下文获取当前用户ID
            org.springframework.security.core.Authentication authentication = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof com.dataasset.security.security.CustomUserDetails) {
                    return ((com.dataasset.security.security.CustomUserDetails) principal).getUserId();
                }
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return null;
    }
}
