package com.dataasset.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.common.exception.BusinessException;
import com.dataasset.security.dto.DataSourceConfigCreateDTO;
import com.dataasset.security.dto.DataSourceConfigQueryDTO;
import com.dataasset.security.entity.DataSourceConfig;
import com.dataasset.security.mapper.DataSourceConfigMapper;
import com.dataasset.security.service.DataSourceConfigService;
import com.dataasset.security.vo.DataSourceConfigVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据源配置服务实现
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataSourceConfigServiceImpl implements DataSourceConfigService {

    private final DataSourceConfigMapper dataSourceConfigMapper;

    private static final Map<String, String> DRIVER_MAP = Map.of(
            "MYSQL", "com.mysql.cj.jdbc.Driver",
            "ORACLE", "oracle.jdbc.OracleDriver",
            "POSTGRESQL", "org.postgresql.Driver",
            "SQLSERVER", "com.microsoft.sqlserver.jdbc.SQLServerDriver"
    );

    @Override
    public Page<DataSourceConfigVO> queryDataSourceConfigs(DataSourceConfigQueryDTO queryDTO) {
        LambdaQueryWrapper<DataSourceConfig> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(queryDTO.getDataSourceName())) {
            wrapper.like(DataSourceConfig::getDataSourceName, queryDTO.getDataSourceName());
        }
        if (StringUtils.hasText(queryDTO.getDatabaseType())) {
            wrapper.eq(DataSourceConfig::getDatabaseType, queryDTO.getDatabaseType());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(DataSourceConfig::getStatus, queryDTO.getStatus());
        }
        wrapper.orderByDesc(DataSourceConfig::getCreatedTime);

        Page<DataSourceConfig> page = dataSourceConfigMapper.selectPage(
                new Page<>(queryDTO.getCurrent(), queryDTO.getSize()), wrapper);

        Page<DataSourceConfigVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public List<DataSourceConfigVO> listActiveDataSources() {
        LambdaQueryWrapper<DataSourceConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataSourceConfig::getStatus, "ACTIVE");
        wrapper.orderByAsc(DataSourceConfig::getDataSourceName);
        return dataSourceConfigMapper.selectList(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public DataSourceConfigVO getDataSourceConfig(Long dataSourceId) {
        DataSourceConfig config = dataSourceConfigMapper.selectById(dataSourceId);
        if (config == null) {
            throw new BusinessException("数据源配置不存在");
        }
        return convertToVO(config);
    }

    @Override
    public Long createDataSourceConfig(DataSourceConfigCreateDTO createDTO) {
        DataSourceConfig config = new DataSourceConfig();
        BeanUtils.copyProperties(createDTO, config);
        config.setStatus("ACTIVE");
        config.setCreatedTime(LocalDateTime.now());
        config.setUpdatedTime(LocalDateTime.now());
        dataSourceConfigMapper.insert(config);
        return config.getDataSourceId();
    }

    @Override
    public void updateDataSourceConfig(Long dataSourceId, DataSourceConfigCreateDTO updateDTO) {
        DataSourceConfig config = dataSourceConfigMapper.selectById(dataSourceId);
        if (config == null) {
            throw new BusinessException("数据源配置不存在");
        }
        config.setDataSourceName(updateDTO.getDataSourceName());
        config.setDatabaseType(updateDTO.getDatabaseType());
        config.setHost(updateDTO.getHost());
        config.setPort(updateDTO.getPort());
        config.setDatabaseName(updateDTO.getDatabaseName());
        config.setUsername(updateDTO.getUsername());
        if (StringUtils.hasText(updateDTO.getPassword())) {
            config.setPassword(updateDTO.getPassword());
        }
        config.setConnectionParams(updateDTO.getConnectionParams());
        config.setRemarks(updateDTO.getRemarks());
        config.setUpdatedTime(LocalDateTime.now());
        dataSourceConfigMapper.updateById(config);
    }

    @Override
    public void deleteDataSourceConfig(Long dataSourceId) {
        DataSourceConfig config = dataSourceConfigMapper.selectById(dataSourceId);
        if (config == null) {
            throw new BusinessException("数据源配置不存在");
        }
        dataSourceConfigMapper.deleteById(dataSourceId);
    }

    @Override
    public boolean testConnection(Long dataSourceId) {
        DataSourceConfig config = dataSourceConfigMapper.selectById(dataSourceId);
        if (config == null) {
            throw new BusinessException("数据源配置不存在");
        }
        boolean success = doTestConnection(config.getDatabaseType(), config.getHost(),
                config.getPort(), config.getDatabaseName(), config.getUsername(), config.getPassword());

        // 更新测试结果
        config.setLastTestTime(LocalDateTime.now());
        config.setLastTestResult(success ? "连接成功" : "连接失败");
        config.setUpdatedTime(LocalDateTime.now());
        dataSourceConfigMapper.updateById(config);

        return success;
    }

    @Override
    public boolean testConnection(DataSourceConfigCreateDTO dto) {
        return doTestConnection(dto.getDatabaseType(), dto.getHost(),
                dto.getPort(), dto.getDatabaseName(), dto.getUsername(), dto.getPassword());
    }

    /**
     * 执行连接测试
     */
    private boolean doTestConnection(String databaseType, String host, Integer port,
                                      String databaseName, String username, String password) {
        if (port == null) {
            throw new BusinessException("数据库端口不能为空");
        }
        String driverClass = DRIVER_MAP.get(databaseType.toUpperCase());
        if (driverClass == null) {
            throw new BusinessException("不支持的数据库类型：" + databaseType);
        }
        String jdbcUrl = buildJdbcUrl(databaseType, host, port, databaseName);
        try {
            Class.forName(driverClass);
            try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
                return conn.isValid(5);
            }
        } catch (ClassNotFoundException e) {
            throw new BusinessException("数据库驱动未找到：" + driverClass);
        } catch (Exception e) {
            log.error("数据库连接测试失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 构建JDBC URL
     */
    private String buildJdbcUrl(String databaseType, String host, int port, String databaseName) {
        return switch (databaseType.toUpperCase()) {
            case "MYSQL" -> String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", host, port, databaseName);
            case "ORACLE" -> String.format("jdbc:oracle:thin:@%s:%d:%s", host, port, databaseName);
            case "POSTGRESQL" -> String.format("jdbc:postgresql://%s:%d/%s?sslmode=disable", host, port, databaseName);
            case "SQLSERVER" -> String.format("jdbc:sqlserver://%s:%d;databaseName=%s;encrypt=false", host, port, databaseName);
            default -> throw new BusinessException("不支持的数据库类型：" + databaseType);
        };
    }

    /**
     * 实体转VO
     */
    private DataSourceConfigVO convertToVO(DataSourceConfig config) {
        DataSourceConfigVO vo = new DataSourceConfigVO();
        BeanUtils.copyProperties(config, vo);
        return vo;
    }
}
