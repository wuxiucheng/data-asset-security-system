package com.dataasset.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.common.exception.BusinessException;
import com.dataasset.security.common.exception.ResourceNotFoundException;
import com.dataasset.security.dto.GradingStandardCreateDTO;
import com.dataasset.security.dto.GradingStandardQueryDTO;
import com.dataasset.security.dto.GradingStandardUpdateDTO;
import com.dataasset.security.entity.GradingStandard;
import com.dataasset.security.entity.SysUser;
import com.dataasset.security.mapper.GradingStandardMapper;
import com.dataasset.security.mapper.SysUserMapper;
import com.dataasset.security.service.GradingStandardService;
import com.dataasset.security.vo.GradingStandardVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据分级标准管理Service实现类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GradingStandardServiceImpl extends ServiceImpl<GradingStandardMapper, GradingStandard> implements GradingStandardService {

    private final SysUserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createGradingStandard(GradingStandardCreateDTO createDTO) {
        // 检查标准编码是否已存在
        GradingStandard existStandard = this.lambdaQuery()
                .eq(GradingStandard::getStandardCode, createDTO.getStandardCode())
                .one();
        if (existStandard != null) {
            throw new BusinessException("标准编码已存在");
        }

        // 创建数据分级标准
        GradingStandard standard = new GradingStandard();
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
        log.info("数据分级标准创建成功：{}", standard.getStandardName());

        return standard.getStandardId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGradingStandard(GradingStandardUpdateDTO updateDTO) {
        // 检查标准是否存在
        GradingStandard standard = this.getById(updateDTO.getStandardId());
        if (standard == null) {
            throw new ResourceNotFoundException("数据分级标准不存在");
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
        log.info("数据分级标准更新成功：{}", standard.getStandardName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGradingStandard(Long standardId) {
        // 检查标准是否存在
        GradingStandard standard = this.getById(standardId);
        if (standard == null) {
            throw new ResourceNotFoundException("数据分级标准不存在");
        }

        // 检查标准是否已发布，已发布的标准不能删除
        if ("PUBLISHED".equals(standard.getStatus())) {
            throw new BusinessException("已发布的标准不能删除");
        }

        // 删除标准（逻辑删除）
        this.removeById(standardId);
        log.info("数据分级标准删除成功：{}", standard.getStandardName());
    }

    @Override
    public Page<GradingStandardVO> queryGradingStandards(GradingStandardQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<GradingStandard> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getStandardCode())) {
            wrapper.like(GradingStandard::getStandardCode, queryDTO.getStandardCode());
        }
        if (StringUtils.hasText(queryDTO.getStandardName())) {
            wrapper.like(GradingStandard::getStandardName, queryDTO.getStandardName());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(GradingStandard::getStatus, queryDTO.getStatus());
        }
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(GradingStandard::getCreatedTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(GradingStandard::getCreatedTime, queryDTO.getEndTime());
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc(GradingStandard::getCreatedTime);

        // 分页查询
        Page<GradingStandard> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<GradingStandard> standardPage = this.page(page, wrapper);

        // 转换为VO
        Page<GradingStandardVO> voPage = new Page<>(standardPage.getCurrent(), standardPage.getSize(), standardPage.getTotal());
        voPage.setRecords(standardPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());

        return voPage;
    }

    @Override
    public GradingStandardVO getGradingStandardDetail(Long standardId) {
        GradingStandard standard = this.getById(standardId);
        if (standard == null) {
            throw new ResourceNotFoundException("数据分级标准不存在");
        }

        return convertToVO(standard);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishGradingStandard(Long standardId) {
        // 检查标准是否存在
        GradingStandard standard = this.getById(standardId);
        if (standard == null) {
            throw new ResourceNotFoundException("数据分级标准不存在");
        }

        // 发布标准
        standard.setStatus("PUBLISHED");
        standard.setUpdatedTime(LocalDateTime.now());
        this.updateById(standard);
        log.info("数据分级标准发布成功：{}", standard.getStandardName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void archiveGradingStandard(Long standardId) {
        // 检查标准是否存在
        GradingStandard standard = this.getById(standardId);
        if (standard == null) {
            throw new ResourceNotFoundException("数据分级标准不存在");
        }

        // 归档标准
        standard.setStatus("ARCHIVED");
        standard.setUpdatedTime(LocalDateTime.now());
        this.updateById(standard);
        log.info("数据分级标准归档成功：{}", standard.getStandardName());
    }

    @Override
    public List<GradingStandardVO> getPublishedGradingStandards() {
        List<GradingStandard> standards = this.lambdaQuery()
                .eq(GradingStandard::getStatus, "PUBLISHED")
                .orderByDesc(GradingStandard::getCreatedTime)
                .list();
        return standards.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private GradingStandardVO convertToVO(GradingStandard standard) {
        GradingStandardVO vo = new GradingStandardVO();
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
                vo.setCreatorName(creator.getUsername());
            }
        }

        // 查询更新人姓名
        if (standard.getUpdaterId() != null) {
            SysUser updater = userMapper.selectById(standard.getUpdaterId());
            if (updater != null) {
                vo.setUpdaterName(updater.getUsername());
            }
        }

        return vo;
    }
}
