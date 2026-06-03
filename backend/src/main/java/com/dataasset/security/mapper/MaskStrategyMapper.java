package com.dataasset.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataasset.security.entity.MaskStrategy;
import org.apache.ibatis.annotations.Mapper;

/**
 * 脱敏策略Mapper
 */
@Mapper
public interface MaskStrategyMapper extends BaseMapper<MaskStrategy> {
}
