package com.dataasset.security.controller;

import com.dataasset.security.common.annotation.AuditLog;
import com.dataasset.security.common.enums.ObjectTypeEnum;
import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.dto.PermissionCreateDTO;
import com.dataasset.security.dto.PermissionTreeQueryDTO;
import com.dataasset.security.dto.PermissionUpdateDTO;
import com.dataasset.security.service.PermissionService;
import com.dataasset.security.vo.PermissionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理控制器
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@Tag(name = "权限管理", description = "权限CRUD相关接口")
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * 创建权限
     */
    @PostMapping
    @Operation(summary = "创建权限", description = "创建新权限")
    @AuditLog(operationType = OperationTypeEnum.CREATE, objectType = ObjectTypeEnum.PERMISSION, description = "创建权限")
    public Result<Long> createPermission(@Valid @RequestBody PermissionCreateDTO createDTO) {
        Long permissionId = permissionService.createPermission(createDTO);
        return Result.success("权限创建成功", permissionId);
    }

    /**
     * 更新权限
     */
    @PutMapping
    @Operation(summary = "更新权限", description = "更新权限信息")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.PERMISSION, description = "更新权限信息")
    public Result<Void> updatePermission(@Valid @RequestBody PermissionUpdateDTO updateDTO) {
        permissionService.updatePermission(updateDTO);
        return Result.success("权限更新成功");
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{permissionId}")
    @Operation(summary = "删除权限", description = "删除指定权限")
    @AuditLog(operationType = OperationTypeEnum.DELETE, objectType = ObjectTypeEnum.PERMISSION, description = "删除权限")
    public Result<Void> deletePermission(@PathVariable Long permissionId) {
        permissionService.deletePermission(permissionId);
        return Result.success("权限删除成功");
    }

    /**
     * 获取权限树
     */
    @PostMapping("/tree")
    @Operation(summary = "获取权限树", description = "获取权限树结构")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.PERMISSION, description = "查询权限树")
    public Result<List<PermissionVO>> getPermissionTree(@RequestBody PermissionTreeQueryDTO queryDTO) {
        List<PermissionVO> tree = permissionService.getPermissionTree(queryDTO);
        return Result.success(tree);
    }

    /**
     * 获取所有权限
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有权限", description = "获取所有启用的权限")
    public Result<List<PermissionVO>> getAllPermissions() {
        List<PermissionVO> permissions = permissionService.getAllPermissions();
        return Result.success(permissions);
    }

    /**
     * 获取当前用户权限
     */
    @GetMapping("/current")
    @Operation(summary = "获取当前用户权限", description = "获取当前登录用户的权限列表")
    public Result<List<PermissionVO>> getCurrentUserPermissions() {
        List<PermissionVO> permissions = permissionService.getCurrentUserPermissions();
        return Result.success(permissions);
    }
}
