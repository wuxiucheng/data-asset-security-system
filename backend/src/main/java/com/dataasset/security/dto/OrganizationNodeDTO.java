package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 组织架构节点DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "组织架构节点")
public class OrganizationNodeDTO {

    @Schema(description = "节点ID")
    private String nodeId;

    @Schema(description = "节点名称")
    private String nodeName;

    @Schema(description = "节点类型：DEPARTMENT, POSITION")
    private String nodeType;

    @Schema(description = "父节点ID")
    private String parentNodeId;

    @Schema(description = "负责人姓名")
    private String leaderName;

    @Schema(description = "负责人工号")
    private String leaderEmployeeNo;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "职务")
    private String position;

    @Schema(description = "子节点列表")
    private List<OrganizationNodeDTO> children;
}
