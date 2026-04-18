package com.dataasset.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataasset.security.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户 Mapper 接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
