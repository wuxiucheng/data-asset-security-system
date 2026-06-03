package com.dataasset.security.service;

import com.dataasset.security.dto.AssetDiscoveryImportDTO;
import com.dataasset.security.dto.DatabaseConnectionDTO;
import com.dataasset.security.vo.DiscoveredTableVO;
import com.dataasset.security.vo.ImportDuplicateCheckVO;

import java.util.List;

/**
 * 资产发现服务接口
 */
public interface AssetDiscoveryService {

    /**
     * 测试数据库连接
     */
    boolean testConnection(DatabaseConnectionDTO connectionDTO);

    /**
     * 扫描数据库中的所有表
     */
    List<DiscoveredTableVO> scanTables(DatabaseConnectionDTO connectionDTO);

    /**
     * 扫描指定表的字段信息
     */
    DiscoveredTableVO scanTableFields(DatabaseConnectionDTO connectionDTO, String tableName);

    /**
     * 批量导入发现的表为数据资产
     */
    List<Long> importDiscoveredAssets(AssetDiscoveryImportDTO importDTO);

    /**
     * 检测导入时的重复资产
     */
    ImportDuplicateCheckVO checkImportDuplicates(AssetDiscoveryImportDTO importDTO);
}
