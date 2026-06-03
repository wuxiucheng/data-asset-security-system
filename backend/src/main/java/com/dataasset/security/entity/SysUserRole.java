package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户角色关联实体类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@TableName("sys_user_role")
public class SysUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户角色ID
     */
    @TableId(value = "user_role_id")
    private Long userRoleId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}
