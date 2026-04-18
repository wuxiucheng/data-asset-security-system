package com.dataasset.security.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.common.annotation.AuditLog;
import com.dataasset.security.common.enums.ObjectTypeEnum;
import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.dto.PasswordChangeDTO;
import com.dataasset.security.dto.UserCreateDTO;
import com.dataasset.security.dto.UserQueryDTO;
import com.dataasset.security.dto.UserUpdateDTO;
import com.dataasset.security.security.CustomUserDetails;
import com.dataasset.security.service.UserService;
import com.dataasset.security.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户CRUD相关接口")
public class UserController {

    private final UserService userService;

    /**
     * 创建用户
     */
    @PostMapping
    @Operation(summary = "创建用户", description = "创建新用户")
    @AuditLog(operationType = OperationTypeEnum.CREATE, objectType = ObjectTypeEnum.USER, description = "创建用户")
    public Result<Long> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        Long userId = userService.createUser(createDTO);
        return Result.success("用户创建成功", userId);
    }

    /**
     * 更新用户
     */
    @PutMapping
    @Operation(summary = "更新用户", description = "更新用户信息")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.USER, description = "更新用户信息")
    public Result<Void> updateUser(@Valid @RequestBody UserUpdateDTO updateDTO) {
        userService.updateUser(updateDTO);
        return Result.success("用户更新成功");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户", description = "删除指定用户")
    @AuditLog(operationType = OperationTypeEnum.DELETE, objectType = ObjectTypeEnum.USER, description = "删除用户")
    public Result<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return Result.success("用户删除成功");
    }

    /**
     * 分页查询用户
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询用户", description = "根据条件分页查询用户")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.USER, description = "查询用户列表")
    public Result<Page<UserVO>> queryUsers(@Valid @RequestBody UserQueryDTO queryDTO) {
        Page<UserVO> page = userService.queryUsers(queryDTO);
        return Result.success(page);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{userId}")
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.USER, description = "查询用户详情")
    public Result<UserVO> getUserDetail(@PathVariable Long userId) {
        UserVO userVO = userService.getUserDetail(userId);
        return Result.success(userVO);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public Result<UserVO> getCurrentUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserVO userVO = userService.getUserDetail(userDetails.getUserId());
        return Result.success(userVO);
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    @Operation(summary = "修改密码", description = "用户修改自己的密码")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.USER, description = "修改密码")
    public Result<Void> changePassword(@Valid @RequestBody PasswordChangeDTO passwordChangeDTO) {
        userService.changePassword(passwordChangeDTO);
        return Result.success("密码修改成功");
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{userId}/status")
    @Operation(summary = "更新用户状态", description = "更新用户状态（启用/禁用/锁定）")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.USER, description = "更新用户状态")
    public Result<Void> updateUserStatus(@PathVariable Long userId, @RequestParam String status) {
        userService.updateUserStatus(userId, status);
        return Result.success("用户状态更新成功");
    }

    /**
     * 重置密码
     */
    @PutMapping("/{userId}/reset-password")
    @Operation(summary = "重置密码", description = "管理员重置用户密码")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.USER, description = "重置用户密码")
    public Result<Void> resetPassword(@PathVariable Long userId, @RequestParam String newPassword) {
        userService.resetPassword(userId, newPassword);
        return Result.success("密码重置成功");
    }
}
