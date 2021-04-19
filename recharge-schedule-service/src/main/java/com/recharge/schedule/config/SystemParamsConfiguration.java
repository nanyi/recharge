package com.recharge.schedule.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Data
@Component
@ConfigurationProperties(prefix = "recharge")
public class SystemParamsConfiguration {
    /**
     * 预加载时间参数
     */
    private int reload;

    /**
     * zookeeper节点
     */
    private String selectMasterZookeeper;
}
