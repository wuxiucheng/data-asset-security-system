package com.dataasset.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dataasset.security.dto.PermissionCreateDTO;
import com.dataasset.security.dto.PermissionTreeQueryDTO;
import com.dataasset.security.dto.PermissionUpdateDTO;
import com.dataasset.security.entity.SysPermission;
import com.dataasset.security.vo.PermissionVO;

import java.util.List;

/**
 * 权限管理Service接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public interface PermissionService extends IService<SysPermission> {

    /**
     * 创建权限
     *
     * @param createDTO 创建权限请求
     * @return 权限ID
     */
    Long createPermission(PermissionCreateDTO createDTO);

    /**
     * 更新权限
     *
     * @param updateDTO 更新权限请求
     */
    void updatePermission(PermissionUpdateDTO updateDTO);

    /**
     * 删除权限
     *
     * @param permissionId 权限ID
     */
    void deletePermission(Long permissionId);

    /**
     * 获取权限树
     *
     * @param queryDTO 查询条件
     * @return 权限树
     */
    List<PermissionVO> getPermissionTree(PermissionTreeQueryDTO queryDTO);

    /**
     * 获取所有权限
     *
     * @return 权限列表
     */
    List<PermissionVO> getAllPermissions();

    /**
     * 获取当前用户权限
     *
     * @return 权限列表
     */
    List<PermissionVO> getCurrentUserPermissions();
}
