package com.dataasset.security.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建数据分级请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "创建数据分级请求")
public class DataGradingCreateDTO {

    @Schema(description = "标准ID", required = true)
    @NotNull(message = "标准ID不能为空")
    private Long standardId;

    @Schema(description = "分级编码", required = true)
    @NotBlank(message = "分级编码不能为空")
    @Size(max = 64, message = "分级编码长度不能超过64")
    private String gradingCode;

    @Schema(description = "分级名称", required = true)
    @NotBlank(message = "分级名称不能为空")
    @Size(max = 128, message = "分级名称长度不能超过128")
    private String gradingName;

    @Schema(description = "分级描述")
    @Size(max = 500, message = "分级描述长度不能超过500")
    @JsonAlias("description")
    private String gradingDescription;

    @Schema(description = "等级值", required = true)
    @NotNull(message = "等级值不能为空")
    private Integer levelValue;

    @Schema(description = "颜色标识")
    @Size(max = 32, message = "颜色标识长度不能超过32")
    private String colorCode;

    @Schema(description = "安全要求")
    @Size(max = 1000, message = "安全要求长度不能超过1000")
    @JsonAlias("securityMeasures")
    private String securityRequirements;

    @Schema(description = "访问控制")
    @Size(max = 1000, message = "访问控制长度不能超过1000")
    private String accessControl;

    @Schema(description = "保留期限")
    @Size(max = 100, message = "保留期限长度不能超过100")
    private String retentionPeriod;

    @Schema(description = "排序")
    private Integer sortOrder;
}
