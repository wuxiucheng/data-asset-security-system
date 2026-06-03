package com.dataasset.security.controller;

import com.dataasset.security.common.annotation.AuditLog;
import com.dataasset.security.common.enums.ObjectTypeEnum;
import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.service.UserRoleService;
import com.dataasset.security.vo.RoleVO;
import com.dataasset.security.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户角色管理控制器
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@RestController
@RequestMapping("/user-roles")
@RequiredArgsConstructor
@Tag(name = "用户角色管理", description = "用户角色分配相关接口")
public class UserRoleController {

    private final UserRoleService userRoleService;

    /**
     * 为用户分配角色
     */
    @PostMapping("/{userId}/assign")
    @Operation(summary = "为用户分配角色", description = "为用户分配角色")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.USER, description = "分配用户角色")
    public Result<Void> assignRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        userRoleService.assignRoles(userId, roleIds);
        return Result.success("角色分配成功");
    }

    /**
     * 移除用户角色
     */
    @PostMapping("/{userId}/remove")
    @Operation(summary = "移除用户角色", description = "移除用户的指定角色")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.USER, description = "移除用户角色")
    public Result<Void> removeRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        userRoleService.removeRoles(userId, roleIds);
        return Result.success("角色移除成功");
    }

    /**
     * 获取用户角色列表
     */
    @GetMapping("/{userId}/roles")
    @Operation(summary = "获取用户角色列表", description = "获取用户的所有角色")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.USER, description = "查询用户角色")
    public Result<List<RoleVO>> getUserRoles(@PathVariable Long userId) {
        List<RoleVO> roles = userRoleService.getUserRoles(userId);
        return Result.success(roles);
    }

    /**
     * 获取角色用户列表
     */
    @GetMapping("/role/{roleId}/users")
    @Operation(summary = "获取角色用户列表", description = "获取拥有指定角色的所有用户")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.ROLE, description = "查询角色用户")
    public Result<List<UserVO>> getRoleUsers(@PathVariable Long roleId) {
        List<UserVO> users = userRoleService.getRoleUsers(roleId);
        return Result.success(users);
    }
}
