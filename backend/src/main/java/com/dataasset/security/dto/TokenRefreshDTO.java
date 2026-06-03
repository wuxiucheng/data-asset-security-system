package com.dataasset.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Token刷新请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
public class TokenRefreshDTO {

    /**
     * 刷新令牌
     */
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
}