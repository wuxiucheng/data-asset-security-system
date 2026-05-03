package com.dataasset.security.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建数据资产请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "创建数据资产请求")
public class DataAssetCreateDTO {

    @Schema(description = "资产名称", required = true)
    @NotBlank(message = "资产名称不能为空")
    @Size(max = 128, message = "资产名称长度不能超过128")
    private String assetName;

    @Schema(description = "资产编码", required = true)
    @NotBlank(message = "资产编码不能为空")
    @Size(max = 64, message = "资产编码长度不能超过64")
    private String assetCode;

    @Schema(description = "资产类型", required = true)
    @NotBlank(message = "资产类型不能为空")
    @Size(max = 32, message = "资产类型长度不能超过32")
    private String assetType;

    @Schema(description = "所属系统")
    @Size(max = 64, message = "所属系统长度不能超过64")
    private String systemName;

    @Schema(description = "数据库类型")
    @Size(max = 32, message = "数据库类型长度不能超过32")
    private String databaseType;

    @Schema(description = "数据库地址")
    @Size(max = 128, message = "数据库地址长度不能超过128")
    private String databaseHost;

    @Schema(description = "数据库端口")
    private Integer databasePort;

    @Schema(description = "数据库名称")
    @Size(max = 64, message = "数据库名称长度不能超过64")
    private String databaseName;

    @Schema(description = "表名")
    @Size(max = 64, message = "表名长度不能超过64")
    private String tableName;

    @Schema(description = "关联数据源ID")
    private Long dataSourceId;

    @Schema(description = "资产描述")
    @Size(max = 500, message = "资产描述长度不能超过500")
    @JsonAlias("description")
    private String assetDescription;

    @Schema(description = "责任部门ID", required = true)
    @NotNull(message = "责任部门ID不能为空")
    private Long departmentId;

    @Schema(description = "责任人ID", required = true)
    @NotNull(message = "责任人ID不能为空")
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
}
