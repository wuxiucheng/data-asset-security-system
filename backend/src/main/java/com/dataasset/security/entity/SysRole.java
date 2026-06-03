package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色实体类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@TableName("sys_role")
public class SysRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @TableId(value = "role_id")
    private Long roleId;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色描述
     */
    private String roleDescription;

    /**
     * 角色类型：SYSTEM_ADMIN, DATA_ADMIN, APPROVER, OWNER, USER
     */
    private String roleType;

    /**
     * 状态：ACTIVE, INACTIVE
     */
    private String status;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新人ID
     */
    private Long updatedBy;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 删除标记：0-未删除，1-已删除
     */
    private Integer deleted;
}
