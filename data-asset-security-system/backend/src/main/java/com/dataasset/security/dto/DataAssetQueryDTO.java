package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据资产查询DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "数据资产查询条件")
public class DataAssetQueryDTO {

    @Schema(description = "资产名称")
    private String assetName;

    @Schema(description = "资产编码")
    private String assetCode;

    @Schema(description = "资产类型")
    private String assetType;

    @Schema(description = "所属系统")
    private String systemName;

    @Schema(description = "责任部门ID")
    private Long departmentId;

    @Schema(description = "责任人ID")
    private Long ownerId;

    @Schema(description = "分类ID")
    private Long classificationId;

    @Schema(description = "分级ID")
    private Long gradingId;

    @Schema(description = "状态：DRAFT, ACTIVE, INACTIVE, ARCHIVED")
    private String status;

    @Schema(description = "是否包含敏感数据")
    private Boolean containsSensitiveData;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "当前页码")
    private Long current = 1L;

    @Schema(description = "每页大小")
    private Long size = 10L;
}
