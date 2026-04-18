package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建责任人请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "创建责任人请求")
public class OwnerCreateDTO {

    @Schema(description = "工号", required = true, example = "EMP001")
    @NotBlank(message = "工号不能为空")
    @Size(max = 64, message = "工号长度不能超过64")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "工号只能包含大写字母和数字")
    private String employeeNo;

    @Schema(description = "姓名", required = true, example = "张三")
    @NotBlank(message = "姓名不能为空")
    @Size(max = 64, message = "姓名长度不能超过64")
    private String name;

    @Schema(description = "所属部门ID", required = true)
    @NotNull(message = "所属部门ID不能为空")
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
}
