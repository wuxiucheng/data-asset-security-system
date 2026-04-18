package com.dataasset.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.common.exception.BusinessException;
import com.dataasset.security.common.exception.ResourceNotFoundException;
import com.dataasset.security.dto.DataClassificationCreateDTO;
import com.dataasset.security.dto.DataClassificationQueryDTO;
import com.dataasset.security.dto.DataClassificationUpdateDTO;
import com.dataasset.security.entity.ClassificationStandard;
import com.dataasset.security.entity.DataClassification;
import com.dataasset.security.entity.SysUser;
import com.dataasset.security.mapper.ClassificationStandardMapper;
import com.dataasset.security.mapper.DataClassificationMapper;
import com.dataasset.security.mapper.SysUserMapper;
import com.dataasset.security.service.DataClassificationService;
import com.dataasset.security.vo.DataClassificationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据分类管理Service实现类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataClassificationServiceImpl extends ServiceImpl<DataClassificationMapper, DataClassification> implements DataClassificationService {

    private final ClassificationStandardMapper classificationStandardMapper;
    private final SysUserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDataClassification(DataClassificationCreateDTO createDTO) {
        // 检查标准是否存在
        ClassificationStandard standard = classificationStandardMapper.selectById(createDTO.getStandardId());
        if (standard == null) {
            throw new BusinessException("数据分类标准不存在");
        }

        // 检查分类编码是否已存在
        DataClassification existClassification = this.lambdaQuery()
                .eq(DataClassification::getStandardId, createDTO.getStandardId())
                .eq(DataClassification::getClassificationCode, createDTO.getClassificationCode())
                .one();
        if (existClassification != null) {
            throw new BusinessException("分类编码在该标准下已存在");
        }

        // 计算层级
        Integer level = 1;
        if (createDTO.getParentId() != null) {
            DataClassification parentClassification = this.getById(createDTO.getParentId());
            if (parentClassification == null) {
                throw new BusinessException("父分类不存在");
            }
            level = parentClassification.getLevel() + 1;
        }

        // 创建数据分类
        DataClassification classification = new DataClassification();
        classification.setStandardId(createDTO.getStandardId());
        classification.setClassificationCode(createDTO.getClassificationCode());
        classification.setClassificationName(createDTO.getClassificationName());
        classification.setClassificationDescription(createDTO.getClassificationDescription());
        classification.setParentId(createDTO.getParentId());
        classification.setLevel(level);
        classification.setSortOrder(createDTO.getSortOrder() != null ? createDTO.getSortOrder() : 0);
        classification.setStatus("ACTIVE");
        classification.setCreatedTime(LocalDateTime.now());
        classification.setUpdatedTime(LocalDateTime.now());

        // TODO: 设置创建人ID（需要从登录用户获取）
        // classification.setCreatorId(currentUser.getUserId());

        this.save(classification);
        log.info("数据分类创建成功：{}", classification.getClassificationName());

        return classification.getClassificationId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDataClassification(DataClassificationUpdateDTO updateDTO) {
        // 检查分类是否存在
        DataClassification classification = this.getById(updateDTO.getClassificationId());
        if (classification == null) {
            throw new ResourceNotFoundException("数据分类不存在");
        }

        // 更新分类信息
        if (StringUtils.hasText(updateDTO.getClassificationName())) {
            classification.setClassificationName(updateDTO.getClassificationName());
        }
        if (StringUtils.hasText(updateDTO.getClassificationDescription())) {
            classification.setClassificationDescription(updateDTO.getClassificationDescription());
        }
        if (updateDTO.getParentId() != null) {
            classification.setParentId(updateDTO.getParentId());
            // 重新计算层级
            if (updateDTO.getParentId().equals(updateDTO.getClassificationId())) {
                throw new BusinessException("不能设置自己为父分类");
            }
            DataClassification parentClassification = this.getById(updateDTO.getParentId());
            if (parentClassification != null) {
                classification.setLevel(parentClassification.getLevel() + 1);
            }
        }
        if (updateDTO.getSortOrder() != null) {
            classification.setSortOrder(updateDTO.getSortOrder());
        }
        if (StringUtils.hasText(updateDTO.getStatus())) {
            classification.setStatus(updateDTO.getStatus());
        }

        classification.setUpdatedTime(LocalDateTime.now());

        // TODO: 设置更新人ID（需要从登录用户获取）
        // classification.setUpdaterId(currentUser.getUserId());

        this.updateById(classification);
        log.info("数据分类更新成功：{}", classification.getClassificationName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDataClassification(Long classificationId) {
        // 检查分类是否存在
        DataClassification classification = this.getById(classificationId);
        if (classification == null) {
            throw new ResourceNotFoundException("数据分类不存在");
        }

        // 检查是否有子分类
        long childCount = this.lambdaQuery()
                .eq(DataClassification::getParentId, classificationId)
                .count();
        if (childCount > 0) {
            throw new BusinessException("存在子分类，请先删除子分类");
        }

        // 删除分类（逻辑删除）
        this.removeById(classificationId);
        log.info("数据分类删除成功：{}", classification.getClassificationName());
    }

    @Override
    public Page<DataClassificationVO> queryDataClassifications(DataClassificationQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<DataClassification> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getStandardId() != null) {
            wrapper.eq(DataClassification::getStandardId, queryDTO.getStandardId());
        }
        if (StringUtils.hasText(queryDTO.getClassificationCode())) {
            wrapper.like(DataClassification::getClassificationCode, queryDTO.getClassificationCode());
        }
        if (StringUtils.hasText(queryDTO.getClassificationName())) {
            wrapper.like(DataClassification::getClassificationName, queryDTO.getClassificationName());
        }
        if (queryDTO.getParentId() != null) {
            wrapper.eq(DataClassification::getParentId, queryDTO.getParentId());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(DataClassification::getStatus, queryDTO.getStatus());
        }
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(DataClassification::getCreatedTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(DataClassification::getCreatedTime, queryDTO.getEndTime());
        }

        // 按排序和创建时间排序
        wrapper.orderByAsc(DataClassification::getSortOrder)
                .orderByDesc(DataClassification::getCreatedTime);

        // 分页查询
        Page<DataClassification> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<DataClassification> classificationPage = this.page(page, wrapper);

        // 转换为VO
        Page<DataClassificationVO> voPage = new Page<>(classificationPage.getCurrent(), classificationPage.getSize(), classificationPage.getTotal());
        voPage.setRecords(classificationPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());

        return voPage;
    }

    @Override
    public DataClassificationVO getDataClassificationDetail(Long classificationId) {
        DataClassification classification = this.getById(classificationId);
        if (classification == null) {
            throw new ResourceNotFoundException("数据分类不存在");
        }

        return convertToVO(classification);
    }

    @Override
    public List<DataClassificationVO> getDataClassificationTree(Long standardId) {
        // 查询所有分类
        List<DataClassification> classifications = this.lambdaQuery()
                .eq(DataClassification::getStandardId, standardId)
                .eq(DataClassification::getStatus, "ACTIVE")
                .orderByAsc(DataClassification::getSortOrder)
                .list();

        // 构建分类树
        return buildClassificationTree(classifications, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDataClassificationStatus(Long classificationId, String status) {
        // 检查分类是否存在
        DataClassification classification = this.getById(classificationId);
        if (classification == null) {
            throw new ResourceNotFoundException("数据分类不存在");
        }

        // 更新分类状态
        classification.setStatus(status);
        classification.setUpdatedTime(LocalDateTime.now());
        this.updateById(classification);
        log.info("数据分类状态更新成功：{} -> {}", classification.getClassificationName(), status);
    }

    @Override
    public List<DataClassificationVO> getDataClassificationsByStandardId(Long standardId) {
        List<DataClassification> classifications = this.lambdaQuery()
                .eq(DataClassification::getStandardId, standardId)
                .eq(DataClassification::getStatus, "ACTIVE")
                .orderByAsc(DataClassification::getSortOrder)
                .list();
        return classifications.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 构建分类树
     */
    private List<DataClassificationVO> buildClassificationTree(List<DataClassification> classifications, Long parentId) {
        List<DataClassificationVO> tree = new ArrayList<>();

        for (DataClassification classification : classifications) {
            if ((parentId == null && classification.getParentId() == null) ||
                (parentId != null && parentId.equals(classification.getParentId()))) {
                DataClassificationVO vo = convertToVO(classification);
                // 递归构建子分类
                List<DataClassificationVO> children = buildClassificationTree(classifications, classification.getClassificationId());
                vo.setChildren(children);
                tree.add(vo);
            }
        }

        return tree;
    }

    /**
     * 转换为VO
     */
    private DataClassificationVO convertToVO(DataClassification classification) {
        DataClassificationVO vo = new DataClassificationVO();
        vo.setClassificationId(classification.getClassificationId());
        vo.setStandardId(classification.getStandardId());
        vo.setClassificationCode(classification.getClassificationCode());
        vo.setClassificationName(classification.getClassificationName());
        vo.setClassificationDescription(classification.getClassificationDescription());
        vo.setParentId(classification.getParentId());
        vo.setLevel(classification.getLevel());
        vo.setSortOrder(classification.getSortOrder());
        vo.setStatus(classification.getStatus());
        vo.setCreatorId(classification.getCreatorId());
        vo.setUpdaterId(classification.getUpdaterId());
        vo.setCreatedTime(classification.getCreatedTime());
        vo.setUpdatedTime(classification.getUpdatedTime());

        // 查询标准名称
        if (classification.getStandardId() != null) {
            ClassificationStandard standard = classificationStandardMapper.selectById(classification.getStandardId());
            if (standard != null) {
                vo.setStandardName(standard.getStandardName());
            }
        }

        // 查询父分类名称
        if (classification.getParentId() != null) {
            DataClassification parentClassification = this.getById(classification.getParentId());
            if (parentClassification != null) {
                vo.setParentName(parentClassification.getClassificationName());
            }
        }

        // 查询创建人姓名
        if (classification.getCreatorId() != null) {
            SysUser creator = userMapper.selectById(classification.getCreatorId());
            if (creator != null) {
                vo.setCreatorName(creator.getUserName());
            }
        }

        // 查询更新人姓名
        if (classification.getUpdaterId() != null) {
            SysUser updater = userMapper.selectById(classification.getUpdaterId());
            if (updater != null) {
                vo.setUpdaterName(updater.getUserName());
            }
        }

        return vo;
    }
}
