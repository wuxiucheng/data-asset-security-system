package com.dataasset.security.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dataasset.security.dto.DataFieldCreateDTO;
import com.dataasset.security.dto.DataFieldUpdateDTO;
import com.dataasset.security.entity.DataField;
import com.dataasset.security.vo.DataFieldVO;

import java.util.List;

/**
 * 数据字段管理Service接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public interface DataFieldService extends IService<DataField> {

    /**
     * 创建数据字段
     *
     * @param createDTO 创建数据字段请求
     * @return 字段ID
     */
    Long createDataField(DataFieldCreateDTO createDTO);

    /**
     * 批量创建数据字段
     *
     * @param createDTOList 创建数据字段请求列表
     * @return 字段ID列表
     */
    List<Long> batchCreateDataFields(List<DataFieldCreateDTO> createDTOList);

    /**
     * 更新数据字段
     *
     * @param updateDTO 更新数据字段请求
     */
    void updateDataField(DataFieldUpdateDTO updateDTO);

    /**
     * 删除数据字段
     *
     * @param fieldId 字段ID
     */
    void deleteDataField(Long fieldId);

    /**
     * 获取数据字段详情
     *
     * @param fieldId 字段ID
     * @return 字段详情
     */
    DataFieldVO getDataFieldDetail(Long fieldId);

    /**
     * 根据资产ID查询数据字段列表
     *
     * @param assetId 资产ID
     * @return 字段列表
     */
    List<DataFieldVO> getDataFieldsByAssetId(Long assetId);
}
