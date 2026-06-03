package com.dataasset.security.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson配置类
 * 解决LocalDateTime等Java 8时间类型序列化问题
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Configuration
public class JacksonConfig {

    /**
     * 配置ObjectMapper，支持Java 8时间类型
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 注册JavaTimeModule，支持LocalDateTime等时间类型
        objectMapper.registerModule(new JavaTimeModule());
        
        // 禁用将日期写为时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // 忽略未知属性，避免前端发送多余字段时报错
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        return objectMapper;
    }
}
