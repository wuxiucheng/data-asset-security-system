package com.dataasset.security.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dataasset.security.dto.DataClassificationCreateDTO;
import com.dataasset.security.dto.DataClassificationQueryDTO;
import com.dataasset.security.dto.DataClassificationUpdateDTO;
import com.dataasset.security.entity.DataClassification;
import com.dataasset.security.vo.DataClassificationVO;

import java.util.List;

/**
 * 数据分类管理Service接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public interface DataClassificationService extends IService<DataClassification> {

    /**
     * 创建数据分类
     *
     * @param createDTO 创建数据分类请求
     * @return 分类ID
     */
    Long createDataClassification(DataClassificationCreateDTO createDTO);

    /**
     * 更新数据分类
     *
     * @param updateDTO 更新数据分类请求
     */
    void updateDataClassification(DataClassificationUpdateDTO updateDTO);

    /**
     * 删除数据分类
     *
     * @param classificationId 分类ID
     */
    void deleteDataClassification(Long classificationId);

    /**
     * 分页查询数据分类
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<DataClassificationVO> queryDataClassifications(DataClassificationQueryDTO queryDTO);

    /**
     * 获取数据分类详情
     *
     * @param classificationId 分类ID
     * @return 分类详情
     */
    DataClassificationVO getDataClassificationDetail(Long classificationId);

    /**
     * 获取数据分类树
     *
     * @param standardId 标准ID
     * @return 分类树
     */
    List<DataClassificationVO> getDataClassificationTree(Long standardId);

    /**
     * 更新数据分类状态
     *
     * @param classificationId 分类ID
     * @param status            状态
     */
    void updateDataClassificationStatus(Long classificationId, String status);

    /**
     * 根据标准ID查询数据分类列表
     *
     * @param standardId 标准ID
     * @return 分类列表
     */
    List<DataClassificationVO> getDataClassificationsByStandardId(Long standardId);
}
