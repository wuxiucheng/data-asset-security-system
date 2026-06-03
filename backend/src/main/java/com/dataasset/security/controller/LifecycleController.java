package com.dataasset.security.controller;

import com.dataasset.security.common.result.Result;
import com.dataasset.security.entity.LcPolicy;
import com.dataasset.security.entity.LcStatus;
import com.dataasset.security.service.LcPolicyService;
import com.dataasset.security.service.LcStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lifecycle")
@Tag(name = "数据生命周期管理", description = "数据生命周期策略和状态管理")
public class LifecycleController {

    @Autowired
    private LcPolicyService lcPolicyService;

    @Autowired
    private LcStatusService lcStatusService;

    @GetMapping("/policy/list")
    @Operation(summary = "获取所有策略")
    public Result<List<LcPolicy>> listPolicies() {
        return Result.success(lcPolicyService.list());
    }

    @PostMapping("/policy")
    @Operation(summary = "创建策略")
    public Result<LcPolicy> createPolicy(@RequestBody LcPolicy policy) {
        lcPolicyService.save(policy);
        return Result.success(policy);
    }

    @GetMapping("/status/list")
    @Operation(summary = "获取所有状态")
    public Result<List<LcStatus>> listStatuses() {
        return Result.success(lcStatusService.list());
    }
}
