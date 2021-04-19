package com.recharge.schedule.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.recharge.common.cache.CacheService;
import com.recharge.common.entity.Constants;
import com.recharge.common.entity.Task;
import com.recharge.common.exception.ScheduleSystemException;
import com.recharge.common.exception.TaskNotExistException;
import com.recharge.schedule.config.SystemParamsConfiguration;
import com.recharge.schedule.entity.TaskInfo;
import com.recharge.schedule.entity.TaskInfoLogs;
import com.recharge.schedule.mapper.TaskInfoLogsMapper;
import com.recharge.schedule.mapper.TaskInfoMapper;
import com.recharge.schedule.zookeeper.SelectMaster;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Slf4j
@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskInfoMapper taskMapper;
    @Autowired
    private TaskInfoLogsMapper taskLogMapper;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private SystemParamsConfiguration systemParamsConfiguration;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    @Autowired
    private SelectMaster selectMaster;

    @Resource(name = "visiableThreadPool")
    private ThreadPoolTaskExecutor threadPool;

    private long nextScheduleTime;

    @PostConstruct
    private void syncData() {
        // 注册抢占主节点
        selectMaster.selectMaster(Constants.SCHEDULE_LEADER_PATH);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        threadPoolTaskScheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // 只有主节点才能去恢复数据
                if (selectMaster.checkMaster(Constants.SCHEDULE_LEADER_PATH)) {
                    log.info("schedule-service主节点进行数据恢复");
                    reloadData();
                } else {
                    log.info("schedule-service从节点备用");
                }
            }
        }, TimeUnit.MINUTES.toMillis(systemParamsConfiguration.getReload()));
    }

    private void reloadData() {
        log.info("reload data");

        // 清除缓存中原有的数据
        clearCache();
        QueryWrapper<TaskInfo> wrapper = new QueryWrapper<>();
        wrapper.select("task_type", "priority");
        wrapper.groupBy("task_type", "priority");
        List<Map<String, Object>> maps = taskMapper.selectMaps(wrapper);

        long start = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(maps.size());

        // 获取未来5分钟时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, systemParamsConfiguration.getReload());
        nextScheduleTime = calendar.getTimeInMillis();
        // 将时间放入缓存
        cacheService.set(Constants.NEXT_SCHEDULE_TIME, nextScheduleTime + "");

        for (Map<String, Object> map : maps) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    long start = System.currentTimeMillis();

                    String taskType = String.valueOf(map.get("task_type"));
                    String priority = String.valueOf(map.get("priority"));

                    List<TaskInfo> allTaskInfo = taskMapper.queryFutureTime(Integer.parseInt(taskType), Integer.parseInt(priority), calendar.getTime());
                    for (TaskInfo taskInfo : allTaskInfo) {
                        Task task = new Task();
                        // 属性拷贝
                        BeanUtils.copyProperties(taskInfo, task);
                        task.setExecuteTime(taskInfo.getExecuteTime().getTime());
                        // 放入缓存
                        addTaskToCache(task);
                    }

                    latch.countDown();
                    // 追踪每个分组线程的信息
                    log.info("线程-{}, 计数器-{}, 每组恢复耗时-{}", Thread.currentThread().getName(), latch.getCount(), System.currentTimeMillis() - start);
                }
            });
        }

        // 阻塞当前线程, 等待线程池线程返回
        try {
            latch.await(1, TimeUnit.MINUTES);
            log.info("数据恢复完成, 共耗时:" + (System.currentTimeMillis() - start) + "毫秒");
        } catch (InterruptedException e) {
            log.error("数据恢复失败, 失败原因{}", e.getMessage());
        }
    }

    @Override
    public void refresh() {
        log.info("refresh time {}", System.currentTimeMillis() / 1000);

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                // 从未来数据集合中获取所有的key
                Set<String> keys = cacheService.scan(Constants.FUTURE + "*");// future_*
                for (String key : keys) {
                    // key= future_1001_3
                    String topicKey = Constants.TOPIC + key.split(Constants.FUTURE)[1];
                    // 获取当前key的任务数据集合
                    Set<String> values = cacheService.zRangeByScore(key, 0, System.currentTimeMillis());
                    // 将每个任务数据添加到消费者队列并从未来数据集合中删除
                    if (!values.isEmpty()) {
                        cacheService.refreshWithPipeline(key, topicKey, values);
                        log.info("flash " + key + "to " + topicKey + " successfully.");
                    }
                }
            }
        });
    }

    private void clearCache() {
        // 获取未来数据集合所有的key
        // future_*
        Set<String> futureKeys = cacheService.scan(Constants.FUTURE + "*");
        cacheService.delete(futureKeys);

        // 获取消费者队列所有的key
        // topic_*
        Set<String> topicKeys = cacheService.scan(Constants.TOPIC + "*");
        cacheService.delete(topicKeys);
    }

    @Override
    public long size(int type, int priority) {
        // 任务数量=未来数据集合中的任务数量+消费者队列中的数量
        String key = type + "_" + priority;
        Set<String> zRangeAll = cacheService.zRangeAll(Constants.FUTURE + key);
        Long len = cacheService.lLen(Constants.TOPIC + key);
        return zRangeAll.size() + len;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Task poll(int type, int priority) throws TaskNotExistException {
        Future<Task> future = threadPool.submit(new Callable<Task>() {
            @Override
            public Task call() throws Exception {
                Task task = null;
                // 从消费者队列中获取任务
                String key = type + "_" + priority;
                String taskJson = cacheService.lLeftPop(Constants.TOPIC + key);
                if (!StringUtils.isEmpty(taskJson)) {
                    task = JSON.parseObject(taskJson, Task.class);

                    // 更新数据库信息
                    updateDb(task.getTaskId(), Constants.EXECUTED);
                }
                return task;
            }
        });

        // 获取线程返回结果
        Task task = null;
        try {
            task = future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("poll task exception");
            throw new TaskNotExistException(e);
        }

        return task;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public long addTask(Task task) throws ScheduleSystemException {
        /**
         * addTaskToDb()
         * 成功之后添加到缓存addTaskToCache();
         */
        Future<Long> future = threadPool.submit(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                boolean success = addTaskToDb(task);
                if (success) {
                    addTaskToCache(task);
                }
                return task.getTaskId();
            }
        });
        long taskId = -1;
        // 获取结果
        try {
            taskId = future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("add task exception");
            throw new ScheduleSystemException(e);
        }
        return taskId;
    }

    private boolean addTaskToDb(Task task) throws ScheduleSystemException {
        boolean flag = false;

        try {
            // 未执行任务入库
            TaskInfo taskInfo = new TaskInfo();
            taskInfo.setTaskType(task.getTaskType());
            taskInfo.setParameters(task.getParameters());
            taskInfo.setPriority(task.getPriority());
            taskInfo.setExecuteTime(new Date(task.getExecuteTime()));
            taskMapper.insert(taskInfo);

            // 设置任务主键id
            task.setTaskId(taskInfo.getTaskId());

            // 记录任务日志
            TaskInfoLogs taskLog = new TaskInfoLogs();
            taskLog.setExecuteTime(taskInfo.getExecuteTime());
            taskLog.setPriority(taskInfo.getPriority());
            taskLog.setParameters(taskInfo.getParameters());
            taskLog.setTaskType(taskInfo.getTaskType());
            taskLog.setTaskId(taskInfo.getTaskId());
            taskLog.setVersion(1);
            taskLog.setStatus(Constants.SCHEDULED);
            taskLogMapper.insert(taskLog);

            flag = true;
        } catch (Exception e) {
            log.warn("add task exception task id-{}", task.getTaskId());
            throw new ScheduleSystemException(e.getMessage());
        }

        return flag;
    }

    private void addTaskToCache(Task task) {
        // 使用任务类型和优先级作为key
        String key = task.getTaskType() + "_" + task.getPriority();
        long nextScheduleTime = getNextScheduleTime();

        // 判断任务应该放入未来数据集合还是消费者队列
        if (task.getExecuteTime() <= System.currentTimeMillis()) {
            cacheService.lRightPush(Constants.TOPIC + key, JSON.toJSONString(task));
        } else if (task.getExecuteTime() <= nextScheduleTime) {
            cacheService.zAdd(Constants.FUTURE + key, JSON.toJSONString(task), task.getExecuteTime());
        }
    }

    private long getNextScheduleTime() {
        // 判断缓存中是否有数据
        if (cacheService.exists(Constants.NEXT_SCHEDULE_TIME)) {
            String nextScheduleTimeStr = cacheService.get(Constants.NEXT_SCHEDULE_TIME);
            log.info("从缓存中获取NEXT_SCHEDULE_TIME-{}", nextScheduleTimeStr);
            return Long.parseLong(nextScheduleTimeStr);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, systemParamsConfiguration.getReload());
            this.nextScheduleTime = calendar.getTimeInMillis();
            cacheService.set(Constants.NEXT_SCHEDULE_TIME, nextScheduleTime + "");
            log.info("缓存中没有nextScheduleTime, 进行数据补偿{}", nextScheduleTime);
            return nextScheduleTime;
        }
    }

    @Override
    @Transactional
    public boolean cancelTask(long taskId) throws TaskNotExistException {
        boolean flag = false;

        Task task = updateDb(taskId, Constants.CANCELLED);
        if (task != null) {
            removeTaskFromCache(task);
            flag = true;
        }
        return flag;
    }

    private void removeTaskFromCache(Task task) {
        String key = task.getTaskType() + "_" + task.getPriority();

        // 判断从未来数据集合中移除还是从消费者队列移除
        if (task.getExecuteTime() <= System.currentTimeMillis()) {
            cacheService.lRemove(Constants.TOPIC + key, 0, JSON.toJSONString(task));
        } else {
            cacheService.zRemove(Constants.FUTURE + key, JSON.toJSONString(task));
        }
    }

    private Task updateDb(long taskId, int status) throws TaskNotExistException {
        Task task = null;
        try {
            // 删除任务信息表数据
            taskMapper.deleteById(taskId);
            // 修改日志信息表数据为取消状态
            TaskInfoLogs taskLog = taskLogMapper.selectById(taskId);
            taskLog.setStatus(status);
            taskLogMapper.updateById(taskLog);

            // 构造返回的任务对象
            task = new Task();
            BeanUtils.copyProperties(taskLog, task);
            task.setExecuteTime(taskLog.getExecuteTime().getTime());
        } catch (Exception e) {
            log.warn("task cancel exception task id-{}", taskId);
            throw new TaskNotExistException(e);
        }
        return task;
    }
}
