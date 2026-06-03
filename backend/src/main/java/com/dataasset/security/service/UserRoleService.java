package com.dataasset.security.service;

import com.dataasset.security.security.SeparationOfDutiesService;
import com.dataasset.security.vo.RoleVO;
import com.dataasset.security.vo.UserVO;

import java.util.List;

/**
 * 用户角色管理Service接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public interface UserRoleService {

    /**
     * 为用户分配角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 移除用户角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    void removeRoles(Long userId, List<Long> roleIds);

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<RoleVO> getUserRoles(Long userId);

    /**
     * 获取角色用户列表
     *
     * @param roleId 角色ID
     * @return 用户列表
     */
    List<UserVO> getRoleUsers(Long roleId);
}
