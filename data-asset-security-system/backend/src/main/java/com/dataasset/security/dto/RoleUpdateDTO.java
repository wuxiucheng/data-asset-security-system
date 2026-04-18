package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 更新角色请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "更新角色请求")
public class RoleUpdateDTO {

    @Schema(description = "角色ID", required = true)
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @Schema(description = "角色名称", example = "数据资产管理员")
    @Size(max = 64, message = "角色名称长度不能超过64")
    private String roleName;

    @Schema(description = "角色描述", example = "负责分类分级标准制定和维护")
    @Size(max = 500, message = "角色描述长度不能超过500")
    private String roleDescription;

    @Schema(description = "角色类型：SYSTEM_ADMIN, DATA_ADMIN, APPROVER, OWNER, USER")
    @Pattern(regexp = "^(SYSTEM_ADMIN|DATA_ADMIN|APPROVER|OWNER|USER)$", message = "角色类型只能是SYSTEM_ADMIN、DATA_ADMIN、APPROVER、OWNER或USER")
    private String roleType;

    @Schema(description = "状态：ACTIVE, INACTIVE")
    @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "状态只能是ACTIVE或INACTIVE")
    private String status;

    @Schema(description = "权限ID列表")
    private List<Long> permissionIds;
}
