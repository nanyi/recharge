package com.recharge.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@EnableScheduling
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.recharge.feign.agent"})
public class ScheduleJobServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScheduleJobServiceApplication.class, args);
    }
}
