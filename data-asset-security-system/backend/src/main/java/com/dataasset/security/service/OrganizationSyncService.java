package com.dataasset.security.service;

import com.dataasset.security.dto.OrganizationComparisonDTO;
import com.dataasset.security.dto.OrganizationNodeDTO;

import java.util.List;

/**
 * 组织架构同步Service接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public interface OrganizationSyncService {

    /**
     * 导入组织架构
     *
     * @param organization 组织架构数据
     * @return 导入结果
     */
    String importOrganization(OrganizationNodeDTO organization);

    /**
     * 对比组织架构
     *
     * @param organization 组织架构数据
     * @return 对比结果
     */
    OrganizationComparisonDTO compareOrganization(OrganizationNodeDTO organization);

    /**
     * 同步组织架构
     *
     * @param organization 组织架构数据
     * @return 同步结果
     */
    String syncOrganization(OrganizationNodeDTO organization);

    /**
     * 导出组织架构
     *
     * @return 组织架构数据
     */
    OrganizationNodeDTO exportOrganization();
}
