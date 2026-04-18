package com.dataasset.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataasset.security.entity.ApprovalProcessInstance;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批流程实例Mapper接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Mapper
public interface ApprovalProcessInstanceMapper extends BaseMapper<ApprovalProcessInstance> {
}
