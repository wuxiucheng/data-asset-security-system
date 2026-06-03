package com.dataasset.security.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.common.annotation.AuditLog;
import com.dataasset.security.common.enums.ObjectTypeEnum;
import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.dto.DepartmentCreateDTO;
import com.dataasset.security.dto.DepartmentQueryDTO;
import com.dataasset.security.dto.DepartmentUpdateDTO;
import com.dataasset.security.service.DepartmentService;
import com.dataasset.security.vo.DepartmentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@RestController
@RequestMapping("/department")
@RequiredArgsConstructor
@Tag(name = "部门管理", description = "部门CRUD相关接口")
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * 创建部门
     */
    @PostMapping
    @Operation(summary = "创建部门", description = "创建新部门")
    @AuditLog(operationType = OperationTypeEnum.CREATE, objectType = ObjectTypeEnum.DEPARTMENT, description = "创建部门")
    public Result<Long> createDepartment(@Valid @RequestBody DepartmentCreateDTO createDTO) {
        Long departmentId = departmentService.createDepartment(createDTO);
        return Result.success("部门创建成功", departmentId);
    }

    /**
     * 更新部门
     */
    @PutMapping
    @Operation(summary = "更新部门", description = "更新部门信息")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.DEPARTMENT, description = "更新部门信息")
    public Result<Void> updateDepartment(@Valid @RequestBody DepartmentUpdateDTO updateDTO) {
        departmentService.updateDepartment(updateDTO);
        return Result.success("部门更新成功");
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{departmentId}")
    @Operation(summary = "删除部门", description = "删除指定部门")
    @AuditLog(operationType = OperationTypeEnum.DELETE, objectType = ObjectTypeEnum.DEPARTMENT, description = "删除部门")
    public Result<Void> deleteDepartment(@PathVariable Long departmentId) {
        departmentService.deleteDepartment(departmentId);
        return Result.success("部门删除成功");
    }

    /**
     * 分页查询部门
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询部门", description = "根据条件分页查询部门")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DEPARTMENT, description = "查询部门列表")
    public Result<Page<DepartmentVO>> queryDepartments(@Valid @RequestBody DepartmentQueryDTO queryDTO) {
        Page<DepartmentVO> page = departmentService.queryDepartments(queryDTO);
        return Result.success(page);
    }

    /**
     * 获取部门详情
     */
    @GetMapping("/{departmentId}")
    @Operation(summary = "获取部门详情", description = "根据部门ID获取部门详细信息")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DEPARTMENT, description = "查询部门详情")
    public Result<DepartmentVO> getDepartmentDetail(@PathVariable Long departmentId) {
        DepartmentVO departmentVO = departmentService.getDepartmentDetail(departmentId);
        return Result.success(departmentVO);
    }

    /**
     * 获取部门树
     */
    @GetMapping("/tree")
    @Operation(summary = "获取部门树", description = "获取部门树结构")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DEPARTMENT, description = "查询部门树")
    public Result<List<DepartmentVO>> getDepartmentTree() {
        List<DepartmentVO> tree = departmentService.getDepartmentTree();
        return Result.success(tree);
    }

    /**
     * 分配部门负责人
     */
    @PutMapping("/{departmentId}/leader")
    @Operation(summary = "分配部门负责人", description = "为部门分配负责人")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.DEPARTMENT, description = "分配部门负责人")
    public Result<Void> assignLeader(@PathVariable Long departmentId, @RequestParam Long leaderId) {
        departmentService.assignLeader(departmentId, leaderId);
        return Result.success("负责人分配成功");
    }

    /**
     * 更新部门状态
     */
    @PutMapping("/{departmentId}/status")
    @Operation(summary = "更新部门状态", description = "更新部门状态（启用/禁用）")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.DEPARTMENT, description = "更新部门状态")
    public Result<Void> updateDepartmentStatus(@PathVariable Long departmentId, @RequestParam String status) {
        departmentService.updateDepartmentStatus(departmentId, status);
        return Result.success("部门状态更新成功");
    }

    // ========== 兼容前端路径的接口 ==========

    @PostMapping("/create")
    @AuditLog(operationType = OperationTypeEnum.CREATE, objectType = ObjectTypeEnum.DEPARTMENT, description = "创建部门")
    public Result<Long> createDepartmentAlias(@Valid @RequestBody DepartmentCreateDTO createDTO) {
        return createDepartment(createDTO);
    }

    @PutMapping("/update")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.DEPARTMENT, description = "更新部门")
    public Result<Void> updateDepartmentAlias(@Valid @RequestBody DepartmentUpdateDTO updateDTO) {
        return updateDepartment(updateDTO);
    }

    @DeleteMapping("/delete/{id}")
    @AuditLog(operationType = OperationTypeEnum.DELETE, objectType = ObjectTypeEnum.DEPARTMENT, description = "删除部门")
    public Result<Void> deleteAlias(@PathVariable Long id) {
        return deleteDepartment(id);
    }
}
