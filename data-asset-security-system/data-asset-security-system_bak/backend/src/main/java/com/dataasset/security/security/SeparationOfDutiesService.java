package com.dataasset.security.security;

import com.dataasset.security.common.exception.BusinessException;
import com.dataasset.security.common.result.ResultCode;
import com.dataasset.security.entity.SysRole;
import com.dataasset.security.mapper.SysRoleMapper;
import com.dataasset.security.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 三权分立验证服务
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeparationOfDutiesService {

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;

    /**
     * 验证用户是否违反三权分立原则
     *
     * @param userId    用户ID
     * @param roleTypes 角色类型集合
     * @return 是否符合三权分立原则
     */
    public boolean validateSeparationOfDuties(Long userId, Set<String> roleTypes) {
        // 审批权和执行权不能同时拥有
        if (roleTypes.contains("APPROVER") && roleTypes.contains("OWNER")) {
            log.warn("用户{}同时拥有审批权和执行权，违反三权分立原则", userId);
            return false;
        }

        // 管理权和审批权不能同时拥有
        if (roleTypes.contains("SYSTEM_ADMIN") && roleTypes.contains("APPROVER")) {
            log.warn("用户{}同时拥有管理权和审批权，违反三权分立原则", userId);
            return false;
        }

        // 管理权和执行权不能同时拥有
        if (roleTypes.contains("SYSTEM_ADMIN") && roleTypes.contains("OWNER")) {
            log.warn("用户{}同时拥有管理权和执行权，违反三权分立原则", userId);
            return false;
        }

        return true;
    }

    /**
     * 验证角色分配是否违反三权分立原则
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    public void validateRoleAssignment(Long userId, Long roleId) {
        // 查询用户当前所有角色
        List<SysRole> currentRoles = sysRoleMapper.selectRolesByUserId(userId);
        Set<String> currentRoleTypes = currentRoles.stream()
                .map(SysRole::getRoleType)
                .collect(Collectors.toSet());

        // 查询要分配的角色
        SysRole newRole = sysRoleMapper.selectById(roleId);
        if (newRole == null) {
            throw new BusinessException("角色不存在");
        }

        // 添加新角色到角色集合
        Set<String> newRoleTypes = Set.copyOf(currentRoleTypes);
        newRoleTypes.add(newRole.getRoleType());

        // 验证是否违反三权分立原则
        if (!validateSeparationOfDuties(userId, newRoleTypes)) {
            throw new BusinessException(ResultCode.SEPARATION_OF_DUTIES_VIOLATION);
        }
    }

    /**
     * 获取用户的角色类型
     *
     * @param userId 用户ID
     * @return 角色类型集合
     */
    public Set<String> getUserRoleTypes(Long userId) {
        List<SysRole> roles = sysRoleMapper.selectRolesByUserId(userId);
        return roles.stream()
                .map(SysRole::getRoleType)
                .collect(Collectors.toSet());
    }

    /**
     * 检查用户是否拥有指定角色
     *
     * @param userId   用户ID
     * @param roleType 角色类型
     * @return 是否拥有该角色
     */
    public boolean hasRole(Long userId, String roleType) {
        Set<String> roleTypes = getUserRoleTypes(userId);
        return roleTypes.contains(roleType);
    }

    /**
     * 检查用户是否拥有任一指定角色
     *
     * @param userId    用户ID
     * @param roleTypes 角色类型集合
     * @return 是否拥有任一角色
     */
    public boolean hasAnyRole(Long userId, Set<String> roleTypes) {
        Set<String> userRoleTypes = getUserRoleTypes(userId);
        return userRoleTypes.stream().anyMatch(roleTypes::contains);
    }

    /**
     * 检查用户是否拥有所有指定角色
     *
     * @param userId    用户ID
     * @param roleTypes 角色类型集合
     * @return 是否拥有所有角色
     */
    public boolean hasAllRoles(Long userId, Set<String> roleTypes) {
        Set<String> userRoleTypes = getUserRoleTypes(userId);
        return userRoleTypes.containsAll(roleTypes);
    }
}
