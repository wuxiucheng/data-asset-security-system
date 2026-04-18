package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限树查询DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "权限树查询条件")
public class PermissionTreeQueryDTO {

    @Schema(description = "权限类型：MENU, BUTTON, API")
    private String permissionType;

    @Schema(description = "权限名称")
    private String permissionName;

    @Schema(description = "状态：ACTIVE, INACTIVE")
    private String status;
}
