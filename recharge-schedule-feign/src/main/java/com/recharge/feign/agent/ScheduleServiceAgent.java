package com.recharge.feign.agent;

import com.recharge.common.entity.ResponseMessage;
import com.recharge.common.entity.Task;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@FeignClient("schedule-service")
public interface ScheduleServiceAgent {
    /**
     * 定时刷新
     *
     * @return
     */
    @GetMapping("/schedule/task/refresh")
    ResponseMessage refresh();

    /**
     * 添加任务
     *
     * @param task
     * @return
     */
    @PostMapping("/schedule/task/push")
    ResponseMessage push(@RequestBody Task task);

    /**
     * 任务消费
     *
     * @param type
     * @param priority
     * @return
     */
    @GetMapping("/schedule/task/poll/{type}/{priority}")
    ResponseMessage poll(@PathVariable("type") Integer type, @PathVariable("priority") Integer priority);

    /**
     * 取消任务
     *
     * @param taskId
     * @return
     */
    @PostMapping("/schedule/task/cancel")
    ResponseMessage cancel(@RequestParam("taskId") Long taskId);
}
