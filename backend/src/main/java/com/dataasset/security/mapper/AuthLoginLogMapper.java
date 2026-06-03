package com.dataasset.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataasset.security.entity.AuthLoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志Mapper接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Mapper
public interface AuthLoginLogMapper extends BaseMapper<AuthLoginLog> {
}