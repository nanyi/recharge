package com.recharge.supplier.service;

import com.alibaba.fastjson.JSON;
import com.recharge.common.cache.CacheService;
import com.recharge.common.entity.Constants;
import com.recharge.common.entity.ResponseMessage;
import com.recharge.common.entity.Task;
import com.recharge.common.enums.TaskTypeEnum;
import com.recharge.common.recharge.RechargeRequest;
import com.recharge.common.utils.ProtostuffUtil;
import com.recharge.feign.agent.ScheduleServiceAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Slf4j
@Service
public class SupplierTaskImpl implements SupplierTask {
    @Autowired
    private ScheduleServiceAgent scheduleServiceAgent;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private CacheService cacheService;

    @Override
    public void cancelCheckTask(String orderNo) {
        String taskId = (String) cacheService.hGet(Constants.ORDER_CHECKED, orderNo);
        if (taskId != null) {
            scheduleServiceAgent.cancel(Long.valueOf(taskId));
            cacheService.hDelete(Constants.ORDER_CHECKED, orderNo);
        }
    }

    @Override
    @Scheduled(fixedRate = 1000)
    public void rechargeException() {
        retry(TaskTypeEnum.REMOTE_ERROR);
    }

    @Override
    @Scheduled(fixedRate = 1000)
    public void retryRecharge() {
        retry(TaskTypeEnum.ORDER_REQ_FAILED);
    }

    @Override
    @Scheduled(fixedRate = 1000)
    public void roundRecharge() {
        retry(TaskTypeEnum.BALANCE_NOT_ENOUGH);
    }

    private void retry(TaskTypeEnum taskTypeEnum) {
        ResponseMessage responseMessage = scheduleServiceAgent.poll(taskTypeEnum.getTaskType(), taskTypeEnum.getPriority());
        if (responseMessage.isFlag()) {
            if (responseMessage.getData() != null) {
                String taskStr = JSON.toJSONString(responseMessage.getData());
                Task task = JSON.parseObject(taskStr, Task.class);
                RechargeRequest rechargeRequest = ProtostuffUtil.deserialize(task.getParameters(), RechargeRequest.class);
                rechargeRequest.setRepeat(rechargeRequest.getRepeat() + 1);
                log.info("消费{}任务, {}", taskTypeEnum.getDesc(), rechargeRequest);
                supplierService.recharge(rechargeRequest);
            }
        }
    }

    @Override
    public void addRetryTask(RechargeRequest rechargeRequest) {
        Task task = new Task();
        TaskTypeEnum taskTypeEnum = TaskTypeEnum.getTaskType(rechargeRequest.getErrorCode());
        assert taskTypeEnum != null;
        task.setTaskType(taskTypeEnum.getTaskType());
        task.setPriority(taskTypeEnum.getPriority());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1);
        task.setExecuteTime(calendar.getTimeInMillis());

        // protostuff 序列化优化
        task.setParameters(ProtostuffUtil.serialize(rechargeRequest));
        log.info("调用延迟任务系统添加 {} 任务: {} ", taskTypeEnum.getDesc(), task);
        scheduleServiceAgent.push(task);
    }
}
