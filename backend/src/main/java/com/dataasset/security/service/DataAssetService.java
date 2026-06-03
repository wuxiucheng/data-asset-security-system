package com.dataasset.security.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dataasset.security.dto.DataAssetCreateDTO;
import com.dataasset.security.dto.DataAssetQueryDTO;
import com.dataasset.security.dto.DataAssetUpdateDTO;
import com.dataasset.security.entity.DataAsset;
import com.dataasset.security.vo.DataAssetVO;

import java.util.List;
import java.util.Map;

/**
 * 数据资产管理Service接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public interface DataAssetService extends IService<DataAsset> {

    /**
     * 创建数据资产
     *
     * @param createDTO 创建数据资产请求
     * @return 资产ID
     */
    Long createDataAsset(DataAssetCreateDTO createDTO);

    /**
     * 更新数据资产
     *
     * @param updateDTO 更新数据资产请求
     */
    void updateDataAsset(DataAssetUpdateDTO updateDTO);

    /**
     * 删除数据资产
     *
     * @param assetId 资产ID
     */
    void deleteDataAsset(Long assetId);

    /**
     * 分页查询数据资产
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<DataAssetVO> queryDataAssets(DataAssetQueryDTO queryDTO);

    /**
     * 获取数据资产详情
     *
     * @param assetId 资产ID
     * @return 资产详情
     */
    DataAssetVO getDataAssetDetail(Long assetId);

    /**
     * 更新数据资产状态
     *
     * @param assetId 资产ID
     * @param status  状态
     */
    void updateDataAssetStatus(Long assetId, String status);

    /**
     * 根据部门ID查询数据资产列表
     *
     * @param departmentId 部门ID
     * @return 资产列表
     */
    List<DataAssetVO> getDataAssetsByDepartmentId(Long departmentId);

    /**
     * 根据责任人ID查询数据资产列表
     *
     * @param ownerId 责任人ID
     * @return 资产列表
     */
    List<DataAssetVO> getDataAssetsByOwnerId(Long ownerId);

    /**
     * 根据分类ID查询数据资产列表
     *
     * @param classificationId 分类ID
     * @return 资产列表
     */
    List<DataAssetVO> getDataAssetsByClassificationId(Long classificationId);

    /**
     * 根据分级ID查询数据资产列表
     *
     * @param gradingId 分级ID
     * @return 资产列表
     */
    List<DataAssetVO> getDataAssetsByGradingId(Long gradingId);

    /**
     * 刷新资产数据条数
     *
     * @param assetId 资产ID
     * @return 刷新结果，包含assetId和rowCount
     */
    Map<String, Object> refreshAssetRowCount(Long assetId);

    /**
     * 提交批量刷新任务
     *
     * @param assetIds      资产ID列表
     * @param refreshScope  刷新范围：ASSET_ONLY 或 ASSET_AND_FIELD
     * @return 任务ID
     */
    String submitBatchRefreshTask(List<Long> assetIds, String refreshScope);

    /**
     * 查询批量刷新任务进度
     *
     * @param taskId 任务ID
     * @return 任务进度信息
     */
    Map<String, Object> getBatchRefreshProgress(String taskId);
}
