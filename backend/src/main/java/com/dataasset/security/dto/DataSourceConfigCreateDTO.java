package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建数据源配置请求DTO
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@Schema(description = "创建数据源配置请求")
public class DataSourceConfigCreateDTO {

    @Schema(description = "数据源名称", required = true)
    @NotBlank(message = "数据源名称不能为空")
    @Size(max = 128, message = "数据源名称长度不能超过128")
    private String dataSourceName;

    @Schema(description = "数据库类型", required = true)
    @NotBlank(message = "数据库类型不能为空")
    @Size(max = 32, message = "数据库类型长度不能超过32")
    private String databaseType;

    @Schema(description = "数据库地址", required = true)
    @NotBlank(message = "数据库地址不能为空")
    @Size(max = 128, message = "数据库地址长度不能超过128")
    private String host;

    @Schema(description = "数据库端口", required = true)
    @NotNull(message = "数据库端口不能为空")
    private Integer port;

    @Schema(description = "数据库名称", required = true)
    @NotBlank(message = "数据库名称不能为空")
    @Size(max = 64, message = "数据库名称长度不能超过64")
    private String databaseName;

    @Schema(description = "用户名")
    @Size(max = 64, message = "用户名长度不能超过64")
    private String username;

    @Schema(description = "密码")
    @Size(max = 256, message = "密码长度不能超过256")
    private String password;

    @Schema(description = "额外连接参数")
    @Size(max = 500, message = "额外连接参数长度不能超过500")
    private String connectionParams;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过500")
    private String remarks;
}
