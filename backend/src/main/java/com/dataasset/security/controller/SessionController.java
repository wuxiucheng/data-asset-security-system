package com.dataasset.security.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.common.annotation.AuditLog;
import com.dataasset.security.common.enums.ObjectTypeEnum;
import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.entity.AuthSession;
import com.dataasset.security.mapper.AuthSessionMapper;
import com.dataasset.security.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会话管理控制器
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
@Tag(name = "会话管理", description = "用户会话管理")
public class SessionController {

    private final AuthSessionMapper authSessionMapper;

    /**
     * 获取当前会话信息
     */
    @GetMapping("/current")
    @Operation(summary = "获取当前会话", description = "获取当前用户的会话信息")
    public Result<AuthSession> getCurrentSession() {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            String username = SecurityUtils.getCurrentUsername();
            
            // 查询当前用户的最新活跃会话
            AuthSession session = authSessionMapper.selectOne(new LambdaQueryWrapper<AuthSession>()
                .eq(AuthSession::getUserId, userId)
                .eq(AuthSession::getStatus, "ACTIVE")
                .orderByDesc(AuthSession::getLoginTime)
                .last("LIMIT 1"));
            
            if (session == null) {
                log.warn("用户 {} 没有活跃会话", username);
                return Result.error("未找到活跃会话");
            }
            
            return Result.success(session);
        } catch (Exception e) {
            log.error("获取当前会话失败：", e);
            return Result.error("获取当前会话失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询会话
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询会话", description = "分页查询用户会话")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.SESSION, description = "查询会话列表")
    public Result<Page<AuthSession>> querySessions(@RequestBody java.util.Map<String, Object> params) {
        Integer page = (Integer) params.getOrDefault("page", 1);
        Integer size = (Integer) params.getOrDefault("size", 10);
        
        Page<AuthSession> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<AuthSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(AuthSession::getLoginTime);
        
        Page<AuthSession> result = authSessionMapper.selectPage(pageObj, wrapper);
        return Result.success(result);
    }

    /**
     * 查询所有活跃会话
     */
    @GetMapping("/active")
    @Operation(summary = "查询活跃会话", description = "查询所有活跃会话")
    public Result<List<AuthSession>> getActiveSessions() {
        List<AuthSession> sessions = authSessionMapper.selectList(new LambdaQueryWrapper<AuthSession>()
            .eq(AuthSession::getStatus, "ACTIVE"));
        return Result.success(sessions);
    }

    /**
     * 获取当前用户的活跃会话列表
     */
    @GetMapping("/my-sessions")
    @Operation(summary = "获取我的会话", description = "获取当前用户的活跃会话列表")
    public Result<List<AuthSession>> getMyActiveSessions() {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            List<AuthSession> sessions = authSessionMapper.selectList(new LambdaQueryWrapper<AuthSession>()
                .eq(AuthSession::getUserId, userId)
                .eq(AuthSession::getStatus, "ACTIVE")
                .orderByDesc(AuthSession::getLoginTime));
            return Result.success(sessions);
        } catch (Exception e) {
            log.error("获取我的会话失败：", e);
            return Result.error("获取我的会话失败：" + e.getMessage());
        }
    }

    /**
     * 获取指定用户的活跃会话列表
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户会话", description = "获取指定用户的活跃会话列表")
    public Result<List<AuthSession>> getUserActiveSessions(@PathVariable Long userId) {
        List<AuthSession> sessions = authSessionMapper.selectList(new LambdaQueryWrapper<AuthSession>()
            .eq(AuthSession::getUserId, userId)
            .eq(AuthSession::getStatus, "ACTIVE")
            .orderByDesc(AuthSession::getLoginTime));
        return Result.success(sessions);
    }

    /**
     * 强制下线
     */
    @DeleteMapping("/{sessionId}")
    @Operation(summary = "强制下线", description = "强制用户下线")
    @AuditLog(operationType = OperationTypeEnum.LOGOUT, objectType = ObjectTypeEnum.SESSION, description = "强制用户下线")
    public Result<Void> forceLogout(@PathVariable Long sessionId) {
        AuthSession session = new AuthSession();
        session.setSessionId(sessionId);
        session.setStatus("KILLED");
        authSessionMapper.updateById(session);
        return Result.success("强制下线成功");
    }
}
