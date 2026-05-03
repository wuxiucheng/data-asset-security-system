package com.dataasset.security.service.impl;

import com.dataasset.security.common.exception.BusinessException;
import com.dataasset.security.service.DynamicDatabaseQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

/**
 * 动态数据库查询服务实现类
 * 通过JDBC动态建立到目标数据库的短连接，执行COUNT查询后关闭连接
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@Service
public class DynamicDatabaseQueryServiceImpl implements DynamicDatabaseQueryService {

    private static final int QUERY_TIMEOUT_SECONDS = 10;

    private static final Map<String, String> DRIVER_MAP = Map.of(
            "MYSQL", "com.mysql.cj.jdbc.Driver",
            "ORACLE", "oracle.jdbc.OracleDriver",
            "POSTGRESQL", "org.postgresql.Driver",
            "SQLSERVER", "com.microsoft.sqlserver.jdbc.SQLServerDriver"
    );

    private static final Map<String, String> URL_PREFIX_MAP = Map.of(
            "MYSQL", "jdbc:mysql://",
            "ORACLE", "jdbc:oracle:thin:@",
            "POSTGRESQL", "jdbc:postgresql://",
            "SQLSERVER", "jdbc:sqlserver://"
    );

    @Override
    public long queryTableRowCount(String databaseType, String host, int port, String databaseName, String tableName) {
        return queryTableRowCount(databaseType, host, port, databaseName, null, null, tableName);
    }

    @Override
    public long queryTableRowCount(String databaseType, String host, int port, String databaseName,
                                    String username, String password, String tableName) {
        validateIdentifier(tableName, "表名");
        String sql = "SELECT COUNT(*) FROM " + tableName;
        return executeCountQuery(databaseType, host, port, databaseName, username, password, sql);
    }

    @Override
    public long queryFieldRowCount(String databaseType, String host, int port, String databaseName,
                                    String tableName, String fieldName) {
        return queryFieldRowCount(databaseType, host, port, databaseName, null, null, tableName, fieldName);
    }

    @Override
    public long queryFieldRowCount(String databaseType, String host, int port, String databaseName,
                                    String username, String password, String tableName, String fieldName) {
        validateIdentifier(tableName, "表名");
        validateIdentifier(fieldName, "字段名");
        String sql = "SELECT COUNT(" + fieldName + ") FROM " + tableName;
        return executeCountQuery(databaseType, host, port, databaseName, username, password, sql);
    }

    private long executeCountQuery(String databaseType, String host, int port, String databaseName,
                                    String username, String password, String sql) {
        String driverClass = DRIVER_MAP.get(databaseType.toUpperCase());
        String urlPrefix = URL_PREFIX_MAP.get(databaseType.toUpperCase());

        if (driverClass == null || urlPrefix == null) {
            throw new BusinessException("不支持的数据库类型：" + databaseType);
        }

        String jdbcUrl = buildJdbcUrl(databaseType.toUpperCase(), urlPrefix, host, port, databaseName);
        Connection connection = null;

        try {
            Class.forName(driverClass);

            // 优先使用带凭证的连接，否则使用无凭证连接
            if (username != null && !username.isEmpty()) {
                connection = DriverManager.getConnection(jdbcUrl, username, password);
            } else {
                connection = DriverManager.getConnection(jdbcUrl);
            }
            connection.setReadOnly(true);

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setQueryTimeout(QUERY_TIMEOUT_SECONDS);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (ClassNotFoundException e) {
            throw new BusinessException("数据库驱动未找到：" + driverClass);
        } catch (Exception e) {
            log.error("查询数据条数失败，SQL: {}, 错误: {}", sql, e.getMessage());
            throw new BusinessException("查询数据条数失败：" + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    log.warn("关闭数据库连接失败: {}", e.getMessage());
                }
            }
        }
    }

    private String buildJdbcUrl(String databaseType, String urlPrefix, String host, int port, String databaseName) {
        return switch (databaseType) {
            case "MYSQL" -> urlPrefix + host + ":" + port + "/" + databaseName + "?useSSL=false&connectTimeout=5000&socketTimeout=10000";
            case "ORACLE" -> urlPrefix + host + ":" + port + ":" + databaseName;
            case "POSTGRESQL" -> urlPrefix + host + ":" + port + "/" + databaseName + "?connectTimeout=5";
            case "SQLSERVER" -> urlPrefix + host + ":" + port + ";databaseName=" + databaseName + ";connectTimeout=5";
            default -> throw new BusinessException("不支持的数据库类型：" + databaseType);
        };
    }

    private void validateIdentifier(String identifier, String label) {
        if (identifier == null || identifier.isEmpty()) {
            throw new BusinessException(label + "不能为空");
        }
        // 允许字母、数字、下划线、中文、点号（支持 schema.table 格式）
        // 禁止 SQL 注入危险字符：分号、空格、引号、注释符等
        if (identifier.contains(";") || identifier.contains("--") || identifier.contains("'")
                || identifier.contains("\"") || identifier.contains("/*") || identifier.contains("*/")) {
            throw new BusinessException(label + "包含非法字符：" + identifier);
        }
    }
}
