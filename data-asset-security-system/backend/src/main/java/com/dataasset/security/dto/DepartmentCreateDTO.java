package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建部门请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "创建部门请求")
public class DepartmentCreateDTO {

    @Schema(description = "部门编码", required = true, example = "DEPT001")
    @NotBlank(message = "部门编码不能为空")
    @Size(max = 64, message = "部门编码长度不能超过64")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "部门编码只能包含大写字母、数字和下划线")
    private String departmentCode;

    @Schema(description = "部门名称", required = true, example = "技术部")
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 255, message = "部门名称长度不能超过255")
    private String departmentName;

    @Schema(description = "部门负责人ID", example = "1001")
    private Long leaderId;

    @Schema(description = "联系电话", example = "010-12345678")
    @Size(max = 20, message = "联系电话长度不能超过20")
    private String contactPhone;

    @Schema(description = "部门描述", example = "负责技术研发工作")
    @Size(max = 500, message = "部门描述长度不能超过500")
    private String departmentDescription;

    @Schema(description = "上级部门ID")
    private Long parentId;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;
}
