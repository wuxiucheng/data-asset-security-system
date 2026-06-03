package com.dataasset.security.service;

/**
 * 动态数据库查询服务接口
 * 根据数据库连接信息动态建立连接并执行COUNT查询
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public interface DynamicDatabaseQueryService {

    /**
     * 查询指定数据表的记录总行数（无凭证，仅用于无需认证的数据库）
     */
    long queryTableRowCount(String databaseType, String host, int port, String databaseName, String tableName);

    /**
     * 查询指定数据表的记录总行数（带凭证）
     */
    long queryTableRowCount(String databaseType, String host, int port, String databaseName,
                            String username, String password, String tableName);

    /**
     * 查询指定字段在数据表中非空值的记录条数（无凭证）
     */
    long queryFieldRowCount(String databaseType, String host, int port, String databaseName,
                            String tableName, String fieldName);

    /**
     * 查询指定字段在数据表中非空值的记录条数（带凭证）
     */
    long queryFieldRowCount(String databaseType, String host, int port, String databaseName,
                            String username, String password, String tableName, String fieldName);
}
