package com.dataasset.security.service.impl;

import com.alibaba.fastjson2.JSON;
import com.dataasset.security.common.exception.BusinessException;
import com.dataasset.security.dto.OrganizationComparisonDTO;
import com.dataasset.security.dto.OrganizationNodeDTO;
import com.dataasset.security.entity.Department;
import com.dataasset.security.entity.Owner;
import com.dataasset.security.mapper.DepartmentMapper;
import com.dataasset.security.mapper.OwnerMapper;
import com.dataasset.security.service.OrganizationSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 组织架构同步Service实现类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationSyncServiceImpl implements OrganizationSyncService {

    private final DepartmentMapper departmentMapper;
    private final OwnerMapper ownerMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importOrganization(OrganizationNodeDTO organization) {
        log.info("开始导入组织架构：{}", organization.getNodeName());

        // 导入部门和责任人
        importNode(organization, null);

        log.info("组织架构导入完成");
        return "组织架构导入成功";
    }

    @Override
    public OrganizationComparisonDTO compareOrganization(OrganizationNodeDTO organization) {
        log.info("开始对比组织架构");

        // 获取现有组织架构
        List<Department> existingDepartments = departmentMapper.selectList(null);
        List<Owner> existingOwners = ownerMapper.selectList(null);

        // 构建现有组织架构映射
        Map<String, Department> existingDepartmentMap = existingDepartments.stream()
                .collect(Collectors.toMap(Department::getDepartmentCode, dept -> dept));
        Map<String, Owner> existingOwnerMap = existingOwners.stream()
                .collect(Collectors.toMap(Owner::getEmployeeNo, owner -> owner));

        // 构建新组织架构映射
        Map<String, OrganizationNodeDTO> newDepartmentMap = new HashMap<>();
        Map<String, OrganizationNodeDTO> newOwnerMap = new HashMap<>();

        buildOrganizationMap(organization, null, newDepartmentMap, newOwnerMap);

        // 对比差异
        int newDepartments = 0;
        int newOwners = 0;
        int deletedDepartments = 0;
        int deletedOwners = 0;
        int updatedDepartments = 0;
        int updatedOwners = 0;

        // 对比部门
        for (String code : newDepartmentMap.keySet()) {
            if (!existingDepartmentMap.containsKey(code)) {
                newDepartments++;
            } else {
                updatedDepartments++;
            }
        }

        for (String code : existingDepartmentMap.keySet()) {
            if (!newDepartmentMap.containsKey(code)) {
                deletedDepartments++;
            }
        }

        // 对比责任人
        for (String employeeNo : newOwnerMap.keySet()) {
            if (!existingOwnerMap.containsKey(employeeNo)) {
                newOwners++;
            } else {
                updatedOwners++;
            }
        }

        for (String employeeNo : existingOwnerMap.keySet()) {
            if (!newOwnerMap.containsKey(employeeNo)) {
                deletedOwners++;
            }
        }

        OrganizationComparisonDTO comparison = new OrganizationComparisonDTO();
        comparison.setNewDepartments(newDepartments);
        comparison.setNewOwners(newOwners);
        comparison.setDeletedDepartments(deletedDepartments);
        comparison.setDeletedOwners(deletedOwners);
        comparison.setUpdatedDepartments(updatedDepartments);
        comparison.setUpdatedOwners(updatedOwners);
        comparison.setDetails(String.format("新增部门：%d，新增责任人：%d，删除部门：%d，删除责任人：%d，更新部门：%d，更新责任人：%d",
                newDepartments, newOwners, deletedDepartments, deletedOwners, updatedDepartments, updatedOwners));

        log.info("组织架构对比完成：{}", comparison.getDetails());
        return comparison;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String syncOrganization(OrganizationNodeDTO organization) {
        log.info("开始同步组织架构：{}", organization.getNodeName());

        // 获取现有组织架构
        List<Department> existingDepartments = departmentMapper.selectList(null);
        List<Owner> existingOwners = ownerMapper.selectList(null);

        // 构建现有组织架构映射
        Map<String, Department> existingDepartmentMap = existingDepartments.stream()
                .collect(Collectors.toMap(Department::getDepartmentCode, dept -> dept));
        Map<String, Owner> existingOwnerMap = existingOwners.stream()
                .collect(Collectors.toMap(Owner::getEmployeeNo, owner -> owner));

        // 构建新组织架构映射
        Map<String, OrganizationNodeDTO> newDepartmentMap = new HashMap<>();
        Map<String, OrganizationNodeDTO> newOwnerMap = new HashMap<>();

        buildOrganizationMap(organization, null, newDepartmentMap, newOwnerMap);

        // 同步部门
        for (Map.Entry<String, OrganizationNodeDTO> entry : newDepartmentMap.entrySet()) {
            String code = entry.getKey();
            OrganizationNodeDTO node = entry.getValue();

            if (!existingDepartmentMap.containsKey(code)) {
                // 新增部门
                createDepartmentFromNode(node, null);
            } else {
                // 更新部门
                updateDepartmentFromNode(node, existingDepartmentMap.get(code));
            }
        }

        // 删除不存在的部门
        for (Map.Entry<String, Department> entry : existingDepartmentMap.entrySet()) {
            String code = entry.getKey();
            if (!newDepartmentMap.containsKey(code)) {
                // 删除部门（逻辑删除）
                Department department = entry.getValue();
                departmentMapper.deleteById(department.getDepartmentId());
            }
        }

        // 同步责任人
        for (Map.Entry<String, OrganizationNodeDTO> entry : newOwnerMap.entrySet()) {
            String employeeNo = entry.getKey();
            OrganizationNodeDTO node = entry.getValue();

            if (!existingOwnerMap.containsKey(employeeNo)) {
                // 新增责任人
                createOwnerFromNode(node);
            } else {
                // 更新责任人
                updateOwnerFromNode(node, existingOwnerMap.get(employeeNo));
            }
        }

        // 删除不存在的责任人
        for (Map.Entry<String, Owner> entry : existingOwnerMap.entrySet()) {
            String employeeNo = entry.getKey();
            if (!newOwnerMap.containsKey(employeeNo)) {
                // 删除责任人（逻辑删除）
                Owner owner = entry.getValue();
                ownerMapper.deleteById(owner.getOwnerId());
            }
        }

        log.info("组织架构同步完成");
        return "组织架构同步成功";
    }

    @Override
    public OrganizationNodeDTO exportOrganization() {
        log.info("开始导出组织架构");

        // 查询所有部门
        List<Department> departments = departmentMapper.selectList(null);
        List<Owner> owners = ownerMapper.selectList(null);

        // 构建部门映射
        Map<Long, Department> departmentMap = departments.stream()
                .collect(Collectors.toMap(Department::getDepartmentId, dept -> dept));

        // 构建责任人映射
        Map<Long, List<Owner>> departmentOwnersMap = owners.stream()
                .collect(Collectors.groupingBy(Owner::getDepartmentId));

        // 构建组织架构树
        OrganizationNodeDTO root = buildOrganizationTree(departments, departmentOwnersMap, departmentMap);

        log.info("组织架构导出完成");
        return root;
    }

    /**
     * 递归导入节点
     */
    private void importNode(OrganizationNodeDTO node, Long parentDepartmentId) {
        if (node == null) {
            return;
        }

        if ("DEPARTMENT".equals(node.getNodeType())) {
            // 导入部门
            Long departmentId = importDepartment(node, parentDepartmentId);

            // 递归导入子节点
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                for (OrganizationNodeDTO child : node.getChildren()) {
                    importNode(child, departmentId);
                }
            }
        } else if ("POSITION".equals(node.getNodeType())) {
            // 导入责任人
            importOwner(node, parentDepartmentId);
        }
    }

    /**
     * 导入部门
     */
    private Long importDepartment(OrganizationNodeDTO node, Long parentDepartmentId) {
        // 检查部门是否已存在
        Department existingDepartment = departmentMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Department>()
                        .eq(Department::getDepartmentCode, node.getNodeId())
        );

        if (existingDepartment != null) {
            // 更新部门
            existingDepartment.setDepartmentName(node.getNodeName());
            if (parentDepartmentId != null) {
                existingDepartment.setParentId(parentDepartmentId);
            }
            existingDepartment.setUpdatedTime(LocalDateTime.now());
            departmentMapper.updateById(existingDepartment);
            return existingDepartment.getDepartmentId();
        } else {
            // 创建部门
            Department department = new Department();
            department.setDepartmentCode(node.getNodeId());
            department.setDepartmentName(node.getNodeName());
            department.setParentId(parentDepartmentId);
            department.setSortOrder(0);
            department.setStatus("ACTIVE");
            department.setCreatedTime(LocalDateTime.now());
            department.setUpdatedTime(LocalDateTime.now());
            departmentMapper.insert(department);
            return department.getDepartmentId();
        }
    }

    /**
     * 导入责任人
     */
    private void importOwner(OrganizationNodeDTO node, Long departmentId) {
        // 检查责任人是否已存在
        Owner existingOwner = ownerMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Owner>()
                        .eq(Owner::getEmployeeNo, node.getLeaderEmployeeNo())
        );

        if (existingOwner == null) {
            // 创建责任人
            Owner owner = new Owner();
            owner.setEmployeeNo(node.getLeaderEmployeeNo());
            owner.setName(node.getLeaderName());
            owner.setDepartmentId(departmentId);
            owner.setPosition(node.getPosition());
            owner.setContactPhone(node.getContactPhone());
            owner.setEmail(node.getEmail());
            owner.setStatus("ACTIVE");
            owner.setCreatedTime(LocalDateTime.now());
            owner.setUpdatedTime(LocalDateTime.now());
            ownerMapper.insert(owner);
        } else {
            // 更新责任人
            existingOwner.setName(node.getLeaderName());
            existingOwner.setDepartmentId(departmentId);
            existingOwner.setPosition(node.getPosition());
            existingOwner.setContactPhone(node.getContactPhone());
            existingOwner.setEmail(node.getEmail());
            existingOwner.setUpdatedTime(LocalDateTime.now());
            ownerMapper.updateById(existingOwner);
        }
    }

    /**
     * 构建组织架构映射
     */
    private void buildOrganizationMap(OrganizationNodeDTO node, String parentCode,
                                       Map<String, OrganizationNodeDTO> departmentMap,
                                       Map<String, OrganizationNodeDTO> ownerMap) {
        if (node == null) {
            return;
        }

        if ("DEPARTMENT".equals(node.getNodeType())) {
            departmentMap.put(node.getNodeId(), node);

            // 递归处理子节点
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                for (OrganizationNodeDTO child : node.getChildren()) {
                    buildOrganizationMap(child, node.getNodeId(), departmentMap, ownerMap);
                }
            }
        } else if ("POSITION".equals(node.getNodeType())) {
            if (StringUtils.hasText(node.getLeaderEmployeeNo())) {
                ownerMap.put(node.getLeaderEmployeeNo(), node);
            }
        }
    }

    /**
     * 从节点创建部门
     */
    private void createDepartmentFromNode(OrganizationNodeDTO node, Long parentId) {
        Department department = new Department();
        department.setDepartmentCode(node.getNodeId());
        department.setDepartmentName(node.getNodeName());
        department.setParentId(parentId);
        department.setSortOrder(0);
        department.setStatus("ACTIVE");
        department.setCreatedTime(LocalDateTime.now());
        department.setUpdatedTime(LocalDateTime.now());
        departmentMapper.insert(department);
    }

    /**
     * 从节点更新部门
     */
    private void updateDepartmentFromNode(OrganizationNodeDTO node, Department department) {
        if (StringUtils.hasText(node.getNodeName())) {
            department.setDepartmentName(node.getNodeName());
        }
        if (node.getParentNodeId() != null) {
            department.setParentId(Long.parseLong(node.getParentNodeId()));
        }
        department.setUpdatedTime(LocalDateTime.now());
        departmentMapper.updateById(department);
    }

    /**
     * 从节点创建责任人
     */
    private void createOwnerFromNode(OrganizationNodeDTO node) {
        // 查找部门ID
        Long departmentId = null;
        if (node.getParentNodeId() != null) {
            Department department = departmentMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Department>()
                            .eq(Department::getDepartmentCode, node.getParentNodeId())
            );
            if (department != null) {
                departmentId = department.getDepartmentId();
            }
        }

        Owner owner = new Owner();
        owner.setEmployeeNo(node.getLeaderEmployeeNo());
        owner.setName(node.getLeaderName());
        owner.setDepartmentId(departmentId);
        owner.setPosition(node.getPosition());
        owner.setContactPhone(node.getContactPhone());
        owner.setEmail(node.getEmail());
        owner.setStatus("ACTIVE");
        owner.setCreatedTime(LocalDateTime.now());
        owner.setUpdatedTime(LocalDateTime.now());
        ownerMapper.insert(owner);
    }

    /**
     * 从节点更新责任人
     */
    private void updateOwnerFromNode(OrganizationNodeDTO node, Owner owner) {
        if (StringUtils.hasText(node.getLeaderName())) {
            owner.setName(node.getLeaderName());
        }
        if (node.getParentNodeId() != null) {
            Department department = departmentMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Department>()
                            .eq(Department::getDepartmentCode, node.getParentNodeId())
            );
            if (department != null) {
                owner.setDepartmentId(department.getDepartmentId());
            }
        }
        if (StringUtils.hasText(node.getPosition())) {
            owner.setPosition(node.getPosition());
        }
        if (StringUtils.hasText(node.getContactPhone())) {
            owner.setContactPhone(node.getContactPhone());
        }
        if (StringUtils.hasText(node.getEmail())) {
            owner.setEmail(node.getEmail());
        }
        owner.setUpdatedTime(LocalDateTime.now());
        ownerMapper.updateById(owner);
    }

    /**
     * 构建组织架构树
     */
    private OrganizationNodeDTO buildOrganizationTree(List<Department> departments, Map<Long, List<Owner>> departmentOwnersMap, Map<Long, Department> departmentMap) {
        // 查找根部门
        List<Department> rootDepartments = departments.stream()
                .filter(dept -> dept.getParentId() == null)
                .sorted((d1, d2) -> d1.getSortOrder().compareTo(d2.getSortOrder()))
                .collect(Collectors.toList());

        // 构建组织架构树
        List<OrganizationNodeDTO> children = new ArrayList<>();
        for (Department rootDepartment : rootDepartments) {
            OrganizationNodeDTO node = buildDepartmentNode(rootDepartment, departmentOwnersMap, departmentMap);
            children.add(node);
        }

        OrganizationNodeDTO root = new OrganizationNodeDTO();
        root.setNodeId("ROOT");
        root.setNodeName("组织架构");
        root.setNodeType("DEPARTMENT");
        root.setChildren(children);

        return root;
    }

    /**
     * 构建部门节点
     */
    private OrganizationNodeDTO buildDepartmentNode(Department department, Map<Long, List<Owner>> departmentOwnersMap, Map<Long, Department> departmentMap) {
        OrganizationNodeDTO node = new OrganizationNodeDTO();
        node.setNodeId(department.getDepartmentCode());
        node.setNodeName(department.getDepartmentName());
        node.setNodeType("DEPARTMENT");
        node.setParentNodeId(department.getParentId() != null ? departmentMap.get(department.getParentId()).getDepartmentCode() : null);

        // 查询子部门
        List<Department> childDepartments = departmentMap.values().stream()
                .filter(dept -> dept.getParentId() != null && dept.getParentId().equals(department.getDepartmentId()))
                .sorted((d1, d2) -> d1.getSortOrder().compareTo(d2.getSortOrder()))
                .collect(Collectors.toList());

        // 构建子节点
        List<OrganizationNodeDTO> children = new ArrayList<>();
        for (Department childDepartment : childDepartments) {
            OrganizationNodeDTO childNode = buildDepartmentNode(childDepartment, departmentOwnersMap, departmentMap);
            children.add(childNode);
        }

        // 查询责任人
        List<Owner> owners = departmentOwnersMap.getOrDefault(department.getDepartmentId(), new ArrayList<>());
        for (Owner owner : owners) {
            OrganizationNodeDTO ownerNode = new OrganizationNodeDTO();
            ownerNode.setNodeId(owner.getEmployeeNo());
            ownerNode.setNodeName(owner.getName());
            ownerNode.setNodeType("POSITION");
            ownerNode.setParentNodeId(department.getDepartmentCode());
            ownerNode.setLeaderName(owner.getName());
            ownerNode.setLeaderEmployeeNo(owner.getEmployeeNo());
            ownerNode.setContactPhone(owner.getContactPhone());
            ownerNode.setEmail(owner.getEmail());
            ownerNode.setPosition(owner.getPosition());
            children.add(ownerNode);
        }

        node.setChildren(children);
        return node;
    }
}
