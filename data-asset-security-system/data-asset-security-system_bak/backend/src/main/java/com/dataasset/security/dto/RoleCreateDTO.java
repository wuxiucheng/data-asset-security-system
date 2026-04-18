package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 创建角色请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "创建角色请求")
public class RoleCreateDTO {

    @Schema(description = "角色编码", required = true, example = "DATA_ADMIN")
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 64, message = "角色编码长度不能超过64")
    @Pattern(regexp = "^[A-Z_]+$", message = "角色编码只能包含大写字母和下划线")
    private String roleCode;

    @Schema(description = "角色名称", required = true, example = "数据资产管理员")
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 64, message = "角色名称长度不能超过64")
    private String roleName;

    @Schema(description = "角色描述", example = "负责分类分级标准制定和维护")
    @Size(max = 500, message = "角色描述长度不能超过500")
    private String roleDescription;

    @Schema(description = "角色类型：SYSTEM_ADMIN, DATA_ADMIN, APPROVER, OWNER, USER", required = true)
    @NotBlank(message = "角色类型不能为空")
    @Pattern(regexp = "^(SYSTEM_ADMIN|DATA_ADMIN|APPROVER|OWNER|USER)$", message = "角色类型只能是SYSTEM_ADMIN、DATA_ADMIN、APPROVER、OWNER或USER")
    private String roleType;

    @Schema(description = "权限ID列表")
    private List<Long> permissionIds;
}
