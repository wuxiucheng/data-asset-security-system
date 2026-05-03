package com.dataasset.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataasset.security.entity.AuthMfaConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 多因素认证配置Mapper接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Mapper
public interface AuthMfaConfigMapper extends BaseMapper<AuthMfaConfig> {
}