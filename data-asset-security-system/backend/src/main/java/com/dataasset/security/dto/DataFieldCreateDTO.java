package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建数据字段请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "创建数据字段请求")
public class DataFieldCreateDTO {

    @Schema(description = "资产ID", required = true)
    @NotNull(message = "资产ID不能为空")
    private Long assetId;

    @Schema(description = "字段名称", required = true)
    @NotBlank(message = "字段名称不能为空")
    @Size(max = 64, message = "字段名称长度不能超过64")
    private String fieldName;

    @Schema(description = "字段编码", required = true)
    @NotBlank(message = "字段编码不能为空")
    @Size(max = 64, message = "字段编码长度不能超过64")
    private String fieldCode;

    @Schema(description = "字段类型", required = true)
    @NotBlank(message = "字段类型不能为空")
    @Size(max = 32, message = "字段类型长度不能超过32")
    private String fieldType;

    @Schema(description = "字段长度")
    private Integer fieldLength;

    @Schema(description = "是否为空")
    private Boolean nullable;

    @Schema(description = "是否为主键")
    private Boolean isPrimaryKey;

    @Schema(description = "字段描述")
    @Size(max = 500, message = "字段描述长度不能超过500")
    private String fieldDescription;

    @Schema(description = "分类ID")
    private Long classificationId;

    @Schema(description = "分级ID")
    private Long gradingId;

    @Schema(description = "是否包含敏感数据")
    private Boolean containsSensitiveData;

    @Schema(description = "敏感数据类型")
    @Size(max = 128, message = "敏感数据类型长度不能超过128")
    private String sensitiveDataType;

    @Schema(description = "默认值")
    @Size(max = 256, message = "默认值长度不能超过256")
    private String defaultValue;

    @Schema(description = "排序")
    private Integer sortOrder;
}
