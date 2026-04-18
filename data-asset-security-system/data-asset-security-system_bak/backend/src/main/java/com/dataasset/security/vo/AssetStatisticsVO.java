package com.dataasset.security.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 资产统计概览VO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "资产统计概览")
public class AssetStatisticsVO {

    @Schema(description = "总资产数")
    private Long totalAssets;

    @Schema(description = "活跃资产数")
    private Long activeAssets;

    @Schema(description = "草稿资产数")
    private Long draftAssets;

    @Schema(description = "禁用资产数")
    private Long inactiveAssets;

    @Schema(description = "归档资产数")
    private Long archivedAssets;

    @Schema(description = "包含敏感数据的资产数")
    private Long sensitiveAssets;

    @Schema(description = "分类分布统计")
    private List<Map<String, Object>> classificationDistribution;

    @Schema(description = "分级分布统计")
    private List<Map<String, Object>> gradingDistribution;

    @Schema(description = "部门分布统计")
    private List<Map<String, Object>> departmentDistribution;

    @Schema(description = "责任人分布统计")
    private List<Map<String, Object>> ownerDistribution;

    @Schema(description = "资产类型分布统计")
    private List<Map<String, Object>> assetTypeDistribution;

    @Schema(description = "系统分布统计")
    private List<Map<String, Object>> systemDistribution;

    @Schema(description = "数据量级别分布统计")
    private List<Map<String, Object>> dataVolumeDistribution;

    @Schema(description = "访问频率分布统计")
    private List<Map<String, Object>> accessFrequencyDistribution;

    @Schema(description = "数据重要性分布统计")
    private List<Map<String, Object>> importanceDistribution;
}
