package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新权限请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "更新权限请求")
public class PermissionUpdateDTO {

    @Schema(description = "权限ID", required = true)
    @NotNull(message = "权限ID不能为空")
    private Long permissionId;

    @Schema(description = "权限名称", example = "创建用户")
    @Size(max = 128, message = "权限名称长度不能超过128")
    private String permissionName;

    @Schema(description = "权限类型：MENU, BUTTON, API")
    @Pattern(regexp = "^(MENU|BUTTON|API)$", message = "权限类型只能是MENU、BUTTON或API")
    private String permissionType;

    @Schema(description = "父权限ID")
    private Long parentId;

    @Schema(description = "路由路径", example = "/users")
    private String path;

    @Schema(description = "组件路径", example = "@/views/users/index")
    private String component;

    @Schema(description = "图标", example = "User")
    private String icon;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "状态：ACTIVE, INACTIVE")
    @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "状态只能是ACTIVE或INACTIVE")
    private String status;
}
