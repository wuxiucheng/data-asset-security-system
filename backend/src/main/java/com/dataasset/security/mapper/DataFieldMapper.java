package com.dataasset.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataasset.security.entity.DataField;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据字段Mapper接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Mapper
public interface DataFieldMapper extends BaseMapper<DataField> {
}
