package com.dataasset.security.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.dto.DataSourceConfigCreateDTO;
import com.dataasset.security.dto.DataSourceConfigQueryDTO;
import com.dataasset.security.vo.DataSourceConfigVO;

import java.util.List;

/**
 * 数据源配置服务接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public interface DataSourceConfigService {

    /**
     * 分页查询数据源配置
     */
    Page<DataSourceConfigVO> queryDataSourceConfigs(DataSourceConfigQueryDTO queryDTO);

    /**
     * 查询所有活跃的数据源配置（不分页，用于下拉选择）
     */
    List<DataSourceConfigVO> listActiveDataSources();

    /**
     * 获取数据源配置详情
     */
    DataSourceConfigVO getDataSourceConfig(Long dataSourceId);

    /**
     * 创建数据源配置
     */
    Long createDataSourceConfig(DataSourceConfigCreateDTO createDTO);

    /**
     * 更新数据源配置
     */
    void updateDataSourceConfig(Long dataSourceId, DataSourceConfigCreateDTO updateDTO);

    /**
     * 删除数据源配置
     */
    void deleteDataSourceConfig(Long dataSourceId);

    /**
     * 测试数据源连接
     */
    boolean testConnection(Long dataSourceId);

    /**
     * 测试数据源连接（使用DTO参数，不保存）
     */
    boolean testConnection(DataSourceConfigCreateDTO dto);
}
