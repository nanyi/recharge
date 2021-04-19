package com.recharge.schedule.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Slf4j
public class ShardingAlgorithmMonth implements PreciseShardingAlgorithm<Date> {
    /**
     * 执行分片策略
     *
     * @param collection           候选表集合
     * @param preciseShardingValue 精确分片值:任务的执行时间
     * @return 数据路由到的表名称
     */
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<Date> preciseShardingValue) {
        String node = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy_M");
            String dateStr = dateFormat.format(preciseShardingValue.getValue());
            for (String nodeCandidate : collection) {
                if (nodeCandidate.endsWith(dateStr)) {
                    node = nodeCandidate;
                    break;
                }
            }
        } catch (Exception e) {
            log.error("sharding-sphere doSharding exception {}", e.getMessage());
        }
        return node;
    }
}
