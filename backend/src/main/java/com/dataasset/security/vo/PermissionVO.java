package com.dataasset.security.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 权限信息VO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "权限信息")
public class PermissionVO {

    @Schema(description = "权限ID")
    private Long permissionId;

    @Schema(description = "权限编码")
    private String permissionCode;

    @Schema(description = "权限名称")
    private String permissionName;

    @Schema(description = "权限类型：MENU, BUTTON, API")
    private String permissionType;

    @Schema(description = "父权限ID")
    private Long parentId;

    @Schema(description = "路由路径")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "状态：ACTIVE, INACTIVE")
    private String status;

    @Schema(description = "子权限列表")
    private List<PermissionVO> children;
}
