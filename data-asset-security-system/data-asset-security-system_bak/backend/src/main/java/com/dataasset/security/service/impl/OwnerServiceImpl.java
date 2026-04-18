package com.dataasset.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.common.exception.BusinessException;
import com.dataasset.security.common.exception.ResourceNotFoundException;
import com.dataasset.security.common.result.ResultCode;
import com.dataasset.security.dto.OwnerCreateDTO;
import com.dataasset.security.dto.OwnerQueryDTO;
import com.dataasset.security.dto.OwnerUpdateDTO;
import com.dataasset.security.entity.Department;
import com.dataasset.security.entity.Owner;
import com.dataasset.security.mapper.DepartmentMapper;
import com.dataasset.security.mapper.OwnerMapper;
import com.dataasset.security.service.OwnerService;
import com.dataasset.security.vo.OwnerVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 责任人管理Service实现类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OwnerServiceImpl extends ServiceImpl<OwnerMapper, Owner> implements OwnerService {

    private final DepartmentMapper departmentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOwner(OwnerCreateDTO createDTO) {
        // 检查工号是否已存在
        Owner existOwner = this.lambdaQuery()
                .eq(Owner::getEmployeeNo, createDTO.getEmployeeNo())
                .one();
        if (existOwner != null) {
            throw new BusinessException("工号已存在");
        }

        // 检查部门是否存在
        if (createDTO.getDepartmentId() != null) {
            Department department = departmentMapper.selectById(createDTO.getDepartmentId());
            if (department == null) {
                throw new BusinessException("部门不存在");
            }
        }

        // 创建责任人
        Owner owner = new Owner();
        owner.setEmployeeNo(createDTO.getEmployeeNo());
        owner.setName(createDTO.getName());
        owner.setDepartmentId(createDTO.getDepartmentId());
        owner.setPosition(createDTO.getPosition());
        owner.setContactPhone(createDTO.getContactPhone());
        owner.setEmail(createDTO.getEmail());
        owner.setUserAccount(createDTO.getUserAccount());
        owner.setStatus("ACTIVE");
        owner.setCreatedTime(LocalDateTime.now());
        owner.setUpdatedTime(LocalDateTime.now());

        this.save(owner);
        log.info("责任人创建成功：{}", owner.getName());

        return owner.getOwnerId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOwner(OwnerUpdateDTO updateDTO) {
        // 检查责任人是否存在
        Owner owner = this.getById(updateDTO.getOwnerId());
        if (owner == null) {
            throw new ResourceNotFoundException("责任人不存在");
        }

        // 检查部门是否存在
        if (updateDTO.getDepartmentId() != null) {
            Department department = departmentMapper.selectById(updateDTO.getDepartmentId());
            if (department == null) {
                throw new BusinessException("部门不存在");
            }
        }

        // 更新责任人信息
        if (StringUtils.hasText(updateDTO.getName())) {
            owner.setName(updateDTO.getName());
        }
        if (updateDTO.getDepartmentId() != null) {
            owner.setDepartmentId(updateDTO.getDepartmentId());
        }
        if (StringUtils.hasText(updateDTO.getPosition())) {
            owner.setPosition(updateDTO.getPosition());
        }
        if (StringUtils.hasText(updateDTO.getContactPhone())) {
            owner.setContactPhone(updateDTO.getContactPhone());
        }
        if (StringUtils.hasText(updateDTO.getEmail())) {
            owner.setEmail(updateDTO.getEmail());
        }
        if (StringUtils.hasText(updateDTO.getUserAccount())) {
            owner.setUserAccount(updateDTO.getUserAccount());
        }
        if (StringUtils.hasText(updateDTO.getStatus())) {
            owner.setStatus(updateDTO.getStatus());
        }

        owner.setUpdatedTime(LocalDateTime.now());
        this.updateById(owner);
        log.info("责任人更新成功：{}", owner.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOwner(Long ownerId) {
        // 检查责任人是否存在
        Owner owner = this.getById(ownerId);
        if (owner == null) {
            throw new ResourceNotFoundException("责任人不存在");
        }

        // 删除责任人（逻辑删除）
        this.removeById(ownerId);
        log.info("责任人删除成功：{}", owner.getName());
    }

    @Override
    public Page<OwnerVO> queryOwners(OwnerQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<Owner> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getEmployeeNo())) {
            wrapper.like(Owner::getEmployeeNo, queryDTO.getEmployeeNo());
        }
        if (StringUtils.hasText(queryDTO.getName())) {
            wrapper.like(Owner::getName, queryDTO.getName());
        }
        if (queryDTO.getDepartmentId() != null) {
            wrapper.eq(Owner::getDepartmentId, queryDTO.getDepartmentId());
        }
        if (StringUtils.hasText(queryDTO.getPosition())) {
            wrapper.like(Owner::getPosition, queryDTO.getPosition());
        }
        if (StringUtils.hasText(queryDTO.getUserAccount())) {
            wrapper.like(Owner::getUserAccount, queryDTO.getUserAccount());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(Owner::getStatus, queryDTO.getStatus());
        }
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(Owner::getCreatedTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(Owner::getCreatedTime, queryDTO.getEndTime());
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc(Owner::getCreatedTime);

        // 分页查询
        Page<Owner> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<Owner> ownerPage = this.page(page, wrapper);

        // 转换为VO
        Page<OwnerVO> voPage = new Page<>(ownerPage.getCurrent(), ownerPage.getSize(), ownerPage.getTotal());
        voPage.setRecords(ownerPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());

        return voPage;
    }

    @Override
    public OwnerVO getOwnerDetail(Long ownerId) {
        Owner owner = this.getById(ownerId);
        if (owner == null) {
            throw new ResourceNotFoundException("责任人不存在");
        }

        return convertToVO(owner);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOwnerStatus(Long ownerId, String status) {
        // 检查责任人是否存在
        Owner owner = this.getById(ownerId);
        if (owner == null) {
            throw new ResourceNotFoundException("责任人不存在");
        }

        // 更新责任人状态
        owner.setStatus(status);
        owner.setUpdatedTime(LocalDateTime.now());
        this.updateById(owner);
        log.info("责任人状态更新成功：{} -> {}", owner.getName(), status);
    }

    @Override
    public List<OwnerVO> getOwnersByDepartmentId(Long departmentId) {
        // 检查部门是否存在
        Department department = departmentMapper.selectById(departmentId);
        if (department == null) {
            throw new ResourceNotFoundException("部门不存在");
        }

        // 查询部门责任人
        List<Owner> owners = this.lambdaQuery()
                .eq(Owner::getDepartmentId, departmentId)
                .eq(Owner::getStatus, "ACTIVE")
                .orderByAsc(Owner::getName)
                .list();

        return owners.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OwnerVO> getAllOwners() {
        List<Owner> owners = this.lambdaQuery()
                .eq(Owner::getStatus, "ACTIVE")
                .orderByAsc(Owner::getName)
                .list();
        return owners.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private OwnerVO convertToVO(Owner owner) {
        OwnerVO vo = new OwnerVO();
        vo.setOwnerId(owner.getOwnerId());
        vo.setEmployeeNo(owner.getEmployeeNo());
        vo.setName(owner.getName());
        vo.setDepartmentId(owner.getDepartmentId());
        vo.setPosition(owner.getPosition());
        vo.setContactPhone(owner.getContactPhone());
        vo.setEmail(owner.getEmail());
        vo.setUserAccount(owner.getUserAccount());
        vo.setStatus(owner.getStatus());
        vo.setCreatedTime(owner.getCreatedTime());
        vo.setUpdatedTime(owner.getUpdatedTime());

        // 查询部门名称
        if (owner.getDepartmentId() != null) {
            Department department = departmentMapper.selectById(owner.getDepartmentId());
            if (department != null) {
                vo.setDepartmentName(department.getDepartmentName());
            }
        }

        return vo;
    }
}
