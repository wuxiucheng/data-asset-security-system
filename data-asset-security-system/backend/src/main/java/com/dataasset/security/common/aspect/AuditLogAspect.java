package com.dataasset.security.common.aspect;

import com.alibaba.fastjson2.JSON;
import com.dataasset.security.common.annotation.AuditLog;
import com.dataasset.security.common.enums.ObjectTypeEnum;
import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.security.CustomUserDetails;
import com.dataasset.security.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * 审计日志切面
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogService auditLogService;

    @Around("@annotation(auditLog)")
    public Object around(ProceedingJoinPoint joinPoint, com.dataasset.security.common.annotation.AuditLog auditLog) throws Throwable {
        // 获取请求信息
        HttpServletRequest request = getRequest();
        if (request == null) {
            return joinPoint.proceed();
        }

        // 获取当前用户信息
        CustomUserDetails userDetails = getCurrentUser();
        if (userDetails == null) {
            return joinPoint.proceed();
        }

        // 记录开始时间
        long startTime = System.currentTimeMillis();

        // 执行目标方法
        Object result = null;
        String operationResult = "SUCCESS";
        String errorMessage = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            operationResult = "FAILURE";
            errorMessage = e.getMessage();
            throw e;
        } finally {
            // 记录审计日志
            try {
                recordAuditLog(joinPoint, auditLog, request, userDetails, operationResult, errorMessage, startTime);
            } catch (Exception e) {
                log.error("记录审计日志失败：{}", e.getMessage());
            }
        }
    }

    /**
     * 获取请求信息
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 获取当前用户信息
     */
    private CustomUserDetails getCurrentUser() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof CustomUserDetails) {
                return (CustomUserDetails) principal;
            }
        } catch (Exception e) {
            log.warn("获取当前用户信息失败：{}", e.getMessage());
        }
        return null;
    }

    /**
     * 记录审计日志
     */
    private void recordAuditLog(ProceedingJoinPoint joinPoint, AuditLog auditLog,
                               HttpServletRequest request, CustomUserDetails userDetails,
                               String operationResult, String errorMessage, long startTime) {
        try {
            // 构建审计日志对象
            com.dataasset.security.entity.AuditLog log = new com.dataasset.security.entity.AuditLog();

            // 操作类型
            OperationTypeEnum operationType = auditLog.operationType();
            log.setOperationType(operationType.getCode());

            // 操作人信息
            log.setOperatorId(userDetails.getUserId());
            log.setOperatorName(userDetails.getRealName());

            // 操作时间
            log.setOperationTime(LocalDateTime.now());

            // 操作对象类型
            ObjectTypeEnum objectType = auditLog.objectType();
            log.setObjectType(objectType.getCode());

            // 操作内容
            String operationContent = buildOperationContent(joinPoint, auditLog, errorMessage);
            log.setOperationContent(operationContent);

            // 操作结果
            log.setOperationResult(operationResult);

            // 请求信息
            log.setIpAddress(getClientIpAddress(request));
            log.setUserAgent(request.getHeader("User-Agent"));
            log.setRequestMethod(request.getMethod());
            log.setRequestUrl(request.getRequestURI());

            // 创建时间
            log.setCreatedTime(LocalDateTime.now());

            // 保存审计日志（异步处理）
            auditLogService.saveLog(log);

            log.debug("审计日志记录成功：{}", operationContent);

        } catch (Exception e) {
            log.error("构建审计日志失败：{}", e.getMessage());
        }
    }

    /**
     * 构建操作内容
     */
    private String buildOperationContent(ProceedingJoinPoint joinPoint, AuditLog auditLog, String errorMessage) {
        StringBuilder content = new StringBuilder();

        // 添加注解描述
        if (auditLog.description() != null && !auditLog.description().isEmpty()) {
            content.append(auditLog.description());
        } else {
            // 获取方法名
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String methodName = signature.getName();
            content.append(methodName);
        }

        // 添加参数信息
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            content.append("，参数：");
            try {
                content.append(JSON.toJSONString(args));
            } catch (Exception e) {
                content.append("参数序列化失败");
            }
        }

        // 添加错误信息
        if (errorMessage != null && !errorMessage.isEmpty()) {
            content.append("，错误：").append(errorMessage);
        }

        return content.toString();
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
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
        // 对于多个代理的情况，取第一个IP
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        return ipAddress;
    }
}
