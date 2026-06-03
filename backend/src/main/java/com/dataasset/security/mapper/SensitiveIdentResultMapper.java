package com.dataasset.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataasset.security.entity.SensitiveIdentResult;
import org.apache.ibatis.annotations.Mapper;

/**
 * 敏感数据识别结果Mapper
 */
@Mapper
public interface SensitiveIdentResultMapper extends BaseMapper<SensitiveIdentResult> {
}
