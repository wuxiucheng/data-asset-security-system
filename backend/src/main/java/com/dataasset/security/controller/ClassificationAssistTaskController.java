package com.dataasset.security.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.entity.ClassificationAssistTask;
import com.dataasset.security.service.ClassificationAssistTaskService;
import com.dataasset.security.service.TaskExecutionEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/classificationAssistTask")
public class ClassificationAssistTaskController {

    @Autowired
    private ClassificationAssistTaskService taskService;

    @Autowired
    private TaskExecutionEngine executionEngine;

    /**
     * 分页查询任务
     */
    @PostMapping("/page")
    public Result<Page<ClassificationAssistTask>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String taskName,
            @RequestParam(required = false) String status) {
        return Result.success(taskService.page(current, size, taskName, status));
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/{taskId}")
    public Result<ClassificationAssistTask> getById(@PathVariable Long taskId) {
        return Result.success(taskService.getById(taskId));
    }

    /**
     * 创建任务
     */
    @PostMapping
    public Result<Boolean> create(@RequestBody ClassificationAssistTask task) {
        task.setStatus("PENDING");
        task.setDeleted(0);
        return Result.success(taskService.save(task));
    }

    /**
     * 更新任务
     */
    @PutMapping
    public Result<Boolean> update(@RequestBody ClassificationAssistTask task) {
        return Result.success(taskService.updateById(task));
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/{taskId}")
    public Result<Boolean> delete(@PathVariable Long taskId) {
        return Result.success(taskService.removeById(taskId));
    }

    /**
     * 执行任务（异步）
     */
    @PostMapping("/{taskId}/execute")
    public Result<Boolean> execute(@PathVariable Long taskId) {
        // 异步执行任务
        executionEngine.executeTask(taskId);
        return Result.success(true);
    }

    /**
     * 取消任务
     */
    @PostMapping("/{taskId}/cancel")
    public Result<Boolean> cancel(@PathVariable Long taskId) {
        ClassificationAssistTask task = taskService.getById(taskId);
        if (task != null && "RUNNING".equals(task.getStatus())) {
            task.setStatus("CANCELLED");
            taskService.updateById(task);
            return Result.success(true);
        }
        return Result.error("任务状态不允许取消");
    }
}
