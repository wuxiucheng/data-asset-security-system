package com.dataasset.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.common.exception.BusinessException;
import com.dataasset.security.common.exception.ResourceNotFoundException;
import com.dataasset.security.dto.ClassificationStandardCreateDTO;
import com.dataasset.security.dto.ClassificationStandardQueryDTO;
import com.dataasset.security.dto.ClassificationStandardUpdateDTO;
import com.dataasset.security.entity.ClassificationStandard;
import com.dataasset.security.entity.SysUser;
import com.dataasset.security.mapper.ClassificationStandardMapper;
import com.dataasset.security.mapper.SysUserMapper;
import com.dataasset.security.service.ClassificationStandardService;
import com.dataasset.security.vo.ClassificationStandardVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据分类标准管理Service实现类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassificationStandardServiceImpl extends ServiceImpl<ClassificationStandardMapper, ClassificationStandard> implements ClassificationStandardService {

    private final SysUserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createClassificationStandard(ClassificationStandardCreateDTO createDTO) {
        // 检查标准编码是否已存在
        ClassificationStandard existStandard = this.lambdaQuery()
                .eq(ClassificationStandard::getStandardCode, createDTO.getStandardCode())
                .one();
        if (existStandard != null) {
            throw new BusinessException("标准编码已存在");
        }

        // 创建数据分类标准
        ClassificationStandard standard = new ClassificationStandard();
        standard.setStandardCode(createDTO.getStandardCode());
        standard.setStandardName(createDTO.getStandardName());
        standard.setStandardDescription(createDTO.getStandardDescription());
        standard.setVersion(createDTO.getVersion() != null ? createDTO.getVersion() : "1.0");
        standard.setPublishDate(createDTO.getPublishDate());
        standard.setPublishUnit(createDTO.getPublishUnit());
        standard.setScope(createDTO.getScope());
        standard.setStatus("DRAFT");
        standard.setCreatedTime(LocalDateTime.now());
        standard.setUpdatedTime(LocalDateTime.now());

        // TODO: 设置创建人ID（需要从登录用户获取）
        // standard.setCreatorId(currentUser.getUserId());

        this.save(standard);
        log.info("数据分类标准创建成功：{}", standard.getStandardName());

        return standard.getStandardId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateClassificationStandard(ClassificationStandardUpdateDTO updateDTO) {
        // 检查标准是否存在
        ClassificationStandard standard = this.getById(updateDTO.getStandardId());
        if (standard == null) {
            throw new ResourceNotFoundException("数据分类标准不存在");
        }

        // 检查标准是否已发布，已发布的标准不能修改
        if ("PUBLISHED".equals(standard.getStatus())) {
            throw new BusinessException("已发布的标准不能修改，请创建新版本");
        }

        // 更新标准信息
        if (StringUtils.hasText(updateDTO.getStandardName())) {
            standard.setStandardName(updateDTO.getStandardName());
        }
        if (StringUtils.hasText(updateDTO.getStandardDescription())) {
            standard.setStandardDescription(updateDTO.getStandardDescription());
        }
        if (StringUtils.hasText(updateDTO.getVersion())) {
            standard.setVersion(updateDTO.getVersion());
        }
        if (StringUtils.hasText(updateDTO.getPublishDate())) {
            standard.setPublishDate(updateDTO.getPublishDate());
        }
        if (StringUtils.hasText(updateDTO.getPublishUnit())) {
            standard.setPublishUnit(updateDTO.getPublishUnit());
        }
        if (StringUtils.hasText(updateDTO.getScope())) {
            standard.setScope(updateDTO.getScope());
        }
        if (StringUtils.hasText(updateDTO.getStatus())) {
            standard.setStatus(updateDTO.getStatus());
        }

        standard.setUpdatedTime(LocalDateTime.now());

        // TODO: 设置更新人ID（需要从登录用户获取）
        // standard.setUpdaterId(currentUser.getUserId());

        this.updateById(standard);
        log.info("数据分类标准更新成功：{}", standard.getStandardName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteClassificationStandard(Long standardId) {
        // 检查标准是否存在
        ClassificationStandard standard = this.getById(standardId);
        if (standard == null) {
            throw new ResourceNotFoundException("数据分类标准不存在");
        }

        // 检查标准是否已发布，已发布的标准不能删除
        if ("PUBLISHED".equals(standard.getStatus())) {
            throw new BusinessException("已发布的标准不能删除");
        }

        // 删除标准（逻辑删除）
        this.removeById(standardId);
        log.info("数据分类标准删除成功：{}", standard.getStandardName());
    }

    @Override
    public Page<ClassificationStandardVO> queryClassificationStandards(ClassificationStandardQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<ClassificationStandard> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getStandardCode())) {
            wrapper.like(ClassificationStandard::getStandardCode, queryDTO.getStandardCode());
        }
        if (StringUtils.hasText(queryDTO.getStandardName())) {
            wrapper.like(ClassificationStandard::getStandardName, queryDTO.getStandardName());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(ClassificationStandard::getStatus, queryDTO.getStatus());
        }
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(ClassificationStandard::getCreatedTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(ClassificationStandard::getCreatedTime, queryDTO.getEndTime());
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc(ClassificationStandard::getCreatedTime);

        // 分页查询
        Page<ClassificationStandard> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<ClassificationStandard> standardPage = this.page(page, wrapper);

        // 转换为VO
        Page<ClassificationStandardVO> voPage = new Page<>(standardPage.getCurrent(), standardPage.getSize(), standardPage.getTotal());
        voPage.setRecords(standardPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());

        return voPage;
    }

    @Override
    public ClassificationStandardVO getClassificationStandardDetail(Long standardId) {
        ClassificationStandard standard = this.getById(standardId);
        if (standard == null) {
            throw new ResourceNotFoundException("数据分类标准不存在");
        }

        return convertToVO(standard);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishClassificationStandard(Long standardId) {
        // 检查标准是否存在
        ClassificationStandard standard = this.getById(standardId);
        if (standard == null) {
            throw new ResourceNotFoundException("数据分类标准不存在");
        }

        // 发布标准
        standard.setStatus("PUBLISHED");
        standard.setUpdatedTime(LocalDateTime.now());
        this.updateById(standard);
        log.info("数据分类标准发布成功：{}", standard.getStandardName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void archiveClassificationStandard(Long standardId) {
        // 检查标准是否存在
        ClassificationStandard standard = this.getById(standardId);
        if (standard == null) {
            throw new ResourceNotFoundException("数据分类标准不存在");
        }

        // 归档标准
        standard.setStatus("ARCHIVED");
        standard.setUpdatedTime(LocalDateTime.now());
        this.updateById(standard);
        log.info("数据分类标准归档成功：{}", standard.getStandardName());
    }

    @Override
    public List<ClassificationStandardVO> getPublishedClassificationStandards() {
        List<ClassificationStandard> standards = this.lambdaQuery()
                .eq(ClassificationStandard::getStatus, "PUBLISHED")
                .orderByDesc(ClassificationStandard::getCreatedTime)
                .list();
        return standards.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private ClassificationStandardVO convertToVO(ClassificationStandard standard) {
        ClassificationStandardVO vo = new ClassificationStandardVO();
        vo.setStandardId(standard.getStandardId());
        vo.setStandardCode(standard.getStandardCode());
        vo.setStandardName(standard.getStandardName());
        vo.setStandardDescription(standard.getStandardDescription());
        vo.setVersion(standard.getVersion());
        vo.setPublishDate(standard.getPublishDate());
        vo.setPublishUnit(standard.getPublishUnit());
        vo.setScope(standard.getScope());
        vo.setStatus(standard.getStatus());
        vo.setCreatorId(standard.getCreatorId());
        vo.setUpdaterId(standard.getUpdaterId());
        vo.setCreatedTime(standard.getCreatedTime());
        vo.setUpdatedTime(standard.getUpdatedTime());

        // 查询创建人姓名
        if (standard.getCreatorId() != null) {
            SysUser creator = userMapper.selectById(standard.getCreatorId());
            if (creator != null) {
                vo.setCreatorName(creator.getRealName());
            }
        }

        // 查询更新人姓名
        if (standard.getUpdaterId() != null) {
            SysUser updater = userMapper.selectById(standard.getUpdaterId());
            if (updater != null) {
                vo.setUpdaterName(updater.getRealName());
            }
        }

        return vo;
    }
}
