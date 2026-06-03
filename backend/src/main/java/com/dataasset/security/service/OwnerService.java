package com.dataasset.security.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dataasset.security.dto.OwnerCreateDTO;
import com.dataasset.security.dto.OwnerQueryDTO;
import com.dataasset.security.dto.OwnerUpdateDTO;
import com.dataasset.security.entity.Owner;
import com.dataasset.security.vo.OwnerVO;

import java.util.List;

/**
 * 责任人管理Service接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public interface OwnerService extends IService<Owner> {

    /**
     * 创建责任人
     *
     * @param createDTO 创建责任人请求
     * @return 责任人ID
     */
    Long createOwner(OwnerCreateDTO createDTO);

    /**
     * 更新责任人
     *
     * @param updateDTO 更新责任人请求
     */
    void updateOwner(OwnerUpdateDTO updateDTO);

    /**
     * 删除责任人
     *
     * @param ownerId 责任人ID
     */
    void deleteOwner(Long ownerId);

    /**
     * 分页查询责任人
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<OwnerVO> queryOwners(OwnerQueryDTO queryDTO);

    /**
     * 获取责任人详情
     *
     * @param ownerId 责任人ID
     * @return 责任人详情
     */
    OwnerVO getOwnerDetail(Long ownerId);

    /**
     * 更新责任人状态
     *
     * @param ownerId 责任人ID
     * @param status  状态
     */
    void updateOwnerStatus(Long ownerId, String status);

    /**
     * 根据部门ID查询责任人列表
     *
     * @param departmentId 部门ID
     * @return 责任人列表
     */
    List<OwnerVO> getOwnersByDepartmentId(Long departmentId);

    /**
     * 获取所有责任人
     *
     * @return 责任人列表
     */
    List<OwnerVO> getAllOwners();
}
