package com.dataasset.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataasset.security.entity.Owner;
import org.apache.ibatis.annotations.Mapper;

/**
 * 责任人Mapper接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Mapper
public interface OwnerMapper extends BaseMapper<Owner> {
}
