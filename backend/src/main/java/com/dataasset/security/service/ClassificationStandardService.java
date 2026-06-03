package com.dataasset.security.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dataasset.security.dto.ClassificationStandardCreateDTO;
import com.dataasset.security.dto.ClassificationStandardQueryDTO;
import com.dataasset.security.dto.ClassificationStandardUpdateDTO;
import com.dataasset.security.entity.ClassificationStandard;
import com.dataasset.security.vo.ClassificationStandardVO;

import java.util.List;

/**
 * 数据分类标准管理Service接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public interface ClassificationStandardService extends IService<ClassificationStandard> {

    /**
     * 创建数据分类标准
     *
     * @param createDTO 创建数据分类标准请求
     * @return 标准ID
     */
    Long createClassificationStandard(ClassificationStandardCreateDTO createDTO);

    /**
     * 更新数据分类标准
     *
     * @param updateDTO 更新数据分类标准请求
     */
    void updateClassificationStandard(ClassificationStandardUpdateDTO updateDTO);

    /**
     * 删除数据分类标准
     *
     * @param standardId 标准ID
     */
    void deleteClassificationStandard(Long standardId);

    /**
     * 分页查询数据分类标准
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<ClassificationStandardVO> queryClassificationStandards(ClassificationStandardQueryDTO queryDTO);

    /**
     * 获取数据分类标准详情
     *
     * @param standardId 标准ID
     * @return 标准详情
     */
    ClassificationStandardVO getClassificationStandardDetail(Long standardId);

    /**
     * 发布数据分类标准
     *
     * @param standardId 标准ID
     */
    void publishClassificationStandard(Long standardId);

    /**
     * 归档数据分类标准
     *
     * @param standardId 标准ID
     */
    void archiveClassificationStandard(Long standardId);

    /**
     * 获取所有发布的数据分类标准
     *
     * @return 标准列表
     */
    List<ClassificationStandardVO> getPublishedClassificationStandards();
}
