package com.dataasset.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataasset.security.entity.DataClassification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据分类Mapper接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Mapper
public interface DataClassificationMapper extends BaseMapper<DataClassification> {
}
