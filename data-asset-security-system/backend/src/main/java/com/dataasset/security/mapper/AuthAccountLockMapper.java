package com.dataasset.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataasset.security.entity.AuthAccountLock;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账户锁定记录Mapper接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Mapper
public interface AuthAccountLockMapper extends BaseMapper<AuthAccountLock> {
}