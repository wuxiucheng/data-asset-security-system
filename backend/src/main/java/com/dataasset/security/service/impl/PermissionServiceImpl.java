package com.dataasset.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.common.exception.BusinessException;
import com.dataasset.security.common.exception.ResourceNotFoundException;
import com.dataasset.security.common.result.ResultCode;
import com.dataasset.security.dto.PermissionCreateDTO;
import com.dataasset.security.dto.PermissionTreeQueryDTO;
import com.dataasset.security.dto.PermissionUpdateDTO;
import com.dataasset.security.entity.SysPermission;
import com.dataasset.security.mapper.SysPermissionMapper;
import com.dataasset.security.security.CustomUserDetails;
import com.dataasset.security.service.PermissionService;
import com.dataasset.security.vo.PermissionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限管理Service实现类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements PermissionService {

    private final SysPermissionMapper sysPermissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPermission(PermissionCreateDTO createDTO) {
        // 检查权限编码是否已存在
        SysPermission existPermission = this.lambdaQuery()
                .eq(SysPermission::getPermissionCode, createDTO.getPermissionCode())
                .one();
        if (existPermission != null) {
            throw new BusinessException("权限编码已存在");
        }

        // 检查父权限是否存在
        if (createDTO.getParentId() != null) {
            SysPermission parentPermission = this.getById(createDTO.getParentId());
            if (parentPermission == null) {
                throw new BusinessException("父权限不存在");
            }
        }

        // 创建权限
        SysPermission permission = new SysPermission();
        permission.setPermissionCode(createDTO.getPermissionCode());
        permission.setPermissionName(createDTO.getPermissionName());
        permission.setPermissionType(createDTO.getPermissionType());
        permission.setParentId(createDTO.getParentId());
        permission.setPath(createDTO.getPath());
        permission.setComponent(createDTO.getComponent());
        permission.setIcon(createDTO.getIcon());
        permission.setSortOrder(createDTO.getSortOrder() != null ? createDTO.getSortOrder() : 0);
        permission.setStatus("ACTIVE");
        permission.setCreatedTime(LocalDateTime.now());
        permission.setUpdatedTime(LocalDateTime.now());

        this.save(permission);
        log.info("权限创建成功：{}", permission.getPermissionName());

        return permission.getPermissionId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePermission(PermissionUpdateDTO updateDTO) {
        // 检查权限是否存在
        SysPermission permission = this.getById(updateDTO.getPermissionId());
        if (permission == null) {
            throw new ResourceNotFoundException("权限不存在");
        }

        // 检查父权限是否存在
        if (updateDTO.getParentId() != null) {
            // 不能设置自己为父权限
            if (updateDTO.getParentId().equals(updateDTO.getPermissionId())) {
                throw new BusinessException("不能设置自己为父权限");
            }
            SysPermission parentPermission = this.getById(updateDTO.getParentId());
            if (parentPermission == null) {
                throw new BusinessException("父权限不存在");
            }
        }

        // 更新权限信息
        if (StringUtils.hasText(updateDTO.getPermissionName())) {
            permission.setPermissionName(updateDTO.getPermissionName());
        }
        if (StringUtils.hasText(updateDTO.getPermissionType())) {
            permission.setPermissionType(updateDTO.getPermissionType());
        }
        if (updateDTO.getParentId() != null) {
            permission.setParentId(updateDTO.getParentId());
        }
        if (StringUtils.hasText(updateDTO.getPath())) {
            permission.setPath(updateDTO.getPath());
        }
        if (StringUtils.hasText(updateDTO.getComponent())) {
            permission.setComponent(updateDTO.getComponent());
        }
        if (StringUtils.hasText(updateDTO.getIcon())) {
            permission.setIcon(updateDTO.getIcon());
        }
        if (updateDTO.getSortOrder() != null) {
            permission.setSortOrder(updateDTO.getSortOrder());
        }
        if (StringUtils.hasText(updateDTO.getStatus())) {
            permission.setStatus(updateDTO.getStatus());
        }

        permission.setUpdatedTime(LocalDateTime.now());
        this.updateById(permission);
        log.info("权限更新成功：{}", permission.getPermissionName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long permissionId) {
        // 检查权限是否存在
        SysPermission permission = this.getById(permissionId);
        if (permission == null) {
            throw new ResourceNotFoundException("权限不存在");
        }

        // 检查是否有子权限
        long childCount = this.lambdaQuery()
                .eq(SysPermission::getParentId, permissionId)
                .count();
        if (childCount > 0) {
            throw new BusinessException("存在子权限，请先删除子权限");
        }

        // 删除权限（逻辑删除）
        this.removeById(permissionId);
        log.info("权限删除成功：{}", permission.getPermissionName());
    }

    @Override
    public List<PermissionVO> getPermissionTree(PermissionTreeQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getPermissionType())) {
            wrapper.eq(SysPermission::getPermissionType, queryDTO.getPermissionType());
        }
        if (StringUtils.hasText(queryDTO.getPermissionName())) {
            wrapper.like(SysPermission::getPermissionName, queryDTO.getPermissionName());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(SysPermission::getStatus, queryDTO.getStatus());
        }

        // 按排序和创建时间排序
        wrapper.orderByAsc(SysPermission::getSortOrder)
                .orderByDesc(SysPermission::getCreatedTime);

        // 查询所有权限
        List<SysPermission> permissions = this.list(wrapper);

        // 构建权限树
        return buildPermissionTree(permissions, null);
    }

    @Override
    public List<PermissionVO> getAllPermissions() {
        List<SysPermission> permissions = this.lambdaQuery()
                .eq(SysPermission::getStatus, "ACTIVE")
                .orderByAsc(SysPermission::getSortOrder)
                .list();
        return permissions.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionVO> getCurrentUserPermissions() {
        // 获取当前用户
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) {
            return new ArrayList<>();
        }

        // 查询用户权限
        List<SysPermission> permissions = sysPermissionMapper.selectPermissionsByUserId(userDetails.getUserId());
        return permissions.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 构建权限树
     */
    private List<PermissionVO> buildPermissionTree(List<SysPermission> permissions, Long parentId) {
        List<PermissionVO> tree = new ArrayList<>();

        for (SysPermission permission : permissions) {
            if ((parentId == null && permission.getParentId() == null) ||
                (parentId != null && parentId.equals(permission.getParentId()))) {
                PermissionVO vo = convertToVO(permission);
                // 递归构建子权限
                List<PermissionVO> children = buildPermissionTree(permissions, permission.getPermissionId());
                vo.setChildren(children);
                tree.add(vo);
            }
        }

        return tree;
    }

    /**
     * 转换为VO
     */
    private PermissionVO convertToVO(SysPermission permission) {
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
