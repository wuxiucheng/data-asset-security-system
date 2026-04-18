package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新审批流程定义请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "更新审批流程定义请求")
public class ApprovalProcessDefinitionUpdateDTO {

    @Schema(description = "流程定义ID", required = true)
    @NotNull(message = "流程定义ID不能为空")
    private Long definitionId;

    @Schema(description = "流程定义名称")
    @Size(max = 128, message = "流程定义名称长度不能超过128")
    private String processDefinitionName;

    @Schema(description = "流程定义描述")
    @Size(max = 500, message = "流程定义描述长度不能超过500")
    private String processDefinitionDescription;

    @Schema(description = "状态：DRAFT, ACTIVE, INACTIVE, ARCHIVED")
    @Size(max = 32, message = "状态长度不能超过32")
    private String status;

    @Schema(description = "是否启用")
    private Boolean enabled;
}
