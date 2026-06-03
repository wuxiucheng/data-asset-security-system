package com.dataasset.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataasset.security.entity.SysRolePermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色权限关联Mapper接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {
}
