package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色查询DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "角色查询条件")
public class RoleQueryDTO {

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色类型：SYSTEM_ADMIN, DATA_ADMIN, APPROVER, OWNER, USER")
    private String roleType;

    @Schema(description = "状态：ACTIVE, INACTIVE")
    private String status;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "当前页码")
    private Long current = 1L;

    @Schema(description = "每页大小")
    private Long size = 10L;
}
