package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色权限关联实体类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@TableName("sys_role_permission")
public class SysRolePermission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色权限ID
     */
    @TableId(value = "role_permission_id")
    private Long rolePermissionId;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 权限ID
     */
    private Long permissionId;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}
