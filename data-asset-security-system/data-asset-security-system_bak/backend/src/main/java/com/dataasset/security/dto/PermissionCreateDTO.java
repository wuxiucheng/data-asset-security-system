package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建权限请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "创建权限请求")
public class PermissionCreateDTO {

    @Schema(description = "权限编码", required = true, example = "user:create")
    @NotBlank(message = "权限编码不能为空")
    @Size(max = 128, message = "权限编码长度不能超过128")
    private String permissionCode;

    @Schema(description = "权限名称", required = true, example = "创建用户")
    @NotBlank(message = "权限名称不能为空")
    @Size(max = 128, message = "权限名称长度不能超过128")
    private String permissionName;

    @Schema(description = "权限类型：MENU, BUTTON, API", required = true)
    @NotBlank(message = "权限类型不能为空")
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
}
