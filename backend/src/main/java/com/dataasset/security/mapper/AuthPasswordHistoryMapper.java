package com.dataasset.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataasset.security.entity.AuthPasswordHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 密码历史Mapper接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Mapper
public interface AuthPasswordHistoryMapper extends BaseMapper<AuthPasswordHistory> {
}