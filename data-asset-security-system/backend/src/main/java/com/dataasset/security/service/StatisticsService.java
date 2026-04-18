package com.dataasset.security.service;

import com.dataasset.security.dto.AssetStatisticsQueryDTO;
import com.dataasset.security.vo.AssetStatisticsVO;

/**
 * 统计分析Service接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public interface StatisticsService {

    /**
     * 获取资产统计概览
     *
     * @param queryDTO 查询条件
     * @return 统计概览
     */
    AssetStatisticsVO getAssetStatisticsOverview(AssetStatisticsQueryDTO queryDTO);

    /**
     * 获取分类分布统计
     *
     * @param queryDTO 查询条件
     * @return 分类分布统计
     */
    java.util.List<java.util.Map<String, Object>> getClassificationDistribution(AssetStatisticsQueryDTO queryDTO);

    /**
     * 获取分级分布统计
     *
     * @param queryDTO 查询条件
     * @return 分级分布统计
     */
    java.util.List<java.util.Map<String, Object>> getGradingDistribution(AssetStatisticsQueryDTO queryDTO);

    /**
     * 获取部门分布统计
     *
     * @param queryDTO 查询条件
     * @return 部门分布统计
     */
    java.util.List<java.util.Map<String, Object>> getDepartmentDistribution(AssetStatisticsQueryDTO queryDTO);

    /**
     * 获取责任人分布统计
     *
     * @param queryDTO 查询条件
     * @return 责任人分布统计
     */
    java.util.List<java.util.Map<String, Object>> getOwnerDistribution(AssetStatisticsQueryDTO queryDTO);

    /**
     * 获取资产类型分布统计
     *
     * @param queryDTO 查询条件
     * @return 资产类型分布统计
     */
    java.util.List<java.util.Map<String, Object>> getAssetTypeDistribution(AssetStatisticsQueryDTO queryDTO);

    /**
     * 获取系统分布统计
     *
     * @param queryDTO 查询条件
     * @return 系统分布统计
     */
    java.util.List<java.util.Map<String, Object>> getSystemDistribution(AssetStatisticsQueryDTO queryDTO);
}
