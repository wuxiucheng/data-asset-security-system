package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新用户请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "更新用户请求")
public class UserUpdateDTO {

    @Schema(description = "用户ID", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "真实姓名", example = "张三")
    @Size(max = 50, message = "真实姓名长度不能超过50")
    private String realName;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "状态：ACTIVE, INACTIVE, LOCKED", example = "ACTIVE")
    @Pattern(regexp = "^(ACTIVE|INACTIVE|LOCKED)$", message = "状态只能是ACTIVE、INACTIVE或LOCKED")
    private String status;
}
