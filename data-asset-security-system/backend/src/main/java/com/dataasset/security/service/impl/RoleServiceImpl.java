package com.dataasset.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.common.exception.BusinessException;
import com.dataasset.security.common.exception.ResourceNotFoundException;
import com.dataasset.security.common.result.ResultCode;
import com.dataasset.security.dto.RoleCreateDTO;
import com.dataasset.security.dto.RoleQueryDTO;
import com.dataasset.security.dto.RoleUpdateDTO;
import com.dataasset.security.entity.SysPermission;
import com.dataasset.security.entity.SysRole;
import com.dataasset.security.entity.SysRolePermission;
import com.dataasset.security.mapper.SysPermissionMapper;
import com.dataasset.security.mapper.SysRoleMapper;
import com.dataasset.security.mapper.SysRolePermissionMapper;
import com.dataasset.security.service.RoleService;
import com.dataasset.security.vo.PermissionVO;
import com.dataasset.security.vo.RoleVO;
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
 * 角色管理Service实现类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements RoleService {

    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final SysPermissionMapper sysPermissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(RoleCreateDTO createDTO) {
        // 检查角色编码是否已存在
        SysRole existRole = this.lambdaQuery()
                .eq(SysRole::getRoleCode, createDTO.getRoleCode())
                .one();
        if (existRole != null) {
            throw new BusinessException(ResultCode.ROLE_NOT_EXIST.getCode(), "角色编码已存在");
        }

        // 创建角色
        SysRole role = new SysRole();
        role.setRoleCode(createDTO.getRoleCode());
        role.setRoleName(createDTO.getRoleName());
        role.setRoleDescription(createDTO.getRoleDescription());
        role.setRoleType(createDTO.getRoleType());
        role.setStatus("ACTIVE");
        role.setCreatedTime(LocalDateTime.now());
        role.setUpdatedTime(LocalDateTime.now());

        this.save(role);
        log.info("角色创建成功：{}", role.getRoleName());

        // 分配权限
        if (createDTO.getPermissionIds() != null && !createDTO.getPermissionIds().isEmpty()) {
            assignPermissions(role.getRoleId(), createDTO.getPermissionIds());
        }

        return role.getRoleId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleUpdateDTO updateDTO) {
        // 检查角色是否存在
        SysRole role = this.getById(updateDTO.getRoleId());
        if (role == null) {
            throw new ResourceNotFoundException(ResultCode.ROLE_NOT_EXIST);
        }

        // 更新角色信息
        if (StringUtils.hasText(updateDTO.getRoleName())) {
            role.setRoleName(updateDTO.getRoleName());
        }
        if (StringUtils.hasText(updateDTO.getRoleDescription())) {
            role.setRoleDescription(updateDTO.getRoleDescription());
        }
        if (StringUtils.hasText(updateDTO.getRoleType())) {
            role.setRoleType(updateDTO.getRoleType());
        }
        if (StringUtils.hasText(updateDTO.getStatus())) {
            role.setStatus(updateDTO.getStatus());
        }

        role.setUpdatedTime(LocalDateTime.now());
        this.updateById(role);
        log.info("角色更新成功：{}", role.getRoleName());

        // 更新权限
        if (updateDTO.getPermissionIds() != null) {
            // 删除原有权限关联
            sysRolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>()
                    .eq(SysRolePermission::getRoleId, updateDTO.getRoleId()));
            // 重新分配权限
            assignPermissions(updateDTO.getRoleId(), updateDTO.getPermissionIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        // 检查角色是否存在
        SysRole role = this.getById(roleId);
        if (role == null) {
            throw new ResourceNotFoundException(ResultCode.ROLE_NOT_EXIST);
        }

        // 不能删除系统角色
        if ("SYSTEM_ADMIN".equals(role.getRoleType())) {
            throw new BusinessException("不能删除系统管理员角色");
        }

        // 删除角色权限关联
        sysRolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, roleId));

        // 删除角色（逻辑删除）
        this.removeById(roleId);
        log.info("角色删除成功：{}", role.getRoleName());
    }

    @Override
    public Page<RoleVO> queryRoles(RoleQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getRoleCode())) {
            wrapper.like(SysRole::getRoleCode, queryDTO.getRoleCode());
        }
        if (StringUtils.hasText(queryDTO.getRoleName())) {
            wrapper.like(SysRole::getRoleName, queryDTO.getRoleName());
        }
        if (StringUtils.hasText(queryDTO.getRoleType())) {
            wrapper.eq(SysRole::getRoleType, queryDTO.getRoleType());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(SysRole::getStatus, queryDTO.getStatus());
        }
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(SysRole::getCreatedTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(SysRole::getCreatedTime, queryDTO.getEndTime());
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc(SysRole::getCreatedTime);

        // 分页查询
        Page<SysRole> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<SysRole> rolePage = this.page(page, wrapper);

        // 转换为VO
        Page<RoleVO> voPage = new Page<>(rolePage.getCurrent(), rolePage.getSize(), rolePage.getTotal());
        voPage.setRecords(rolePage.getRecords().stream()
                .map(this::convertToVO)
                .toList());

        return voPage;
    }

    @Override
    public RoleVO getRoleDetail(Long roleId) {
        SysRole role = this.getById(roleId);
        if (role == null) {
            throw new ResourceNotFoundException(ResultCode.ROLE_NOT_EXIST);
        }

        return convertToVO(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // 检查角色是否存在
        SysRole role = this.getById(roleId);
        if (role == null) {
            throw new ResourceNotFoundException(ResultCode.ROLE_NOT_EXIST);
        }

        // 删除原有权限关联
        sysRolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, roleId));

        // 创建新的权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<SysRolePermission> rolePermissions = new ArrayList<>();
            for (Long permissionId : permissionIds) {
                SysRolePermission rolePermission = new SysRolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermission.setCreatedTime(LocalDateTime.now());
                rolePermissions.add(rolePermission);
            }
            // 批量插入角色权限关联
            for (SysRolePermission rolePermission : rolePermissions) {
                sysRolePermissionMapper.insert(rolePermission);
            }
        }

        log.info("角色权限分配成功：roleId={}, permissions={}", roleId, permissionIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removePermissions(Long roleId, List<Long> permissionIds) {
        // 检查角色是否存在
        SysRole role = this.getById(roleId);
        if (role == null) {
            throw new ResourceNotFoundException(ResultCode.ROLE_NOT_EXIST);
        }

        // 删除角色权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            sysRolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>()
                    .eq(SysRolePermission::getRoleId, roleId)
                    .in(SysRolePermission::getPermissionId, permissionIds));
        }

        log.info("角色权限移除成功：roleId={}, permissions={}", roleId, permissionIds);
    }

    @Override
    public List<PermissionVO> getRolePermissions(Long roleId) {
        // 检查角色是否存在
        SysRole role = this.getById(roleId);
        if (role == null) {
            throw new ResourceNotFoundException(ResultCode.ROLE_NOT_EXIST);
        }

        // 查询角色权限
        List<SysPermission> permissions = sysPermissionMapper.selectPermissionsByRoleId(roleId);
        return permissions.stream()
                .map(this::convertPermissionToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleVO> getAllRoles() {
        List<SysRole> roles = this.lambdaQuery()
                .eq(SysRole::getStatus, "ACTIVE")
                .orderByAsc(SysRole::getSortOrder)
                .list();
        return roles.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private RoleVO convertToVO(SysRole role) {
        RoleVO vo = new RoleVO();
        vo.setRoleId(role.getRoleId());
        vo.setRoleCode(role.getRoleCode());
        vo.setRoleName(role.getRoleName());
        vo.setRoleDescription(role.getRoleDescription());
        vo.setRoleType(role.getRoleType());
        vo.setStatus(role.getStatus());
        return vo;
    }

    /**
     * 转换权限为VO
     */
    private PermissionVO convertPermissionToVO(SysPermission permission) {
        PermissionVO vo = new PermissionVO();
        vo.setPermissionId(permission.getPermissionId());
        vo.setPermissionCode(permission.getPermissionCode());
        vo.setPermissionName(permission.getPermissionName());
        vo.setPermissionType(permission.getPermissionType());
        vo.setParentId(permission.getParentId());
        vo.setPath(permission.getPath());
        vo.setComponent(permission.getComponent());
        vo.setIcon(permission.getIcon());
        vo.setSortOrder(permission.getSortOrder());
        vo.setStatus(permission.getStatus());
        return vo;
    }
}
