package com.dataasset.security.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dataasset.security.dto.RoleCreateDTO;
import com.dataasset.security.dto.RoleQueryDTO;
import com.dataasset.security.dto.RoleUpdateDTO;
import com.dataasset.security.entity.SysRole;
import com.dataasset.security.vo.PermissionVO;
import com.dataasset.security.vo.RoleVO;

import java.util.List;

/**
 * 角色管理Service接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public interface RoleService extends IService<SysRole> {

    /**
     * 创建角色
     *
     * @param createDTO 创建角色请求
     * @return 角色ID
     */
    Long createRole(RoleCreateDTO createDTO);

    /**
     * 更新角色
     *
     * @param updateDTO 更新角色请求
     */
    void updateRole(RoleUpdateDTO updateDTO);

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     */
    void deleteRole(Long roleId);

    /**
     * 分页查询角色
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<RoleVO> queryRoles(RoleQueryDTO queryDTO);

    /**
     * 获取角色详情
     *
     * @param roleId 角色ID
     * @return 角色详情
     */
    RoleVO getRoleDetail(Long roleId);

    /**
     * 分配权限给角色
     *
     * @param roleId       角色ID
     * @param permissionIds 权限ID列表
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 移除角色权限
     *
     * @param roleId       角色ID
     * @param permissionIds 权限ID列表
     */
    void removePermissions(Long roleId, List<Long> permissionIds);

    /**
     * 获取角色权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<PermissionVO> getRolePermissions(Long roleId);

    /**
     * 获取所有角色
     *
     * @return 角色列表
     */
    List<RoleVO> getAllRoles();
}
