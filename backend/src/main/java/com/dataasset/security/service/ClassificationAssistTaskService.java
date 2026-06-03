package com.dataasset.security.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.entity.ClassificationAssistTask;
import com.dataasset.security.mapper.ClassificationAssistTaskMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClassificationAssistTaskService extends ServiceImpl<ClassificationAssistTaskMapper, ClassificationAssistTask> {

    /**
     * 分页查询任务
     */
    public Page<ClassificationAssistTask> page(int current, int size, String taskName, String status) {
        Page<ClassificationAssistTask> page = new Page<>(current, size);
        LambdaQueryWrapper<ClassificationAssistTask> wrapper = new LambdaQueryWrapper<>();
        
        if (taskName != null && !taskName.isEmpty()) {
            wrapper.like(ClassificationAssistTask::getTaskName, taskName);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(ClassificationAssistTask::getStatus, status);
        }
        
        wrapper.orderByDesc(ClassificationAssistTask::getCreatedTime);
        return this.page(page, wrapper);
    }

    /**
     * 获取待执行的任务
     */
    public List<ClassificationAssistTask> getPendingTasks() {
        LambdaQueryWrapper<ClassificationAssistTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassificationAssistTask::getStatus, "PENDING")
               .eq(ClassificationAssistTask::getDeleted, 0);
        return this.list(wrapper);
    }

    /**
     * 开始执行任务
     */
    public void startTask(Long taskId) {
        ClassificationAssistTask task = this.getById(taskId);
        if (task != null) {
            task.setStatus("RUNNING");
            task.setStartTime(LocalDateTime.now());
            this.updateById(task);
        }
    }

    /**
     * 完成任务
     */
    public void completeTask(Long taskId, int totalCount, int matchedCount) {
        ClassificationAssistTask task = this.getById(taskId);
        if (task != null) {
            task.setStatus("COMPLETED");
            task.setEndTime(LocalDateTime.now());
            task.setTotalCount(totalCount);
            task.setMatchedCount(matchedCount);
            this.updateById(task);
        }
    }

    /**
     * 任务失败
     */
    public void failTask(Long taskId, String error) {
        ClassificationAssistTask task = this.getById(taskId);
        if (task != null) {
            task.setStatus("FAILED");
            task.setEndTime(LocalDateTime.now());
            this.updateById(task);
        }
    }
}
