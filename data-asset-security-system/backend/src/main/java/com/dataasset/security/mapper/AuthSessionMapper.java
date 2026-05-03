package com.dataasset.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataasset.security.entity.AuthSession;
import org.apache.ibatis.annotations.Mapper;

/**
 * 认证会话Mapper接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Mapper
public interface AuthSessionMapper extends BaseMapper<AuthSession> {
}