package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新责任人请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "更新责任人请求")
public class OwnerUpdateDTO {

    @Schema(description = "责任人ID", required = true)
    @NotNull(message = "责任人ID不能为空")
    private Long ownerId;

    @Schema(description = "姓名", example = "张三")
    @Size(max = 64, message = "姓名长度不能超过64")
    private String name;

    @Schema(description = "所属部门ID")
    private Long departmentId;

    @Schema(description = "职务", example = "技术经理")
    @Size(max = 64, message = "职务长度不能超过64")
    private String position;

    @Schema(description = "联系电话", example = "13800138000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String contactPhone;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    @Email(message = "邮箱格式不正确")
    @Size(max = 128, message = "邮箱长度不能超过128")
    private String email;

    @Schema(description = "用户账号", example = "zhangsan")
    @Size(max = 64, message = "用户账号长度不能超过64")
    private String userAccount;

    @Schema(description = "状态：ACTIVE, INACTIVE")
    @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "状态只能是ACTIVE或INACTIVE")
    private String status;
}
