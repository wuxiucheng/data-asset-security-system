package com.dataasset.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.common.exception.BusinessException;
import com.dataasset.security.common.exception.ResourceNotFoundException;
import com.dataasset.security.dto.DataAssetCreateDTO;
import com.dataasset.security.dto.DataAssetQueryDTO;
import com.dataasset.security.dto.DataAssetUpdateDTO;
import com.dataasset.security.entity.DataAsset;
import com.dataasset.security.entity.DataSourceConfig;
import com.dataasset.security.entity.Department;
import com.dataasset.security.entity.Owner;
import com.dataasset.security.mapper.DataAssetMapper;
import com.dataasset.security.mapper.DataSourceConfigMapper;
import com.dataasset.security.mapper.DepartmentMapper;
import com.dataasset.security.mapper.OwnerMapper;
import com.dataasset.security.service.DataAssetService;
import com.dataasset.security.service.DynamicDatabaseQueryService;
import com.dataasset.security.vo.DataAssetVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 数据资产管理Service实现类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataAssetServiceImpl extends ServiceImpl<DataAssetMapper, DataAsset> implements DataAssetService {

    private final DepartmentMapper departmentMapper;
    private final OwnerMapper ownerMapper;
    private final DataSourceConfigMapper dataSourceConfigMapper;
    private final DynamicDatabaseQueryService dynamicDatabaseQueryService;

    // 并发控制：正在刷新中的资产ID集合
    private final ConcurrentHashMap<Long, Boolean> refreshingAssets = new ConcurrentHashMap<>();

    // 批量刷新任务存储
    private final ConcurrentHashMap<String, Map<String, Object>> batchRefreshTasks = new ConcurrentHashMap<>();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDataAsset(DataAssetCreateDTO createDTO) {
        // 检查资产编码是否已存在
        DataAsset existAsset = this.lambdaQuery()
                .eq(DataAsset::getAssetCode, createDTO.getAssetCode())
                .one();
        if (existAsset != null) {
            throw new BusinessException("资产编码已存在");
        }

        // 检查部门是否存在
        if (createDTO.getDepartmentId() != null) {
            Department department = departmentMapper.selectById(createDTO.getDepartmentId());
            if (department == null) {
                throw new BusinessException("责任部门不存在");
            }
        }

        // 检查责任人是否存在
        if (createDTO.getOwnerId() != null) {
            Owner owner = ownerMapper.selectById(createDTO.getOwnerId());
            if (owner == null) {
                throw new BusinessException("责任人不存在");
            }
        }

        // 创建数据资产
        DataAsset asset = new DataAsset();
        asset.setAssetName(createDTO.getAssetName());
        asset.setAssetCode(createDTO.getAssetCode());
        asset.setAssetType(createDTO.getAssetType());
        asset.setSystemName(createDTO.getSystemName());
        asset.setDatabaseType(createDTO.getDatabaseType());
        asset.setDatabaseHost(createDTO.getDatabaseHost());
        asset.setDatabasePort(createDTO.getDatabasePort());
        asset.setDatabaseName(createDTO.getDatabaseName());
        asset.setTableName(createDTO.getTableName());
        asset.setAssetDescription(createDTO.getAssetDescription());
        asset.setDepartmentId(createDTO.getDepartmentId());
        asset.setOwnerId(createDTO.getOwnerId());
        asset.setClassificationId(createDTO.getClassificationId());
        asset.setGradingId(createDTO.getGradingId());
        asset.setDataVolumeLevel(createDTO.getDataVolumeLevel());
        asset.setAccessFrequency(createDTO.getAccessFrequency());
        asset.setImportanceLevel(createDTO.getImportanceLevel());
        asset.setContainsSensitiveData(createDTO.getContainsSensitiveData());
        asset.setSensitiveDataType(createDTO.getSensitiveDataType());
        asset.setStatus("DRAFT");

        // 处理过期时间
        if (StringUtils.hasText(createDTO.getExpireTime())) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                asset.setExpireTime(LocalDateTime.parse(createDTO.getExpireTime(), formatter));
            } catch (Exception e) {
                throw new BusinessException("过期时间格式不正确，应为 yyyy-MM-dd HH:mm:ss");
            }
        }

        asset.setRemarks(createDTO.getRemarks());
        asset.setCreatedTime(LocalDateTime.now());
        asset.setUpdatedTime(LocalDateTime.now());

        // TODO: 设置创建人ID（需要从登录用户获取）
        // asset.setCreatorId(currentUser.getUserId());

        this.save(asset);
        log.info("数据资产创建成功：{}", asset.getAssetName());

        return asset.getAssetId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDataAsset(DataAssetUpdateDTO updateDTO) {
        // 检查资产是否存在
        DataAsset asset = this.getById(updateDTO.getAssetId());
        if (asset == null) {
            throw new ResourceNotFoundException("数据资产不存在");
        }

        // 检查部门是否存在
        if (updateDTO.getDepartmentId() != null) {
            Department department = departmentMapper.selectById(updateDTO.getDepartmentId());
            if (department == null) {
                throw new BusinessException("责任部门不存在");
            }
        }

        // 检查责任人是否存在
        if (updateDTO.getOwnerId() != null) {
            Owner owner = ownerMapper.selectById(updateDTO.getOwnerId());
            if (owner == null) {
                throw new BusinessException("责任人不存在");
            }
        }

        // 更新资产信息 - 使用BeanUtils覆盖所有字段
        org.springframework.beans.BeanUtils.copyProperties(updateDTO, asset);
        // 确保ID不被覆盖
        asset.setAssetId(updateDTO.getAssetId());

        asset.setUpdatedTime(LocalDateTime.now());

        // TODO: 设置更新人ID（需要从登录用户获取）
        // asset.setUpdaterId(currentUser.getUserId());

        this.updateById(asset);
        log.info("数据资产更新成功：{}", asset.getAssetName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDataAsset(Long assetId) {
        // 检查资产是否存在
        DataAsset asset = this.getById(assetId);
        if (asset == null) {
            throw new ResourceNotFoundException("数据资产不存在");
        }

        // 删除资产（逻辑删除）
        this.removeById(assetId);
        log.info("数据资产删除成功：{}", asset.getAssetName());
    }

    @Override
    public Page<DataAssetVO> queryDataAssets(DataAssetQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<DataAsset> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getAssetName())) {
            wrapper.like(DataAsset::getAssetName, queryDTO.getAssetName());
        }
        if (StringUtils.hasText(queryDTO.getAssetCode())) {
            wrapper.like(DataAsset::getAssetCode, queryDTO.getAssetCode());
        }
        if (StringUtils.hasText(queryDTO.getAssetType())) {
            wrapper.eq(DataAsset::getAssetType, queryDTO.getAssetType());
        }
        if (StringUtils.hasText(queryDTO.getSystemName())) {
            wrapper.like(DataAsset::getSystemName, queryDTO.getSystemName());
        }
        if (queryDTO.getDepartmentId() != null) {
            wrapper.eq(DataAsset::getDepartmentId, queryDTO.getDepartmentId());
        }
        if (queryDTO.getOwnerId() != null) {
            wrapper.eq(DataAsset::getOwnerId, queryDTO.getOwnerId());
        }
        if (queryDTO.getClassificationId() != null) {
            wrapper.eq(DataAsset::getClassificationId, queryDTO.getClassificationId());
        }
        if (queryDTO.getGradingId() != null) {
            wrapper.eq(DataAsset::getGradingId, queryDTO.getGradingId());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(DataAsset::getStatus, queryDTO.getStatus());
        }
        if (StringUtils.hasText(queryDTO.getDatabaseType())) {
            wrapper.eq(DataAsset::getDatabaseType, queryDTO.getDatabaseType());
        }
        if (StringUtils.hasText(queryDTO.getDatabaseHost())) {
            wrapper.like(DataAsset::getDatabaseHost, queryDTO.getDatabaseHost());
        }
        if (StringUtils.hasText(queryDTO.getDatabaseName())) {
            wrapper.like(DataAsset::getDatabaseName, queryDTO.getDatabaseName());
        }
        if (queryDTO.getContainsSensitiveData() != null) {
            wrapper.eq(DataAsset::getContainsSensitiveData, queryDTO.getContainsSensitiveData());
        }
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(DataAsset::getCreatedTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(DataAsset::getCreatedTime, queryDTO.getEndTime());
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc(DataAsset::getCreatedTime);

        // 分页查询
        Page<DataAsset> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<DataAsset> assetPage = this.page(page, wrapper);

        // 转换为VO
        Page<DataAssetVO> voPage = new Page<>(assetPage.getCurrent(), assetPage.getSize(), assetPage.getTotal());
        voPage.setRecords(assetPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());

        return voPage;
    }

    @Override
    public DataAssetVO getDataAssetDetail(Long assetId) {
        DataAsset asset = this.getById(assetId);
        if (asset == null) {
            throw new ResourceNotFoundException("数据资产不存在");
        }

        return convertToVO(asset);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDataAssetStatus(Long assetId, String status) {
        // 检查资产是否存在
        DataAsset asset = this.getById(assetId);
        if (asset == null) {
            throw new ResourceNotFoundException("数据资产不存在");
        }

        // 更新资产状态
        asset.setStatus(status);
        asset.setUpdatedTime(LocalDateTime.now());
        this.updateById(asset);
        log.info("数据资产状态更新成功：{} -> {}", asset.getAssetName(), status);
    }

    @Override
    public List<DataAssetVO> getDataAssetsByDepartmentId(Long departmentId) {
        // 检查部门是否存在
        Department department = departmentMapper.selectById(departmentId);
        if (department == null) {
            throw new ResourceNotFoundException("部门不存在");
        }

        // 查询部门数据资产
        List<DataAsset> assets = this.lambdaQuery()
                .eq(DataAsset::getDepartmentId, departmentId)
                .eq(DataAsset::getStatus, "ACTIVE")
                .orderByDesc(DataAsset::getCreatedTime)
                .list();
        return assets.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataAssetVO> getDataAssetsByOwnerId(Long ownerId) {
        // 检查责任人是否存在
        Owner owner = ownerMapper.selectById(ownerId);
        if (owner == null) {
            throw new ResourceNotFoundException("责任人不存在");
        }

        // 查询责任人数据资产
        List<DataAsset> assets = this.lambdaQuery()
                .eq(DataAsset::getOwnerId, ownerId)
                .eq(DataAsset::getStatus, "ACTIVE")
                .orderByDesc(DataAsset::getCreatedTime)
                .list();
        return assets.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataAssetVO> getDataAssetsByClassificationId(Long classificationId) {
        // 查询分类数据资产
        List<DataAsset> assets = this.lambdaQuery()
                .eq(DataAsset::getClassificationId, classificationId)
                .eq(DataAsset::getStatus, "ACTIVE")
                .orderByDesc(DataAsset::getCreatedTime)
                .list();
        return assets.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataAssetVO> getDataAssetsByGradingId(Long gradingId) {
        // 查询分级数据资产
        List<DataAsset> assets = this.lambdaQuery()
                .eq(DataAsset::getGradingId, gradingId)
                .eq(DataAsset::getStatus, "ACTIVE")
                .orderByDesc(DataAsset::getCreatedTime)
                .list();
        return assets.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private DataAssetVO convertToVO(DataAsset asset) {
        DataAssetVO vo = new DataAssetVO();
        vo.setAssetId(asset.getAssetId());
        vo.setAssetName(asset.getAssetName());
        vo.setAssetCode(asset.getAssetCode());
        vo.setAssetType(asset.getAssetType());
        vo.setSystemName(asset.getSystemName());
        vo.setDatabaseType(asset.getDatabaseType());
        vo.setDatabaseHost(asset.getDatabaseHost());
        vo.setDatabasePort(asset.getDatabasePort());
        vo.setDatabaseName(asset.getDatabaseName());
        vo.setTableName(asset.getTableName());
        vo.setDataSourceId(asset.getDataSourceId());
        vo.setAssetDescription(asset.getAssetDescription());
        vo.setDepartmentId(asset.getDepartmentId());
        vo.setOwnerId(asset.getOwnerId());
        vo.setClassificationId(asset.getClassificationId());
        vo.setGradingId(asset.getGradingId());
        vo.setSensitivityScore(asset.getSensitivityScore());
        vo.setDataVolumeLevel(asset.getDataVolumeLevel());
        vo.setAccessFrequency(asset.getAccessFrequency());
        vo.setImportanceLevel(asset.getImportanceLevel());
        vo.setStatus(asset.getStatus());
        vo.setContainsSensitiveData(asset.getContainsSensitiveData());
        vo.setSensitiveDataType(asset.getSensitiveDataType());
        vo.setLastScanTime(asset.getLastScanTime());
        vo.setLastScanResult(asset.getLastScanResult());
        vo.setExpireTime(asset.getExpireTime());
        vo.setRowCount(asset.getRowCount());
        vo.setRemarks(asset.getRemarks());
        vo.setCreatorId(asset.getCreatorId());
        vo.setUpdaterId(asset.getUpdaterId());
        vo.setCreatedTime(asset.getCreatedTime());
        vo.setUpdatedTime(asset.getUpdatedTime());

        // 查询部门名称
        if (asset.getDepartmentId() != null) {
            Department department = departmentMapper.selectById(asset.getDepartmentId());
            if (department != null) {
                vo.setDepartmentName(department.getDepartmentName());
            }
        }

        // 查询责任人姓名
        if (asset.getOwnerId() != null) {
            Owner owner = ownerMapper.selectById(asset.getOwnerId());
            if (owner != null) {
                vo.setOwnerName(owner.getName());
            }
        }

        // 查询数据源名称
        if (asset.getDataSourceId() != null) {
            DataSourceConfig dsConfig = dataSourceConfigMapper.selectById(asset.getDataSourceId());
            if (dsConfig != null) {
                vo.setDataSourceName(dsConfig.getDataSourceName());
            }
        }

        return vo;
    }

    @Override
    public Map<String, Object> refreshAssetRowCount(Long assetId) {
        // 并发控制
        if (refreshingAssets.putIfAbsent(assetId, true) != null) {
            throw new BusinessException("该资产正在刷新中，请稍后");
        }

        try {
            DataAsset asset = this.getById(assetId);
            if (asset == null) {
                throw new ResourceNotFoundException("资产不存在");
            }

            // 校验：DATABASE 或 TABLE 类型才支持刷新数据条数
            if (asset.getAssetType() == null
                    || (!asset.getAssetType().equals("DATABASE") && !asset.getAssetType().equals("TABLE"))) {
                throw new BusinessException("仅数据库/表类型资产支持查询数据条数");
            }

            // 必须有表名
            if (asset.getTableName() == null || asset.getTableName().isEmpty()) {
                throw new BusinessException("资产缺少表名，无法查询数据条数");
            }

            // 获取数据库连接信息：优先从关联的数据源配置获取（含凭证），否则从资产自身字段获取
            String dbType = asset.getDatabaseType();
            String host = asset.getDatabaseHost();
            int port = asset.getDatabasePort() != null ? asset.getDatabasePort() : 0;
            String dbName = asset.getDatabaseName();
            String username = null;
            String password = null;

            if (asset.getDataSourceId() != null) {
                DataSourceConfig dsConfig = dataSourceConfigMapper.selectById(asset.getDataSourceId());
                if (dsConfig != null && "ACTIVE".equals(dsConfig.getStatus())) {
                    dbType = dsConfig.getDatabaseType();
                    host = dsConfig.getHost();
                    port = dsConfig.getPort();
                    dbName = dsConfig.getDatabaseName();
                    username = dsConfig.getUsername();
                    password = dsConfig.getPassword();
                }
            }

            if (!StringUtils.hasText(host) || port == 0
                    || !StringUtils.hasText(dbName) || !StringUtils.hasText(asset.getTableName())) {
                throw new BusinessException("资产缺少数据库连接信息，无法查询数据条数。请配置数据源或补充连接信息。");
            }

            // 执行COUNT查询
            long rowCount = dynamicDatabaseQueryService.queryTableRowCount(
                    dbType, host, port, dbName, username, password, asset.getTableName()
            );

            // 更新rowCount字段
            asset.setRowCount(rowCount);
            asset.setUpdatedTime(LocalDateTime.now());
            this.updateById(asset);

            log.info("资产数据条数刷新成功：{}，rowCount={}", asset.getAssetName(), rowCount);

            return Map.of("assetId", assetId, "rowCount", rowCount);
        } finally {
            refreshingAssets.remove(assetId);
        }
    }

    @Override
    public String submitBatchRefreshTask(List<Long> assetIds, String refreshScope) {
        String taskId = "task_" + System.currentTimeMillis();

        Map<String, Object> task = new ConcurrentHashMap<>();
        task.put("taskId", taskId);
        task.put("status", "RUNNING");
        task.put("totalCount", assetIds.size());
        task.put("completedCount", 0);
        task.put("currentAssetName", "");
        task.put("results", new java.util.ArrayList<Map<String, Object>>());
        batchRefreshTasks.put(taskId, task);

        // 异步执行批量刷新
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            List<Map<String, Object>> results = (List<Map<String, Object>>) task.get("results");
            for (int i = 0; i < assetIds.size(); i++) {
                Long id = assetIds.get(i);
                try {
                    DataAsset asset = this.getById(id);
                    if (asset != null) {
                        task.put("currentAssetName", asset.getAssetName());

                        // 获取连接信息：优先从数据源配置获取
                        String dbType = asset.getDatabaseType();
                        String host = asset.getDatabaseHost();
                        int port = asset.getDatabasePort() != null ? asset.getDatabasePort() : 0;
                        String dbName = asset.getDatabaseName();
                        String username = null;
                        String password = null;

                        if (asset.getDataSourceId() != null) {
                            DataSourceConfig dsConfig = dataSourceConfigMapper.selectById(asset.getDataSourceId());
                            if (dsConfig != null && "ACTIVE".equals(dsConfig.getStatus())) {
                                dbType = dsConfig.getDatabaseType();
                                host = dsConfig.getHost();
                                port = dsConfig.getPort();
                                dbName = dsConfig.getDatabaseName();
                                username = dsConfig.getUsername();
                                password = dsConfig.getPassword();
                            }
                        }

                        long rowCount = dynamicDatabaseQueryService.queryTableRowCount(
                                dbType, host, port, dbName, username, password, asset.getTableName()
                        );
                        asset.setRowCount(rowCount);
                        asset.setUpdatedTime(LocalDateTime.now());
                        this.updateById(asset);

                        Map<String, Object> result = new java.util.HashMap<>();
                        result.put("assetId", id);
                        result.put("assetName", asset.getAssetName());
                        result.put("status", "SUCCESS");
                        result.put("rowCount", rowCount);
                        result.put("errorMessage", null);
                        results.add(result);
                    } else {
                        Map<String, Object> result = new java.util.HashMap<>();
                        result.put("assetId", id);
                        result.put("assetName", "");
                        result.put("status", "FAILED");
                        result.put("rowCount", null);
                        result.put("errorMessage", "资产不存在");
                        results.add(result);
                    }
                } catch (Exception e) {
                    Map<String, Object> result = new java.util.HashMap<>();
                    result.put("assetId", id);
                    result.put("assetName", "");
                    result.put("status", "FAILED");
                    result.put("rowCount", null);
                    result.put("errorMessage", e.getMessage());
                    results.add(result);
                }
                task.put("completedCount", i + 1);
            }
            task.put("status", "COMPLETED");
        });

        return taskId;
    }

    @Override
    public Map<String, Object> getBatchRefreshProgress(String taskId) {
        Map<String, Object> task = batchRefreshTasks.get(taskId);
        if (task == null) {
            throw new ResourceNotFoundException("任务不存在");
        }
        return task;
    }
}
