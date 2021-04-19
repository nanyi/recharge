package com.recharge.schedule.service;

import com.recharge.common.entity.Task;
import com.recharge.common.exception.ScheduleSystemException;
import com.recharge.common.exception.TaskNotExistException;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
public interface TaskService {

    /**
     * 未来数据集合向消费者队列定时刷新
     */
    void refresh();

    /**
     * 按照类型和优先级获取任务数量
     *
     * @param type
     * @param priority
     * @return
     */
    long size(int type, int priority);

    /**
     * 按照类型和优先级来拉取任务
     *
     * @param type
     * @param priority
     * @return
     * @throws TaskNotExistException
     */
    Task poll(int type, int priority) throws TaskNotExistException;

    /**
     * 添加任务
     *
     * @param task 任务对象
     * @return 任务id
     * @throws ScheduleSystemException
     */
    long addTask(Task task) throws ScheduleSystemException;

    /**
     * 取消任务
     *
     * @param taskId 任务id
     * @return 取消结果
     * @throws TaskNotExistException
     */
    boolean cancelTask(long taskId) throws TaskNotExistException;
}
