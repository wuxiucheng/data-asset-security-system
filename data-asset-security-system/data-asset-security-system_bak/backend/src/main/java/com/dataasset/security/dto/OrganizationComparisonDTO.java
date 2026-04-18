package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 组织架构对比结果DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "组织架构对比结果")
public class OrganizationComparisonDTO {

    @Schema(description = "新增的部门")
    private int newDepartments;

    @Schema(description = "新增的责任人")
    private int newOwners;

    @Schema(description = "删除的部门")
    private int deletedDepartments;

    @Schema(description = "删除的责任人")
    private int deletedOwners;

    @Schema(description = "更新的部门")
    private int updatedDepartments;

    @Schema(description = "更新的责任人")
    private int updatedOwners;

    @Schema(description = "差异详情")
    private String details;
}
