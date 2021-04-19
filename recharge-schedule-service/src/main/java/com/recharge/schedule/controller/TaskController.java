package com.recharge.schedule.controller;

import com.recharge.common.entity.ResponseMessage;
import com.recharge.common.entity.Task;
import com.recharge.schedule.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Slf4j
@RestController
@RequestMapping("/task")
@Api(value = "任务调度API", tags = "Schedule")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping("/refresh")
    @ApiOperation(" 未来数据集合向消费者队列定时刷新")
    public ResponseMessage refresh() {
        try {
            taskService.refresh();
            log.info("refresh----");
            return ResponseMessage.ok("");
        } catch (Exception e) {
            log.error("refresh exception");
            return ResponseMessage.error(e.getMessage());
        }
    }

    @PostMapping("/push")
    @ApiOperation("添加任务")
    public ResponseMessage pushTask(@RequestBody Task task) {
        log.info("add task {}", task);
        try {
            // 参数校验
            Assert.notNull(task.getTaskType(), "任务类型不能为空");
            Assert.notNull(task.getPriority(), "任务优先级不能为空");
            Assert.notNull(task.getExecuteTime(), "任务执行时间不能为空");
            Long taskId = taskService.addTask(task);
            return ResponseMessage.ok(taskId);
        } catch (Exception e) {
            log.error("push task exception {}", task);
            return ResponseMessage.error(e.getMessage());
        }
    }

    @GetMapping("/poll/{type}/{priority}")
    @ApiOperation("消费任务")
    public ResponseMessage pollTask(@PathVariable("type") Integer type, @PathVariable("priority") Integer priority) {
        log.info("poll task {},{}", type, priority);
        try {
            Assert.notNull(type, "任务类型不能为空");
            Assert.notNull(priority, "任务优先级不能为空");
            Task task = taskService.poll(type, priority);
            return ResponseMessage.ok(task);
        } catch (Exception e) {
            log.error("poll task exception {}, {}", type, priority);
            return ResponseMessage.error(e.getMessage());
        }
    }

    @PostMapping("/cancel")
    @ApiOperation("取消任务")
    public ResponseMessage cancelTask(@RequestParam("taskId") Long taskId) {
        log.info("cancel task {}", taskId);
        try {
            Assert.notNull(taskId, "任务id不能为空");
            boolean success = taskService.cancelTask(taskId);
            return ResponseMessage.ok(success);
        } catch (Exception e) {
            log.error("cancel task exception {}", taskId);
            return ResponseMessage.error(e.getMessage());
        }
    }
}
