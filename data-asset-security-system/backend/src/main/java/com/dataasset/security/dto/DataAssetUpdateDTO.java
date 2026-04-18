package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新数据资产请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "更新数据资产请求")
public class DataAssetUpdateDTO {

    @Schema(description = "资产ID", required = true)
    @NotNull(message = "资产ID不能为空")
    private Long assetId;

    @Schema(description = "资产名称")
    @Size(max = 128, message = "资产名称长度不能超过128")
    private String assetName;

    @Schema(description = "资产描述")
    @Size(max = 500, message = "资产描述长度不能超过500")
    private String assetDescription;

    @Schema(description = "责任部门ID")
    private Long departmentId;

    @Schema(description = "责任人ID")
    private Long ownerId;

    @Schema(description = "分类ID")
    private Long classificationId;

    @Schema(description = "分级ID")
    private Long gradingId;

    @Schema(description = "数据量级别")
    @Size(max = 16, message = "数据量级别长度不能超过16")
    private String dataVolumeLevel;

    @Schema(description = "访问频率")
    @Size(max = 16, message = "访问频率长度不能超过16")
    private String accessFrequency;

    @Schema(description = "数据重要性")
    @Size(max = 16, message = "数据重要性长度不能超过16")
    private String importanceLevel;

    @Schema(description = "是否包含敏感数据")
    private Boolean containsSensitiveData;

    @Schema(description = "敏感数据类型")
    @Size(max = 128, message = "敏感数据类型长度不能超过128")
    private String sensitiveDataType;

    @Schema(description = "过期时间")
    private String expireTime;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过500")
    private String remarks;

    @Schema(description = "状态：DRAFT, ACTIVE, INACTIVE, ARCHIVED")
    @Size(max = 32, message = "状态长度不能超过32")
    private String status;
}
