package com.dataasset.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataasset.security.entity.MaskWhitelist;
import org.apache.ibatis.annotations.Mapper;

/**
 * 脱敏白名单Mapper
 */
@Mapper
public interface MaskWhitelistMapper extends BaseMapper<MaskWhitelist> {
}
