package com.dataasset.security.common.aspect;

import com.dataasset.security.common.annotation.RateLimit;
import com.dataasset.security.common.exception.RateLimitException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 接口限流切面
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Redis Lua脚本实现限流
     */
    private static final String RATE_LIMIT_LUA_SCRIPT =
            "local key = KEYS[1]\n" +
                    "local count = tonumber(ARGV[1])\n" +
                    "local time = tonumber(ARGV[2])\n" +
                    "local current = redis.call('GET', key)\n" +
                    "if current == false then\n" +
                    "    redis.call('SET', key, 1)\n" +
                    "    redis.call('EXPIRE', key, time)\n" +
                    "    return 1\n" +
                    "else\n" +
                    "    if tonumber(current) < count then\n" +
                    "        redis.call('INCR', key)\n" +
                    "        return 1\n" +
                    "    else\n" +
                    "        return 0\n" +
                    "    end\n" +
                    "end";

    @Before("@annotation(rateLimit)")
    public void doBefore(JoinPoint joinPoint, RateLimit rateLimit) {
        // 获取限流key
        String limitKey = getLimitKey(joinPoint, rateLimit);

        // 执行限流检查
        boolean allowed = checkRateLimit(limitKey, rateLimit.count(), rateLimit.time());

        if (!allowed) {
            log.warn("接口限流触发，key：{}，限制：{}/{}秒", limitKey, rateLimit.count(), rateLimit.time());
            throw new RateLimitException(rateLimit.message());
        }
    }

    /**
     * 获取限流key
     */
    private String getLimitKey(JoinPoint joinPoint, RateLimit rateLimit) {
        StringBuilder keyBuilder = new StringBuilder("rate_limit:");

        // 添加限流类型前缀
        String limitType = rateLimit.limitType();
        keyBuilder.append(limitType).append(":");

        // 根据限流类型构建key
        switch (limitType) {
            case "IP":
                keyBuilder.append(getClientIp());
                break;
            case "USER":
                keyBuilder.append(getCurrentUserId());
                break;
            case "API":
                keyBuilder.append(getRequestUri());
                break;
            default:
                keyBuilder.append("default");
        }

        // 添加自定义key
        if (rateLimit.key() != null && !rateLimit.key().isEmpty()) {
            keyBuilder.append(":").append(rateLimit.key());
        }

        return keyBuilder.toString();
    }

    /**
     * 检查是否限流
     */
    private boolean checkRateLimit(String key, int count, int time) {
        try {
            RedisScript<Long> redisScript = RedisScript.of(RATE_LIMIT_LUA_SCRIPT, Long.class);
            Long result = stringRedisTemplate.execute(
                    redisScript,
                    Collections.singletonList(key),
                    String.valueOf(count),
                    String.valueOf(time)
            );
            return result != null && result == 1;
        } catch (Exception e) {
            log.error("限流检查异常：{}", e.getMessage());
            // 异常情况下默认允许通过
            return true;
        }
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return "unknown";
            }

            HttpServletRequest request = attributes.getRequest();
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
        } catch (Exception e) {
            log.error("获取客户端IP失败：{}", e.getMessage());
            return "unknown";
        }
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof com.dataasset.security.security.CustomUserDetails) {
                com.dataasset.security.security.CustomUserDetails userDetails =
                        (com.dataasset.security.security.CustomUserDetails) authentication.getPrincipal();
                return userDetails.getUserId();
            }
        } catch (Exception e) {
            log.error("获取当前用户ID失败：{}", e.getMessage());
        }
        return 0L;
    }

    /**
     * 获取请求URI
     */
    private String getRequestUri() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return "unknown";
            }
            return attributes.getRequest().getRequestURI();
        } catch (Exception e) {
            log.error("获取请求URI失败：{}", e.getMessage());
            return "unknown";
        }
    }
}
