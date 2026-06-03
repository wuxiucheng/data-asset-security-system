package com.dataasset.security.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门信息VO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "部门信息")
public class DepartmentVO {

    @Schema(description = "部门ID")
    private Long departmentId;

    @Schema(description = "部门编码")
    private String departmentCode;

    @Schema(description = "部门名称")
    private String departmentName;

    @Schema(description = "部门负责人ID")
    private Long leaderId;

    @Schema(description = "部门负责人姓名")
    private String leaderName;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "部门描述")
    private String departmentDescription;

    @Schema(description = "上级部门ID")
    private Long parentId;

    @Schema(description = "上级部门名称")
    private String parentName;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "状态：ACTIVE, INACTIVE")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;

    @Schema(description = "子部门列表")
    private List<DepartmentVO> children;
}
