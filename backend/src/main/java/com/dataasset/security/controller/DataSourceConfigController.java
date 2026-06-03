package com.dataasset.security.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.common.annotation.AuditLog;
import com.dataasset.security.common.enums.ObjectTypeEnum;
import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.dto.DataSourceConfigCreateDTO;
import com.dataasset.security.dto.DataSourceConfigQueryDTO;
import com.dataasset.security.service.DataSourceConfigService;
import com.dataasset.security.vo.DataSourceConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据源配置管理控制器
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@RestController
@RequestMapping("/datasource")
@RequiredArgsConstructor
@Tag(name = "数据源配置管理", description = "数据源配置的增删改查及连接测试接口")
public class DataSourceConfigController {

    private final DataSourceConfigService dataSourceConfigService;

    @PostMapping("/page")
    @Operation(summary = "分页查询数据源配置")
    @AuditLog(objectType = ObjectTypeEnum.DATA_SOURCE, operationType = OperationTypeEnum.QUERY)
    public Result<Page<DataSourceConfigVO>> queryDataSourceConfigs(@RequestBody DataSourceConfigQueryDTO queryDTO) {
        return Result.success(dataSourceConfigService.queryDataSourceConfigs(queryDTO));
    }

    @GetMapping("/list")
    @Operation(summary = "查询所有活跃数据源（下拉选择用）")
    public Result<List<DataSourceConfigVO>> listActiveDataSources() {
        return Result.success(dataSourceConfigService.listActiveDataSources());
    }

    @GetMapping("/{dataSourceId}")
    @Operation(summary = "获取数据源配置详情")
    @AuditLog(objectType = ObjectTypeEnum.DATA_SOURCE, operationType = OperationTypeEnum.QUERY)
    public Result<DataSourceConfigVO> getDataSourceConfig(@PathVariable Long dataSourceId) {
        return Result.success(dataSourceConfigService.getDataSourceConfig(dataSourceId));
    }

    @PostMapping
    @Operation(summary = "创建数据源配置")
    @AuditLog(objectType = ObjectTypeEnum.DATA_SOURCE, operationType = OperationTypeEnum.CREATE)
    public Result<Long> createDataSourceConfig(@Valid @RequestBody DataSourceConfigCreateDTO createDTO) {
        return Result.success(dataSourceConfigService.createDataSourceConfig(createDTO));
    }

    @PutMapping("/{dataSourceId}")
    @Operation(summary = "更新数据源配置")
    @AuditLog(objectType = ObjectTypeEnum.DATA_SOURCE, operationType = OperationTypeEnum.UPDATE)
    public Result<Void> updateDataSourceConfig(@PathVariable Long dataSourceId,
                                                @Valid @RequestBody DataSourceConfigCreateDTO updateDTO) {
        dataSourceConfigService.updateDataSourceConfig(dataSourceId, updateDTO);
        return Result.success();
    }

    @DeleteMapping("/{dataSourceId}")
    @Operation(summary = "删除数据源配置")
    @AuditLog(objectType = ObjectTypeEnum.DATA_SOURCE, operationType = OperationTypeEnum.DELETE)
    public Result<Void> deleteDataSourceConfig(@PathVariable Long dataSourceId) {
        dataSourceConfigService.deleteDataSourceConfig(dataSourceId);
        return Result.success();
    }

    @PostMapping("/test-connection/{dataSourceId}")
    @Operation(summary = "测试已保存数据源的连接")
    @AuditLog(objectType = ObjectTypeEnum.DATA_SOURCE, operationType = OperationTypeEnum.QUERY)
    public Result<Boolean> testConnection(@PathVariable Long dataSourceId) {
        return Result.success(dataSourceConfigService.testConnection(dataSourceId));
    }

    @PostMapping("/test-connection")
    @Operation(summary = "测试数据源连接（不保存）")
    public Result<Boolean> testConnection(@Valid @RequestBody DataSourceConfigCreateDTO dto) {
        return Result.success(dataSourceConfigService.testConnection(dto));
    }
}
