package com.dataasset.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataasset.security.entity.QualityAlert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 质量告警Mapper
 */
@Mapper
public interface QualityAlertMapper extends BaseMapper<QualityAlert> {
}
