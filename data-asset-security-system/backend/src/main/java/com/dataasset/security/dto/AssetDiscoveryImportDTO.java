package com.dataasset.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 资产发现导入请求DTO
 */
@Data
@Schema(description = "资产发现导入请求")
public class AssetDiscoveryImportDTO {

    @Schema(description = "数据库类型")
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

    @Schema(description = "要导入的表信息列表")
    @NotNull(message = "导入表列表不能为空")
    private List<TableImportItem> tables;

    @Schema(description = "所属系统名称")
    private String systemName;

    @Schema(description = "责任部门ID")
    private Long departmentId;

    @Schema(description = "责任人ID")
    private Long ownerId;

    @Schema(description = "关联数据源ID（可选，如果选择已配置数据源则传入）")
    private Long dataSourceId;

    @Schema(description = "重复处理策略：SKIP-跳过重复，OVERWRITE-覆盖更新，FORCE-强制新增（不检测重复）")
    private String duplicateStrategy;

    @Data
    @Schema(description = "单个表导入项")
    public static class TableImportItem {
        @Schema(description = "表名")
        private String tableName;

        @Schema(description = "表注释/描述")
        private String tableComment;

        @Schema(description = "是否导入")
        private boolean selected = true;
    }
}
