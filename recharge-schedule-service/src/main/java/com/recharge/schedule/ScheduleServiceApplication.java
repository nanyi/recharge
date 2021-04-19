package com.recharge.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@EnableScheduling
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.recharge.common.cache", "com.recharge.schedule.*"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ScheduleServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScheduleServiceApplication.class, args);
    }
}
