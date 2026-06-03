package com.dataasset.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataasset.security.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 部门Mapper接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {

    /**
     * 查询部门树
     *
     * @return 部门列表
     */
    @Select("SELECT * FROM department WHERE deleted = 0 ORDER BY sort_order ASC, created_time DESC")
    List<Department> selectDepartmentTree();

    /**
     * 根据负责人ID查询部门列表
     *
     * @param leaderId 负责人ID
     * @return 部门列表
     */
    @Select("SELECT * FROM department WHERE leader_id = #{leaderId} AND deleted = 0")
    List<Department> selectDepartmentsByLeaderId(Long leaderId);
}
