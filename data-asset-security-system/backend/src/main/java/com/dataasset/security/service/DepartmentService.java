package com.dataasset.security.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dataasset.security.dto.DepartmentCreateDTO;
import com.dataasset.security.dto.DepartmentQueryDTO;
import com.dataasset.security.dto.DepartmentUpdateDTO;
import com.dataasset.security.entity.Department;
import com.dataasset.security.vo.DepartmentVO;

import java.util.List;

/**
 * 部门管理Service接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public interface DepartmentService extends IService<Department> {

    /**
     * 创建部门
     *
     * @param createDTO 创建部门请求
     * @return 部门ID
     */
    Long createDepartment(DepartmentCreateDTO createDTO);

    /**
     * 更新部门
     *
     * @param updateDTO 更新部门请求
     */
    void updateDepartment(DepartmentUpdateDTO updateDTO);

    /**
     * 删除部门
     *
     * @param departmentId 部门ID
     */
    void deleteDepartment(Long departmentId);

    /**
     * 分页查询部门
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<DepartmentVO> queryDepartments(DepartmentQueryDTO queryDTO);

    /**
     * 获取部门详情
     *
     * @param departmentId 部门ID
     * @return 部门详情
     */
    DepartmentVO getDepartmentDetail(Long departmentId);

    /**
     * 获取部门树
     *
     * @return 部门树
     */
    List<DepartmentVO> getDepartmentTree();

    /**
     * 分配部门负责人
     *
     * @param departmentId 部门ID
     * @param leaderId      负责人ID
     */
    void assignLeader(Long departmentId, Long leaderId);

    /**
     * 更新部门状态
     *
     * @param departmentId 部门ID
     * @param status        状态
     */
    void updateDepartmentStatus(Long departmentId, String status);
}
