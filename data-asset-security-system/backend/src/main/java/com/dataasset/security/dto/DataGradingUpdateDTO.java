package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新数据分级请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "更新数据分级请求")
public class DataGradingUpdateDTO {

    @Schema(description = "分级ID", required = true)
    @NotNull(message = "分级ID不能为空")
    private Long gradingId;

    @Schema(description = "分级名称")
    @Size(max = 128, message = "分级名称长度不能超过128")
    private String gradingName;

    @Schema(description = "分级描述")
    @Size(max = 500, message = "分级描述长度不能超过500")
    private String gradingDescription;

    @Schema(description = "等级值")
    private Integer levelValue;

    @Schema(description = "颜色标识")
    @Size(max = 32, message = "颜色标识长度不能超过32")
    private String colorCode;

    @Schema(description = "安全要求")
    @Size(max = 1000, message = "安全要求长度不能超过1000")
    private String securityRequirements;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "状态：ACTIVE, INACTIVE")
    @Size(max = 32, message = "状态长度不能超过32")
    private String status;
}
