package com.dataasset.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataasset.security.entity.DataAsset;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据资产Mapper接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Mapper
public interface DataAssetMapper extends BaseMapper<DataAsset> {
}
