package com.dataasset.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataasset.security.entity.DataSourceConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据源配置Mapper接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Mapper
public interface DataSourceConfigMapper extends BaseMapper<DataSourceConfig> {
}
