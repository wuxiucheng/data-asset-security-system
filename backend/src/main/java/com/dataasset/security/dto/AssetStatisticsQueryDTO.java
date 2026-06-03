package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资产统计查询DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "资产统计查询条件")
public class AssetStatisticsQueryDTO {

    @Schema(description = "部门ID")
    private Long departmentId;

    @Schema(description = "责任人ID")
    private Long ownerId;

    @Schema(description = "分类ID")
    private Long classificationId;

    @Schema(description = "分级ID")
    private Long gradingId;

    @Schema(description = "资产类型")
    private String assetType;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "统计维度：DEPARTMENT, OWNER, CLASSIFICATION, GRADING, ASSET_TYPE, SYSTEM")
    private String dimension;
}
