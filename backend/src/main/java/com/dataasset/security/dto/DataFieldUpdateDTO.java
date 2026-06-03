package com.dataasset.security.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新数据字段请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "更新数据字段请求")
public class DataFieldUpdateDTO {

    @Schema(description = "字段ID", required = true)
    @NotNull(message = "字段ID不能为空")
    private Long fieldId;

    @Schema(description = "字段名称")
    @Size(max = 64, message = "字段名称长度不能超过64")
    private String fieldName;

    @Schema(description = "字段描述")
    @Size(max = 500, message = "字段描述长度不能超过500")
    @JsonAlias("description")
    private String fieldDescription;

    @Schema(description = "分类ID")
    private Long classificationId;

    @Schema(description = "分级ID")
    private Long gradingId;

    @Schema(description = "是否包含敏感数据")
    private Boolean containsSensitiveData;

    @Schema(description = "敏感数据类型")
    @Size(max = 128, message = "敏感数据类型长度不能超过128")
    @JsonAlias("sensitiveType")
    private String sensitiveDataType;

    @Schema(description = "风险等级")
    @Size(max = 32, message = "风险等级长度不能超过32")
    private String riskLevel;

    @Schema(description = "是否必填")
    private Boolean isRequired;

    @Schema(description = "状态：ACTIVE, INACTIVE")
    @Size(max = 32, message = "状态长度不能超过32")
    private String status;
}
