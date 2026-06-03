package com.dataasset.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dataasset.security.common.exception.BusinessException;
import com.dataasset.security.common.exception.ResourceNotFoundException;
import com.dataasset.security.common.result.ResultCode;
import com.dataasset.security.entity.SysRole;
import com.dataasset.security.entity.SysUser;
import com.dataasset.security.entity.SysUserRole;
import com.dataasset.security.mapper.SysRoleMapper;
import com.dataasset.security.mapper.SysUserRoleMapper;
import com.dataasset.security.mapper.SysUserMapper;
import com.dataasset.security.security.SeparationOfDutiesService;
import com.dataasset.security.service.UserRoleService;
import com.dataasset.security.vo.RoleVO;
import com.dataasset.security.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色管理Service实现类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SeparationOfDutiesService separationOfDutiesService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 检查用户是否存在
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new ResourceNotFoundException(ResultCode.USER_NOT_EXIST);
        }

        // 验证所有角色是否存在
        for (Long roleId : roleIds) {
            SysRole role = sysRoleMapper.selectById(roleId);
            if (role == null) {
                throw new BusinessException("角色不存在：" + roleId);
            }

            // 验证三权分立原则
            separationOfDutiesService.validateRoleAssignment(userId, roleId);
        }

        // 删除原有角色关联
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId));

        // 创建新的角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRole.setCreatedTime(LocalDateTime.now());
                sysUserRoleMapper.insert(userRole);
            }
        }

        log.info("用户角色分配成功：userId={}, roles={}", userId, roleIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRoles(Long userId, List<Long> roleIds) {
        // 检查用户是否存在
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new ResourceNotFoundException(ResultCode.USER_NOT_EXIST);
        }

        // 删除用户角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                    .eq(SysUserRole::getUserId, userId)
                    .in(SysUserRole::getRoleId, roleIds));
        }

        log.info("用户角色移除成功：userId={}, roles={}", userId, roleIds);
    }

    @Override
    public List<RoleVO> getUserRoles(Long userId) {
        // 检查用户是否存在
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new ResourceNotFoundException(ResultCode.USER_NOT_EXIST);
        }

        // 查询用户角色
        List<SysRole> roles = sysRoleMapper.selectRolesByUserId(userId);
        return roles.stream()
                .map(role -> {
                    RoleVO vo = new RoleVO();
                    vo.setRoleId(role.getRoleId());
                    vo.setRoleCode(role.getRoleCode());
                    vo.setRoleName(role.getRoleName());
                    vo.setRoleDescription(role.getRoleDescription());
                    vo.setRoleType(role.getRoleType());
                    vo.setStatus(role.getStatus());
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<UserVO> getRoleUsers(Long roleId) {
        // 检查角色是否存在
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null) {
            throw new ResourceNotFoundException(ResultCode.ROLE_NOT_EXIST);
        }

        // 查询角色用户
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, roleId));

        return userRoles.stream()
                .map(userRole -> {
                    SysUser user = sysUserMapper.selectById(userRole.getUserId());
                    if (user == null) {
                        return null;
                    }
                    UserVO vo = new UserVO();
                    vo.setUserId(user.getUserId());
                    vo.setUsername(user.getUsername());
                    vo.setRealName(user.getRealName());
                    vo.setEmail(user.getEmail());
                    vo.setPhone(user.getPhone());
                    vo.setStatus(user.getStatus());
                    vo.setLastLoginTime(user.getLastLoginTime());
                    vo.setLastLoginIp(user.getLastLoginIp());
                    vo.setCreatedTime(user.getCreatedTime());
                    vo.setUpdatedTime(user.getUpdatedTime());
                    return vo;
                })
                .filter(vo -> vo != null)
                .collect(Collectors.toList());
    }
}
