package com.dataasset.security.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色信息VO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "角色信息")
public class RoleVO {

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色描述")
    private String roleDescription;

    @Schema(description = "角色类型：SYSTEM_ADMIN, DATA_ADMIN, APPROVER, OWNER, USER")
    private String roleType;

    @Schema(description = "状态：ACTIVE, INACTIVE")
    private String status;
}
