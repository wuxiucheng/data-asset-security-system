package com.dataasset.security.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.common.annotation.AuditLog;
import com.dataasset.security.common.enums.ObjectTypeEnum;
import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.dto.RoleCreateDTO;
import com.dataasset.security.dto.RoleQueryDTO;
import com.dataasset.security.dto.RoleUpdateDTO;
import com.dataasset.security.service.RoleService;
import com.dataasset.security.vo.PermissionVO;
import com.dataasset.security.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色CRUD相关接口")
public class RoleController {

    private final RoleService roleService;

    /**
     * 创建角色
     */
    @PostMapping
    @Operation(summary = "创建角色", description = "创建新角色")
    @AuditLog(operationType = OperationTypeEnum.CREATE, objectType = ObjectTypeEnum.ROLE, description = "创建角色")
    public Result<Long> createRole(@Valid @RequestBody RoleCreateDTO createDTO) {
        Long roleId = roleService.createRole(createDTO);
        return Result.success("角色创建成功", roleId);
    }

    /**
     * 更新角色
     */
    @PutMapping
    @Operation(summary = "更新角色", description = "更新角色信息")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.ROLE, description = "更新角色信息")
    public Result<Void> updateRole(@Valid @RequestBody RoleUpdateDTO updateDTO) {
        roleService.updateRole(updateDTO);
        return Result.success("角色更新成功");
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{roleId}")
    @Operation(summary = "删除角色", description = "删除指定角色")
    @AuditLog(operationType = OperationTypeEnum.DELETE, objectType = ObjectTypeEnum.ROLE, description = "删除角色")
    public Result<Void> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return Result.success("角色删除成功");
    }

    /**
     * 分页查询角色
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询角色", description = "根据条件分页查询角色")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.ROLE, description = "查询角色列表")
    public Result<Page<RoleVO>> queryRoles(@Valid @RequestBody RoleQueryDTO queryDTO) {
        Page<RoleVO> page = roleService.queryRoles(queryDTO);
        return Result.success(page);
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{roleId}")
    @Operation(summary = "获取角色详情", description = "根据角色ID获取角色详细信息")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.ROLE, description = "查询角色详情")
    public Result<RoleVO> getRoleDetail(@PathVariable Long roleId) {
        RoleVO roleVO = roleService.getRoleDetail(roleId);
        return Result.success(roleVO);
    }

    /**
     * 分配权限给角色
     */
    @PostMapping("/{roleId}/permissions")
    @Operation(summary = "分配权限给角色", description = "为角色分配权限")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.ROLE, description = "分配角色权限")
    public Result<Void> assignPermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds);
        return Result.success("权限分配成功");
    }

    /**
     * 移除角色权限
     */
    @DeleteMapping("/{roleId}/permissions")
    @Operation(summary = "移除角色权限", description = "移除角色的指定权限")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.ROLE, description = "移除角色权限")
    public Result<Void> removePermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        roleService.removePermissions(roleId, permissionIds);
        return Result.success("权限移除成功");
    }

    /**
     * 获取角色权限列表
     */
    @GetMapping("/{roleId}/permissions")
    @Operation(summary = "获取角色权限列表", description = "获取角色的所有权限")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.ROLE, description = "查询角色权限")
    public Result<List<PermissionVO>> getRolePermissions(@PathVariable Long roleId) {
        List<PermissionVO> permissions = roleService.getRolePermissions(roleId);
        return Result.success(permissions);
    }

    /**
     * 获取所有角色
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有角色", description = "获取所有启用的角色")
    public Result<List<RoleVO>> getAllRoles() {
        List<RoleVO> roles = roleService.getAllRoles();
        return Result.success(roles);
    }
}
