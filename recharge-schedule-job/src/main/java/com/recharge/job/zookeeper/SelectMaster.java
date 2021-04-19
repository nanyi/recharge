package com.recharge.job.zookeeper;

import lombok.Data;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Data
@Component
@ConfigurationProperties(prefix = "recharge")
public class SelectMaster {
    private String selectMasterZookeeper;

    Map<String, Boolean> masterMap = new HashMap<String, Boolean>();

    /**
     * 选主
     *
     * @param leaderPath zookeeper目录节点
     */
    public void selectMaster(String leaderPath) {
        CuratorFramework client = CuratorFrameworkFactory.builder().
                connectString(selectMasterZookeeper)
                // 超时时间
                .sessionTimeoutMs(5000)
                // 连接不上重试三次
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();

        // 争抢注册节点
        @SuppressWarnings("resource")
        LeaderSelector selector = new LeaderSelector(client, leaderPath,
                new LeaderSelectorListenerAdapter() {

                    @Override
                    public void takeLeadership(CuratorFramework client) throws Exception {
                        //如果争抢到当前注册节点
                        masterMap.put(leaderPath, true);
                        while (true) {
                            //抢占当前节点
                            TimeUnit.SECONDS.sleep(3);
                        }
                    }
                });

        masterMap.put(leaderPath, false);
        selector.autoRequeue();
        selector.start();
    }

    public boolean checkMaster(String leaderPath) {
        Boolean isMaster = masterMap.get(leaderPath);
        return isMaster == null ? false : isMaster;
    }
}
