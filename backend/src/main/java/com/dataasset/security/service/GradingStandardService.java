package com.dataasset.security.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dataasset.security.entity.GradingStandard;
import com.dataasset.security.vo.GradingStandardVO;

import java.util.List;

/**
 * 数据分级标准管理Service接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public interface GradingStandardService extends IService<GradingStandard> {

    /**
     * 创建数据分级标准
     *
     * @param createDTO 创建数据分级标准请求
     * @return 标准ID
     */
    Long createGradingStandard(com.dataasset.security.dto.GradingStandardCreateDTO createDTO);

    /**
     * 更新数据分级标准
     *
     * @param updateDTO 更新数据分级标准请求
     */
    void updateGradingStandard(com.dataasset.security.dto.GradingStandardUpdateDTO updateDTO);

    /**
     * 删除数据分级标准
     *
     * @param standardId 标准ID
     */
    void deleteGradingStandard(Long standardId);

    /**
     * 分页查询数据分级标准
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<GradingStandardVO> queryGradingStandards(com.dataasset.security.dto.GradingStandardQueryDTO queryDTO);

    /**
     * 获取数据分级标准详情
     *
     * @param standardId 标准ID
     * @return 标准详情
     */
    GradingStandardVO getGradingStandardDetail(Long standardId);

    /**
     * 发布数据分级标准
     *
     * @param standardId 标准ID
     */
    void publishGradingStandard(Long standardId);

    /**
     * 归档数据分级标准
     *
     * @param standardId 标准ID
     */
    void archiveGradingStandard(Long standardId);

    /**
     * 获取所有发布的数据分级标准
     *
     * @return 标准列表
     */
    List<GradingStandardVO> getPublishedGradingStandards();
}
