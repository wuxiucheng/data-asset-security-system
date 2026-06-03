package com.dataasset.security.controller;

import com.dataasset.security.common.result.Result;
import com.dataasset.security.entity.DataStandard;
import com.dataasset.security.entity.ComplianceClause;
import com.dataasset.security.entity.GovernanceKpi;
import com.dataasset.security.service.DataStandardService;
import com.dataasset.security.service.ComplianceClauseService;
import com.dataasset.security.service.GovernanceKpiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compliance")
@Tag(name = "数据标准与合规", description = "数据标准、合规条款和治理KPI管理")
public class ComplianceController {

    @Autowired
    private DataStandardService dataStandardService;

    @Autowired
    private ComplianceClauseService complianceClauseService;

    @Autowired
    private GovernanceKpiService governanceKpiService;

    @GetMapping("/standard/list")
    @Operation(summary = "获取所有数据标准")
    public Result<List<DataStandard>> listStandards() {
        return Result.success(dataStandardService.list());
    }

    @PostMapping("/standard")
    @Operation(summary = "创建数据标准")
    public Result<DataStandard> createStandard(@RequestBody DataStandard standard) {
        dataStandardService.save(standard);
        return Result.success(standard);
    }

    @GetMapping("/clause/list")
    @Operation(summary = "获取所有合规条款")
    public Result<List<ComplianceClause>> listClauses() {
        return Result.success(complianceClauseService.list());
    }

    @PostMapping("/clause")
    @Operation(summary = "创建合规条款")
    public Result<ComplianceClause> createClause(@RequestBody ComplianceClause clause) {
        complianceClauseService.save(clause);
        return Result.success(clause);
    }

    @GetMapping("/kpi/list")
    @Operation(summary = "获取所有治理KPI")
    public Result<List<GovernanceKpi>> listKpis() {
        return Result.success(governanceKpiService.list());
    }

    @PostMapping("/kpi")
    @Operation(summary = "创建治理KPI")
    public Result<GovernanceKpi> createKpi(@RequestBody GovernanceKpi kpi) {
        governanceKpiService.save(kpi);
        return Result.success(kpi);
    }
}
