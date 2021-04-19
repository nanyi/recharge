package com.recharge.job.service;

import com.recharge.common.entity.Constants;
import com.recharge.common.entity.ResponseMessage;
import com.recharge.feign.agent.ScheduleServiceAgent;
import com.recharge.job.zookeeper.SelectMaster;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Slf4j
@Service
public class JobService {
    @Autowired
    private ScheduleServiceAgent scheduleServiceAgent;

    @Autowired
    private SelectMaster selectMaster;

    @PostConstruct
    private void init() {
        // 争抢注册主节点
        selectMaster.selectMaster(Constants.JOB_LEADER_PATH);
    }

    /**
     * 注意要将schedule-service中的@Scheduled注释
     */
    @Scheduled(cron = "*/1 * * * * ?")
    public void refresh() {
        // 只有主节点才去统一调度刷新
        if (selectMaster.checkMaster(Constants.JOB_LEADER_PATH)) {
            log.info("job主节点开始进行刷新任务调度");
            try {
                ResponseMessage refresh = scheduleServiceAgent.refresh();
                log.info("refresh-{}", refresh);
            } catch (Exception e) {
                log.error("refresh exception {}", e.getMessage());
            }
        } else {
            log.info("job从节点备用");
        }
    }
}
