package com.dataasset.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.common.exception.BusinessException;
import com.dataasset.security.common.exception.ResourceNotFoundException;
import com.dataasset.security.common.result.ResultCode;
import com.dataasset.security.dto.DepartmentCreateDTO;
import com.dataasset.security.dto.DepartmentQueryDTO;
import com.dataasset.security.dto.DepartmentUpdateDTO;
import com.dataasset.security.entity.Department;
import com.dataasset.security.entity.Owner;
import com.dataasset.security.mapper.DepartmentMapper;
import com.dataasset.security.mapper.OwnerMapper;
import com.dataasset.security.service.DepartmentService;
import com.dataasset.security.vo.DepartmentVO;
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
 * 部门管理Service实现类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    private final OwnerMapper ownerMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDepartment(DepartmentCreateDTO createDTO) {
        // 检查部门编码是否已存在
        Department existDepartment = this.lambdaQuery()
                .eq(Department::getDepartmentCode, createDTO.getDepartmentCode())
                .one();
        if (existDepartment != null) {
            throw new BusinessException("部门编码已存在");
        }

        // 检查上级部门是否存在
        if (createDTO.getParentId() != null) {
            Department parentDepartment = this.getById(createDTO.getParentId());
            if (parentDepartment == null) {
                throw new BusinessException("上级部门不存在");
            }
        }

        // 创建部门
        Department department = new Department();
        department.setDepartmentCode(createDTO.getDepartmentCode());
        department.setDepartmentName(createDTO.getDepartmentName());
        department.setLeaderId(createDTO.getLeaderId());
        department.setContactPhone(createDTO.getContactPhone());
        department.setDepartmentDescription(createDTO.getDepartmentDescription());
        department.setParentId(createDTO.getParentId());
        department.setSortOrder(createDTO.getSortOrder() != null ? createDTO.getSortOrder() : 0);
        department.setStatus("ACTIVE");
        department.setCreatedTime(LocalDateTime.now());
        department.setUpdatedTime(LocalDateTime.now());

        this.save(department);
        log.info("部门创建成功：{}", department.getDepartmentName());

        return department.getDepartmentId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDepartment(DepartmentUpdateDTO updateDTO) {
        // 检查部门是否存在
        Department department = this.getById(updateDTO.getDepartmentId());
        if (department == null) {
            throw new ResourceNotFoundException("部门不存在");
        }

        // 检查上级部门是否存在
        if (updateDTO.getParentId() != null) {
            // 不能设置自己为上级部门
            if (updateDTO.getParentId().equals(updateDTO.getDepartmentId())) {
                throw new BusinessException("不能设置自己为上级部门");
            }
            Department parentDepartment = this.getById(updateDTO.getParentId());
            if (parentDepartment == null) {
                throw new BusinessException("上级部门不存在");
            }
        }

        // 更新部门信息
        if (StringUtils.hasText(updateDTO.getDepartmentName())) {
            department.setDepartmentName(updateDTO.getDepartmentName());
        }
        if (updateDTO.getLeaderId() != null) {
            department.setLeaderId(updateDTO.getLeaderId());
        }
        if (StringUtils.hasText(updateDTO.getContactPhone())) {
            department.setContactPhone(updateDTO.getContactPhone());
        }
        if (StringUtils.hasText(updateDTO.getDepartmentDescription())) {
            department.setDepartmentDescription(updateDTO.getDepartmentDescription());
        }
        if (updateDTO.getParentId() != null) {
            department.setParentId(updateDTO.getParentId());
        }
        if (updateDTO.getSortOrder() != null) {
            department.setSortOrder(updateDTO.getSortOrder());
        }
        if (StringUtils.hasText(updateDTO.getStatus())) {
            department.setStatus(updateDTO.getStatus());
        }

        department.setUpdatedTime(LocalDateTime.now());
        this.updateById(department);
        log.info("部门更新成功：{}", department.getDepartmentName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDepartment(Long departmentId) {
        // 检查部门是否存在
        Department department = this.getById(departmentId);
        if (department == null) {
            throw new ResourceNotFoundException("部门不存在");
        }

        // 检查是否有子部门
        long childCount = this.lambdaQuery()
                .eq(Department::getParentId, departmentId)
                .count();
        if (childCount > 0) {
            throw new BusinessException("存在子部门，请先删除子部门");
        }

        // 检查是否有责任人
        long ownerCount = ownerMapper.selectCount(new LambdaQueryWrapper<Owner>()
                .eq(Owner::getDepartmentId, departmentId));
        if (ownerCount > 0) {
            throw new BusinessException("部门下存在责任人，请先转移责任人");
        }

        // 删除部门（逻辑删除）
        this.removeById(departmentId);
        log.info("部门删除成功：{}", department.getDepartmentName());
    }

    @Override
    public Page<DepartmentVO> queryDepartments(DepartmentQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getDepartmentCode())) {
            wrapper.like(Department::getDepartmentCode, queryDTO.getDepartmentCode());
        }
        if (StringUtils.hasText(queryDTO.getDepartmentName())) {
            wrapper.like(Department::getDepartmentName, queryDTO.getDepartmentName());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(Department::getStatus, queryDTO.getStatus());
        }
        if (queryDTO.getParentId() != null) {
            wrapper.eq(Department::getParentId, queryDTO.getParentId());
        }
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(Department::getCreatedTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(Department::getCreatedTime, queryDTO.getEndTime());
        }

        // 按排序和创建时间排序
        wrapper.orderByAsc(Department::getSortOrder)
                .orderByDesc(Department::getCreatedTime);

        // 分页查询
        Page<Department> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<Department> departmentPage = this.page(page, wrapper);

        // 转换为VO
        Page<DepartmentVO> voPage = new Page<>(departmentPage.getCurrent(), departmentPage.getSize(), departmentPage.getTotal());
        voPage.setRecords(departmentPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());

        return voPage;
    }

    @Override
    public DepartmentVO getDepartmentDetail(Long departmentId) {
        Department department = this.getById(departmentId);
        if (department == null) {
            throw new ResourceNotFoundException("部门不存在");
        }

        return convertToVO(department);
    }

    @Override
    public List<DepartmentVO> getDepartmentTree() {
        // 查询所有部门
        List<Department> departments = this.lambdaQuery()
                .eq(Department::getStatus, "ACTIVE")
                .orderByAsc(Department::getSortOrder)
                .list();

        // 构建部门树
        return buildDepartmentTree(departments, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignLeader(Long departmentId, Long leaderId) {
        // 检查部门是否存在
        Department department = this.getById(departmentId);
        if (department == null) {
            throw new ResourceNotFoundException("部门不存在");
        }

        // 检查责任人是否存在
        Owner owner = ownerMapper.selectById(leaderId);
        if (owner == null) {
            throw new ResourceNotFoundException("责任人不存在");
        }

        // 分配部门负责人
        department.setLeaderId(leaderId);
        department.setUpdatedTime(LocalDateTime.now());
        this.updateById(department);
        log.info("部门负责人分配成功：departmentId={}, leaderId={}", departmentId, leaderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDepartmentStatus(Long departmentId, String status) {
        // 检查部门是否存在
        Department department = this.getById(departmentId);
        if (department == null) {
            throw new ResourceNotFoundException("部门不存在");
        }

        // 更新部门状态
        department.setStatus(status);
        department.setUpdatedTime(LocalDateTime.now());
        this.updateById(department);
        log.info("部门状态更新成功：{} -> {}", department.getDepartmentName(), status);
    }

    /**
     * 构建部门树
     */
    private List<DepartmentVO> buildDepartmentTree(List<Department> departments, Long parentId) {
        List<DepartmentVO> tree = new ArrayList<>();

        for (Department department : departments) {
            if ((parentId == null && department.getParentId() == null) ||
                (parentId != null && parentId.equals(department.getParentId()))) {
                DepartmentVO vo = convertToVO(department);
                // 递归构建子部门
                List<DepartmentVO> children = buildDepartmentTree(departments, department.getDepartmentId());
                vo.setChildren(children);
                tree.add(vo);
            }
        }

        return tree;
    }

    /**
     * 转换为VO
     */
    private DepartmentVO convertToVO(Department department) {
        DepartmentVO vo = new DepartmentVO();
        vo.setDepartmentId(department.getDepartmentId());
        vo.setDepartmentCode(department.getDepartmentCode());
        vo.setDepartmentName(department.getDepartmentName());
        vo.setLeaderId(department.getLeaderId());
        vo.setContactPhone(department.getContactPhone());
        vo.setDepartmentDescription(department.getDepartmentDescription());
        vo.setParentId(department.getParentId());
        vo.setSortOrder(department.getSortOrder());
        vo.setStatus(department.getStatus());
        vo.setCreatedTime(department.getCreatedTime());
        vo.setUpdatedTime(department.getUpdatedTime());

        // 查询负责人姓名
        if (department.getLeaderId() != null) {
            Owner leader = ownerMapper.selectById(department.getLeaderId());
            if (leader != null) {
                vo.setLeaderName(leader.getName());
            }
        }

        // 查询上级部门名称
        if (department.getParentId() != null) {
            Department parent = this.getById(department.getParentId());
            if (parent != null) {
                vo.setParentName(parent.getDepartmentName());
            }
        }

        return vo;
    }
}
