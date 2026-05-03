package com.dataasset.security.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据资产信息VO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "数据资产信息")
public class DataAssetVO {

    @Schema(description = "资产ID")
    private Long assetId;

    @Schema(description = "资产名称")
    private String assetName;

    @Schema(description = "资产编码")
    private String assetCode;

    @Schema(description = "资产类型")
    private String assetType;

    @Schema(description = "所属系统")
    private String systemName;

    @Schema(description = "数据库类型")
    private String databaseType;

    @Schema(description = "数据库地址")
    private String databaseHost;

    @Schema(description = "数据库端口")
    private Integer databasePort;

    @Schema(description = "数据库名称")
    private String databaseName;

    @Schema(description = "表名")
    private String tableName;

    @Schema(description = "关联数据源ID")
    private Long dataSourceId;

    @Schema(description = "关联数据源名称")
    private String dataSourceName;

    @Schema(description = "资产描述")
    private String assetDescription;

    @Schema(description = "责任部门ID")
    private Long departmentId;

    @Schema(description = "责任部门名称")
    private String departmentName;

    @Schema(description = "责任人ID")
    private Long ownerId;

    @Schema(description = "责任人姓名")
    private String ownerName;

    @Schema(description = "分类ID")
    private Long classificationId;

    @Schema(description = "分类名称")
    private String classificationName;

    @Schema(description = "分级ID")
    private Long gradingId;

    @Schema(description = "分级名称")
    private String gradingName;

    @Schema(description = "敏感度评分")
    private Integer sensitivityScore;

    @Schema(description = "数据量级别")
    private String dataVolumeLevel;

    @Schema(description = "访问频率")
    private String accessFrequency;

    @Schema(description = "数据重要性")
    private String importanceLevel;

    @Schema(description = "状态：DRAFT, ACTIVE, INACTIVE, ARCHIVED")
    private String status;

    @Schema(description = "是否包含敏感数据")
    private Boolean containsSensitiveData;

    @Schema(description = "敏感数据类型")
    private String sensitiveDataType;

    @Schema(description = "最后扫描时间")
    private LocalDateTime lastScanTime;

    @Schema(description = "最后扫描结果")
    private String lastScanResult;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "数据条数，记录该资产对应数据表的记录总行数")
    private Long rowCount;

    @Schema(description = "备注")
    private String remarks;

    @Schema(description = "创建人ID")
    private Long creatorId;

    @Schema(description = "创建人姓名")
    private String creatorName;

    @Schema(description = "更新人ID")
    private Long updaterId;

    @Schema(description = "更新人姓名")
    private String updaterName;

    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;
}
