package com.dataasset.security.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.common.annotation.AuditLog;
import com.dataasset.security.common.enums.ObjectTypeEnum;
import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.dto.OwnerCreateDTO;
import com.dataasset.security.dto.OwnerQueryDTO;
import com.dataasset.security.dto.OwnerUpdateDTO;
import com.dataasset.security.service.OwnerService;
import com.dataasset.security.vo.OwnerVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 责任人管理控制器
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@RestController
@RequestMapping("/owner")
@RequiredArgsConstructor
@Tag(name = "责任人管理", description = "责任人CRUD相关接口")
public class OwnerController {

    private final OwnerService ownerService;

    /**
     * 创建责任人
     */
    @PostMapping
    @Operation(summary = "创建责任人", description = "创建新责任人")
    @AuditLog(operationType = OperationTypeEnum.CREATE, objectType = ObjectTypeEnum.OWNER, description = "创建责任人")
    public Result<Long> createOwner(@Valid @RequestBody OwnerCreateDTO createDTO) {
        Long ownerId = ownerService.createOwner(createDTO);
        return Result.success("责任人创建成功", ownerId);
    }

    /**
     * 更新责任人
     */
    @PutMapping
    @Operation(summary = "更新责任人", description = "更新责任人信息")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.OWNER, description = "更新责任人信息")
    public Result<Void> updateOwner(@Valid @RequestBody OwnerUpdateDTO updateDTO) {
        ownerService.updateOwner(updateDTO);
        return Result.success("责任人更新成功");
    }

    /**
     * 删除责任人
     */
    @DeleteMapping("/{ownerId}")
    @Operation(summary = "删除责任人", description = "删除指定责任人")
    @AuditLog(operationType = OperationTypeEnum.DELETE, objectType = ObjectTypeEnum.OWNER, description = "删除责任人")
    public Result<Void> deleteOwner(@PathVariable Long ownerId) {
        ownerService.deleteOwner(ownerId);
        return Result.success("责任人删除成功");
    }

    /**
     * 分页查询责任人
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询责任人", description = "根据条件分页查询责任人")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.OWNER, description = "查询责任人列表")
    public Result<Page<OwnerVO>> queryOwners(@Valid @RequestBody OwnerQueryDTO queryDTO) {
        Page<OwnerVO> page = ownerService.queryOwners(queryDTO);
        return Result.success(page);
    }

    /**
     * 获取责任人详情
     */
    @GetMapping("/{ownerId}")
    @Operation(summary = "获取责任人详情", description = "根据责任人ID获取责任人详细信息")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.OWNER, description = "查询责任人详情")
    public Result<OwnerVO> getOwnerDetail(@PathVariable Long ownerId) {
        OwnerVO ownerVO = ownerService.getOwnerDetail(ownerId);
        return Result.success(ownerVO);
    }

    /**
     * 更新责任人状态
     */
    @PutMapping("/{ownerId}/status")
    @Operation(summary = "更新责任人状态", description = "更新责任人状态（启用/禁用）")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.OWNER, description = "更新责任人状态")
    public Result<Void> updateOwnerStatus(@PathVariable Long ownerId, @RequestParam String status) {
        ownerService.updateOwnerStatus(ownerId, status);
        return Result.success("责任人状态更新成功");
    }

    /**
     * 根据部门ID查询责任人列表
     */
    @GetMapping("/department/{departmentId}")
    @Operation(summary = "根据部门ID查询责任人列表", description = "查询指定部门的所有责任人")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.OWNER, description = "查询部门责任人")
    public Result<List<OwnerVO>> getOwnersByDepartmentId(@PathVariable Long departmentId) {
        List<OwnerVO> owners = ownerService.getOwnersByDepartmentId(departmentId);
        return Result.success(owners);
    }

    /**
     * 获取所有责任人
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有责任人", description = "获取所有启用的责任人")
    public Result<List<OwnerVO>> getAllOwners() {
        List<OwnerVO> owners = ownerService.getAllOwners();
        return Result.success(owners);
    }

    // ========== 兼容前端路径的接口 ==========

    @PostMapping("/create")
    @AuditLog(operationType = OperationTypeEnum.CREATE, objectType = ObjectTypeEnum.OWNER, description = "创建责任人")
    public Result<Long> createOwnerAlias(@Valid @RequestBody OwnerCreateDTO createDTO) {
        return createOwner(createDTO);
    }

    @PutMapping("/update")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.OWNER, description = "更新责任人")
    public Result<Void> updateOwnerAlias(@Valid @RequestBody OwnerUpdateDTO updateDTO) {
        return updateOwner(updateDTO);
    }

    @DeleteMapping("/delete/{id}")
    @AuditLog(operationType = OperationTypeEnum.DELETE, objectType = ObjectTypeEnum.OWNER, description = "删除责任人")
    public Result<Void> deleteAlias(@PathVariable Long id) {
        return deleteOwner(id);
    }
}