package com.dataasset.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataasset.security.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联Mapper接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
}
