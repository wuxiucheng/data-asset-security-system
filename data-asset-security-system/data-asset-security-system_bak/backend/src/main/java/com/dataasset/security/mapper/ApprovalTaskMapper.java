package com.dataasset.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataasset.security.entity.ApprovalTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批任务Mapper接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Mapper
public interface ApprovalTaskMapper extends BaseMapper<ApprovalTask> {
}
