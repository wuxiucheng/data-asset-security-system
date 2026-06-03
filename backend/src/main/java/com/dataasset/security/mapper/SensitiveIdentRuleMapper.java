package com.dataasset.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataasset.security.entity.SensitiveIdentRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 敏感数据识别规则Mapper
 */
@Mapper
public interface SensitiveIdentRuleMapper extends BaseMapper<SensitiveIdentRule> {
}
