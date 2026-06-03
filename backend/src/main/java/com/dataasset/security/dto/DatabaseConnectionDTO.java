package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 数据库连接请求DTO
 */
@Data
@Schema(description = "数据库连接请求")
public class DatabaseConnectionDTO {

    @Schema(description = "数据库类型：MYSQL, POSTGRESQL")
    private String databaseType;

    @Schema(description = "数据库地址")
    private String host;

    @Schema(description = "数据库端口")
    private Integer port;

    @Schema(description = "数据库名称")
    private String databaseName;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "关联数据源ID（可选，传入时优先从数据源配置获取连接信息）")
    private Long dataSourceId;
}
