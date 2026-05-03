package com.dataasset.security.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.common.annotation.AuditLog;
import com.dataasset.security.common.enums.ObjectTypeEnum;
import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.dto.DataGradingCreateDTO;
import com.dataasset.security.dto.DataGradingUpdateDTO;
import com.dataasset.security.entity.DataGrading;
import com.dataasset.security.mapper.DataGradingMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据分级管理控制器
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@RestController
@RequestMapping("/grading")
@RequiredArgsConstructor
@Tag(name = "数据分级管理", description = "数据分级CRUD相关接口")
public class DataGradingController {

    private final DataGradingMapper dataGradingMapper;

    /**
     * 创建数据分级
     */
    @PostMapping("/create")
    @Operation(summary = "创建数据分级", description = "创建新的数据分级")
    @AuditLog(operationType = OperationTypeEnum.CREATE, objectType = ObjectTypeEnum.DATA_GRADING, description = "创建数据分级")
    public Result<Long> createGrading(@RequestBody DataGradingCreateDTO createDTO) {
        log.info("创建数据分级：{}", createDTO.getGradingName());
        try {
            DataGrading grading = new DataGrading();
            BeanUtils.copyProperties(createDTO, grading);
            grading.setStatus("ACTIVE");
            grading.setDeleted(0);
            dataGradingMapper.insert(grading);
            return Result.success("数据分级创建成功", grading.getGradingId());
        } catch (Exception e) {
            log.error("创建数据分级失败：", e);
            return Result.error("创建数据分级失败：" + e.getMessage());
        }
    }

    /**
     * 更新数据分级
     */
    @PutMapping("/update")
    @Operation(summary = "更新数据分级", description = "更新数据分级信息")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.DATA_GRADING, description = "更新数据分级信息")
    public Result<Void> updateGrading(@RequestBody DataGradingUpdateDTO updateDTO) {
        log.info("更新数据分级：{}", updateDTO.getGradingId());
        try {
            DataGrading grading = new DataGrading();
            BeanUtils.copyProperties(updateDTO, grading);
            dataGradingMapper.updateById(grading);
            return Result.success("数据分级更新成功");
        } catch (Exception e) {
            log.error("更新数据分级失败：", e);
            return Result.error("更新数据分级失败：" + e.getMessage());
        }
    }

    /**
     * 删除数据分级
     */
    @DeleteMapping("/delete/{gradingId}")
    @Operation(summary = "删除数据分级", description = "删除指定的数据分级")
    @AuditLog(operationType = OperationTypeEnum.DELETE, objectType = ObjectTypeEnum.DATA_GRADING, description = "删除数据分级")
    public Result<Void> deleteGrading(@PathVariable Long gradingId) {
        log.info("删除数据分级：{}", gradingId);
        try {
            DataGrading grading = new DataGrading();
            grading.setGradingId(gradingId);
            grading.setDeleted(1);
            dataGradingMapper.updateById(grading);
            return Result.success("数据分级删除成功");
        } catch (Exception e) {
            log.error("删除数据分级失败：", e);
            return Result.error("删除数据分级失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询数据分级
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询数据分级", description = "根据条件分页查询数据分级")
    @AuditLog(operationType = OperationTypeEnum.QUERY, objectType = ObjectTypeEnum.DATA_GRADING, description = "查询数据分级列表")
    public Result<Page<DataGrading>> queryGradings(@RequestBody java.util.Map<String, Object> params) {
        Integer current = (Integer) params.getOrDefault("current", 1);
        Integer size = (Integer) params.getOrDefault("size", 10);
        
        Page<DataGrading> pageObj = new Page<>(current, size);
        LambdaQueryWrapper<DataGrading> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataGrading::getDeleted, 0);
        wrapper.orderByAsc(DataGrading::getLevelValue);
        
        Page<DataGrading> result = dataGradingMapper.selectPage(pageObj, wrapper);
        return Result.success(result);
    }

    /**
     * 获取标准下的所有分级
     */
    @GetMapping("/standard/{standardId}")
    @Operation(summary = "获取标准下的分级", description = "获取指定标准下的所有分级")
    public Result<List<DataGrading>> getGradingsByStandardId(@PathVariable Long standardId) {
        List<DataGrading> gradings = dataGradingMapper.selectList(new LambdaQueryWrapper<DataGrading>()
            .eq(DataGrading::getStandardId, standardId)
            .eq(DataGrading::getDeleted, 0)
            .orderByAsc(DataGrading::getLevelValue));
        return Result.success(gradings);
    }
}
