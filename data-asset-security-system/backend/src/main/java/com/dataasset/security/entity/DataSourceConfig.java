package com.dataasset.security.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dataasset.security.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据源配置实体类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("data_source_config")
public class DataSourceConfig extends BaseEntity implements Serializable {

    @TableId(value = "data_source_id", type = IdType.AUTO)
    private Long dataSourceId;

    /**
     * 数据源名称
     */
    private String dataSourceName;

    /**
     * 数据库类型：MYSQL, ORACLE, POSTGRESQL, SQLSERVER
     */
    private String databaseType;

    /**
     * 数据库地址
     */
    private String host;

    /**
     * 数据库端口
     */
    private Integer port;

    /**
     * 数据库名称
     */
    private String databaseName;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密存储）
     */
    private String password;

    /**
     * 额外连接参数
     */
    private String connectionParams;

    /**
     * 状态：ACTIVE, INACTIVE
     */
    private String status;

    /**
     * 最后测试连接时间
     */
    private LocalDateTime lastTestTime;

    /**
     * 最后测试连接结果
     */
    private String lastTestResult;

    /**
     * 备注
     */
    private String remarks;
}
