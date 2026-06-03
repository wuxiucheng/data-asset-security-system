package com.dataasset.security.service.impl;

import com.dataasset.security.dto.AssetDiscoveryImportDTO;
import com.dataasset.security.dto.DataAssetCreateDTO;
import com.dataasset.security.dto.DatabaseConnectionDTO;
import com.dataasset.security.entity.DataAsset;
import com.dataasset.security.entity.DataField;
import com.dataasset.security.entity.DataSourceConfig;
import com.dataasset.security.mapper.DataAssetMapper;
import com.dataasset.security.mapper.DataFieldMapper;
import com.dataasset.security.mapper.DataSourceConfigMapper;
import com.dataasset.security.service.AssetDiscoveryService;
import com.dataasset.security.vo.DiscoveredTableVO;
import com.dataasset.security.vo.ImportDuplicateCheckVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 资产发现服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetDiscoveryServiceImpl implements AssetDiscoveryService {

    private final DataAssetMapper dataAssetMapper;
    private final DataFieldMapper dataFieldMapper;
    private final DataSourceConfigMapper dataSourceConfigMapper;

    /**
     * 如果DTO中指定了dataSourceId，从数据源配置补充连接信息（含凭证）
     */
    private void fillFromDataSource(DatabaseConnectionDTO dto) {
        if (dto.getDataSourceId() != null) {
            DataSourceConfig dsConfig = dataSourceConfigMapper.selectById(dto.getDataSourceId());
            if (dsConfig != null && "ACTIVE".equals(dsConfig.getStatus())) {
                dto.setDatabaseType(dsConfig.getDatabaseType());
                dto.setHost(dsConfig.getHost());
                dto.setPort(dsConfig.getPort());
                dto.setDatabaseName(dsConfig.getDatabaseName());
                dto.setUsername(dsConfig.getUsername());
                dto.setPassword(dsConfig.getPassword());
            }
        }
    }

    @Override
    public boolean testConnection(DatabaseConnectionDTO dto) {
        fillFromDataSource(dto);
        try (Connection conn = createConnection(dto)) {
            return conn.isValid(5);
        } catch (Exception e) {
            log.error("数据库连接测试失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public List<DiscoveredTableVO> scanTables(DatabaseConnectionDTO dto) {
        fillFromDataSource(dto);
        List<DiscoveredTableVO> tables = new ArrayList<>();
        try (Connection conn = createConnection(dto)) {
            DatabaseMetaData metaData = conn.getMetaData();
            String schema = getSchema(dto, metaData);

            try (ResultSet rs = metaData.getTables(dto.getDatabaseName(), schema, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    DiscoveredTableVO table = new DiscoveredTableVO();
                    table.setTableName(rs.getString("TABLE_NAME"));
                    table.setTableComment(rs.getString("REMARKS"));
                    table.setTableType(rs.getString("TABLE_TYPE"));
                    table.setFields(new ArrayList<>());
                    tables.add(table);
                }
            }

            // 获取每张表的行数估算
            for (DiscoveredTableVO table : tables) {
                try {
                    table.setRowCount(getRowCount(conn, dto, table.getTableName()));
                } catch (Exception e) {
                    table.setRowCount(0L);
                }
            }
        } catch (Exception e) {
            log.error("扫描数据库表失败: {}", e.getMessage());
            throw new RuntimeException("扫描数据库表失败: " + e.getMessage());
        }
        return tables;
    }

    @Override
    public DiscoveredTableVO scanTableFields(DatabaseConnectionDTO dto, String tableName) {
        fillFromDataSource(dto);
        DiscoveredTableVO table = new DiscoveredTableVO();
        table.setTableName(tableName);
        table.setFields(new ArrayList<>());

        try (Connection conn = createConnection(dto)) {
            DatabaseMetaData metaData = conn.getMetaData();
            String schema = getSchema(dto, metaData);

            // 获取表注释
            try (ResultSet tableRs = metaData.getTables(dto.getDatabaseName(), schema, tableName, new String[]{"TABLE"})) {
                if (tableRs.next()) {
                    table.setTableComment(tableRs.getString("REMARKS"));
                }
            }

            // 获取主键列
            List<String> pkColumns = new ArrayList<>();
            try (ResultSet pkRs = metaData.getPrimaryKeys(dto.getDatabaseName(), schema, tableName)) {
                while (pkRs.next()) {
                    pkColumns.add(pkRs.getString("COLUMN_NAME"));
                }
            }

            // 获取字段信息
            try (ResultSet colRs = metaData.getColumns(dto.getDatabaseName(), schema, tableName, "%")) {
                while (colRs.next()) {
                    DiscoveredTableVO.DiscoveredFieldVO field = new DiscoveredTableVO.DiscoveredFieldVO();
                    field.setFieldName(colRs.getString("COLUMN_NAME"));
                    field.setFieldType(mapFieldType(colRs.getString("TYPE_NAME")));
                    field.setFieldLength(colRs.getInt("COLUMN_SIZE"));
                    field.setNullable("YES".equalsIgnoreCase(colRs.getString("IS_NULLABLE")));
                    field.setIsPrimaryKey(pkColumns.contains(field.getFieldName()));
                    field.setFieldComment(colRs.getString("REMARKS"));
                    field.setDefaultValue(colRs.getString("COLUMN_DEF"));
                    table.getFields().add(field);
                }
            }
        } catch (Exception e) {
            log.error("扫描表字段失败: {}", e.getMessage());
            throw new RuntimeException("扫描表字段失败: " + e.getMessage());
        }
        return table;
    }

    @Override
    public ImportDuplicateCheckVO checkImportDuplicates(AssetDiscoveryImportDTO importDTO) {
        // 补充数据源配置信息
        String host = importDTO.getHost();
        Integer port = importDTO.getPort();
        String databaseName = importDTO.getDatabaseName();
        if (importDTO.getDataSourceId() != null) {
            DataSourceConfig dsConfig = dataSourceConfigMapper.selectById(importDTO.getDataSourceId());
            if (dsConfig != null && "ACTIVE".equals(dsConfig.getStatus())) {
                host = dsConfig.getHost();
                port = dsConfig.getPort();
                databaseName = dsConfig.getDatabaseName();
            }
        }

        ImportDuplicateCheckVO result = new ImportDuplicateCheckVO();
        List<ImportDuplicateCheckVO.DuplicateItem> duplicates = new ArrayList<>();
        int newCount = 0;

        for (AssetDiscoveryImportDTO.TableImportItem item : importDTO.getTables()) {
            if (!item.isSelected()) continue;

            // 查找同地址+同库名+同表名的资产
            DataAsset existing = findExistingAsset(host, port, databaseName, item.getTableName());
            if (existing != null) {
                ImportDuplicateCheckVO.DuplicateItem dup = new ImportDuplicateCheckVO.DuplicateItem();
                dup.setTableName(item.getTableName());
                dup.setTableComment(item.getTableComment());
                dup.setExistingAssetId(existing.getAssetId());
                dup.setExistingAssetName(existing.getAssetName());
                dup.setExistingAssetStatus(existing.getStatus());
                duplicates.add(dup);
            } else {
                newCount++;
            }
        }

        result.setHasDuplicate(!duplicates.isEmpty());
        result.setDuplicates(duplicates);
        result.setNewCount(newCount);
        return result;
    }

    /**
     * 查找已存在的资产：同数据库地址+端口+库名+表名
     */
    private DataAsset findExistingAsset(String host, Integer port, String databaseName, String tableName) {
        LambdaQueryWrapper<DataAsset> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataAsset::getDatabaseHost, host);
        wrapper.eq(DataAsset::getDatabasePort, port);
        wrapper.eq(DataAsset::getDatabaseName, databaseName);
        wrapper.eq(DataAsset::getTableName, tableName);
        wrapper.eq(DataAsset::getDeleted, 0);
        wrapper.last("LIMIT 1");
        return dataAssetMapper.selectOne(wrapper);
    }

    @Override
    public List<Long> importDiscoveredAssets(AssetDiscoveryImportDTO importDTO) {
        List<Long> assetIds = new ArrayList<>();
        DatabaseConnectionDTO connDTO = new DatabaseConnectionDTO();
        connDTO.setDatabaseType(importDTO.getDatabaseType());
        connDTO.setHost(importDTO.getHost());
        connDTO.setPort(importDTO.getPort());
        connDTO.setDatabaseName(importDTO.getDatabaseName());
        connDTO.setUsername(importDTO.getUsername());
        connDTO.setPassword(importDTO.getPassword());

        // 如果关联了数据源配置，优先从数据源配置获取连接信息（含凭证）
        if (importDTO.getDataSourceId() != null) {
            DataSourceConfig dsConfig = dataSourceConfigMapper.selectById(importDTO.getDataSourceId());
            if (dsConfig != null && "ACTIVE".equals(dsConfig.getStatus())) {
                connDTO.setDatabaseType(dsConfig.getDatabaseType());
                connDTO.setHost(dsConfig.getHost());
                connDTO.setPort(dsConfig.getPort());
                connDTO.setDatabaseName(dsConfig.getDatabaseName());
                connDTO.setUsername(dsConfig.getUsername());
                connDTO.setPassword(dsConfig.getPassword());
            }
        }

        String duplicateStrategy = importDTO.getDuplicateStrategy() != null ? importDTO.getDuplicateStrategy() : "SKIP";

        for (AssetDiscoveryImportDTO.TableImportItem item : importDTO.getTables()) {
            if (!item.isSelected()) continue;

            // 检测重复
            DataAsset existingAsset = findExistingAsset(connDTO.getHost(), connDTO.getPort(), connDTO.getDatabaseName(), item.getTableName());

            if (existingAsset != null) {
                if ("SKIP".equals(duplicateStrategy)) {
                    // 跳过重复
                    log.info("跳过重复资产：{} (ID: {})", existingAsset.getAssetName(), existingAsset.getAssetId());
                    continue;
                } else if ("OVERWRITE".equals(duplicateStrategy)) {
                    // 覆盖更新
                    log.info("覆盖更新资产：{} (ID: {})", existingAsset.getAssetName(), existingAsset.getAssetId());
                    overwriteAsset(existingAsset, item, importDTO, connDTO);
                    assetIds.add(existingAsset.getAssetId());
                    continue;
                }
                // FORCE: 强制新增，继续往下走
            }

            // 新增资产
            Long newId = createNewAsset(item, importDTO, connDTO);
            assetIds.add(newId);
        }
        return assetIds;
    }

    /**
     * 创建新资产及其字段
     */
    private Long createNewAsset(AssetDiscoveryImportDTO.TableImportItem item, AssetDiscoveryImportDTO importDTO, DatabaseConnectionDTO connDTO) {
        // 扫描字段
        DiscoveredTableVO tableInfo = scanTableFields(connDTO, item.getTableName());

        // 创建数据资产
        DataAsset asset = new DataAsset();
        asset.setAssetName(item.getTableComment() != null && !item.getTableComment().isEmpty()
                ? item.getTableComment() : item.getTableName());
        asset.setAssetCode(importDTO.getDatabaseName() + "." + item.getTableName());
        asset.setAssetType("TABLE");
        asset.setSystemName(importDTO.getSystemName());
        asset.setDatabaseType(connDTO.getDatabaseType());
        asset.setDatabaseHost(connDTO.getHost());
        asset.setDatabasePort(connDTO.getPort());
        asset.setDatabaseName(connDTO.getDatabaseName());
        asset.setTableName(item.getTableName());
        asset.setDataSourceId(importDTO.getDataSourceId());
        asset.setAssetDescription(item.getTableComment());
        asset.setDepartmentId(importDTO.getDepartmentId());
        asset.setOwnerId(importDTO.getOwnerId());
        asset.setStatus("DRAFT");
        asset.setContainsSensitiveData(false);
        asset.setCreatedTime(LocalDateTime.now());
        asset.setUpdatedTime(LocalDateTime.now());
        dataAssetMapper.insert(asset);

        // 创建字段
        insertFields(asset.getAssetId(), tableInfo);
        return asset.getAssetId();
    }

    /**
     * 覆盖更新已有资产
     */
    private void overwriteAsset(DataAsset existingAsset, AssetDiscoveryImportDTO.TableImportItem item, AssetDiscoveryImportDTO importDTO, DatabaseConnectionDTO connDTO) {
        // 扫描字段
        DiscoveredTableVO tableInfo = scanTableFields(connDTO, item.getTableName());

        // 更新资产基本信息
        existingAsset.setAssetName(item.getTableComment() != null && !item.getTableComment().isEmpty()
                ? item.getTableComment() : item.getTableName());
        existingAsset.setAssetCode(importDTO.getDatabaseName() + "." + item.getTableName());
        existingAsset.setSystemName(importDTO.getSystemName());
        existingAsset.setDatabaseType(connDTO.getDatabaseType());
        existingAsset.setDatabaseHost(connDTO.getHost());
        existingAsset.setDatabasePort(connDTO.getPort());
        existingAsset.setDatabaseName(connDTO.getDatabaseName());
        existingAsset.setTableName(item.getTableName());
        existingAsset.setDataSourceId(importDTO.getDataSourceId());
        existingAsset.setAssetDescription(item.getTableComment());
        if (importDTO.getDepartmentId() != null) {
            existingAsset.setDepartmentId(importDTO.getDepartmentId());
        }
        if (importDTO.getOwnerId() != null) {
            existingAsset.setOwnerId(importDTO.getOwnerId());
        }
        existingAsset.setUpdatedTime(LocalDateTime.now());
        dataAssetMapper.updateById(existingAsset);

        // 删除旧字段，重新插入
        LambdaQueryWrapper<DataField> fieldWrapper = new LambdaQueryWrapper<>();
        fieldWrapper.eq(DataField::getAssetId, existingAsset.getAssetId());
        dataFieldMapper.delete(fieldWrapper);

        insertFields(existingAsset.getAssetId(), tableInfo);
    }

    /**
     * 插入字段列表
     */
    private void insertFields(Long assetId, DiscoveredTableVO tableInfo) {
        if (tableInfo.getFields() != null) {
            int sortOrder = 1;
            for (DiscoveredTableVO.DiscoveredFieldVO fieldVO : tableInfo.getFields()) {
                DataField field = new DataField();
                field.setAssetId(assetId);
                field.setFieldName(fieldVO.getFieldName());
                field.setFieldCode(fieldVO.getFieldName());
                field.setFieldType(fieldVO.getFieldType());
                field.setFieldLength(fieldVO.getFieldLength());
                field.setNullable(fieldVO.getNullable());
                field.setIsPrimaryKey(fieldVO.getIsPrimaryKey());
                field.setFieldDescription(fieldVO.getFieldComment());
                field.setContainsSensitiveData(false);
                field.setSortOrder(sortOrder++);
                field.setStatus("ACTIVE");
                field.setCreatedTime(LocalDateTime.now());
                field.setUpdatedTime(LocalDateTime.now());
                dataFieldMapper.insert(field);
            }
        }
    }

    /**
     * 创建JDBC连接
     */
    private Connection createConnection(DatabaseConnectionDTO dto) throws SQLException {
        String url = buildJdbcUrl(dto);
        String driverClass = switch (dto.getDatabaseType().toUpperCase()) {
            case "MYSQL" -> "com.mysql.cj.jdbc.Driver";
            case "POSTGRESQL" -> "org.postgresql.Driver";
            default -> throw new RuntimeException("不支持的数据库类型: " + dto.getDatabaseType());
        };
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("数据库驱动未找到: " + driverClass + "，请确认相关依赖已添加");
        }
        return DriverManager.getConnection(url, dto.getUsername(), dto.getPassword());
    }

    /**
     * 构建JDBC URL
     */
    private String buildJdbcUrl(DatabaseConnectionDTO dto) {
        return switch (dto.getDatabaseType().toUpperCase()) {
            case "MYSQL" -> String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                    dto.getHost(), dto.getPort(), dto.getDatabaseName());
            case "POSTGRESQL" -> String.format("jdbc:postgresql://%s:%d/%s?sslmode=disable",
                    dto.getHost(), dto.getPort(), dto.getDatabaseName());
            default -> throw new RuntimeException("不支持的数据库类型: " + dto.getDatabaseType());
        };
    }

    /**
     * 获取schema
     */
    private String getSchema(DatabaseConnectionDTO dto, DatabaseMetaData metaData) throws SQLException {
        if ("POSTGRESQL".equalsIgnoreCase(dto.getDatabaseType())) {
            return "public";
        }
        return metaData.getUserName();
    }

    /**
     * 获取表行数估算
     */
    private long getRowCount(Connection conn, DatabaseConnectionDTO dto, String tableName) throws SQLException {
        String sql = switch (dto.getDatabaseType().toUpperCase()) {
            case "MYSQL" -> "SELECT TABLE_ROWS FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?";
            case "POSTGRESQL" -> "SELECT reltuples::bigint FROM pg_class WHERE relname = ?";
            default -> "SELECT 0";
        };

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if ("MYSQL".equalsIgnoreCase(dto.getDatabaseType())) {
                ps.setString(1, dto.getDatabaseName());
                ps.setString(2, tableName);
            } else {
                ps.setString(1, tableName);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return 0;
    }

    /**
     * 映射数据库字段类型到系统字段类型
     */
    private String mapFieldType(String dbType) {
        if (dbType == null) return "OTHER";
        String upper = dbType.toUpperCase();
        if (upper.contains("CHAR") || upper.contains("TEXT") || upper.contains("CLOB")) return "STRING";
        if (upper.contains("INT") || upper.contains("SERIAL") || upper.contains("BIGSERIAL")) return "INTEGER";
        if (upper.contains("DECIMAL") || upper.contains("NUMERIC") || upper.contains("DOUBLE") || upper.contains("FLOAT") || upper.contains("REAL")) return "DECIMAL";
        if (upper.contains("DATE") || upper.contains("TIME") || upper.contains("TIMESTAMP")) return "DATE";
        if (upper.contains("BOOL")) return "BOOLEAN";
        if (upper.contains("BLOB") || upper.contains("BYTEA") || upper.contains("BINARY")) return "BINARY";
        return "OTHER";
    }
}
