package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建审批流程定义请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "创建审批流程定义请求")
public class ApprovalProcessDefinitionCreateDTO {

    @Schema(description = "流程定义Key", required = true)
    @NotBlank(message = "流程定义Key不能为空")
    @Size(max = 64, message = "流程定义Key长度不能超过64")
    private String processDefinitionKey;

    @Schema(description = "流程定义名称", required = true)
    @NotBlank(message = "流程定义名称不能为空")
    @Size(max = 128, message = "流程定义名称长度不能超过128")
    private String processDefinitionName;

    @Schema(description = "流程定义描述")
    @Size(max = 500, message = "流程定义描述长度不能超过500")
    private String processDefinitionDescription;

    @Schema(description = "流程类型", required = true)
    @NotBlank(message = "流程类型不能为空")
    @Size(max = 32, message = "流程类型长度不能超过32")
    private String processType;

    @Schema(description = "BPMN流程定义内容", required = true)
    @NotBlank(message = "BPMN流程定义内容不能为空")
    private String bpmnContent;

    @Schema(description = "流程资源名称")
    @Size(max = 128, message = "流程资源名称长度不能超过128")
    private String resourceName;
}
