package com.recharge.feign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.recharge.feign.agent"})
public class ScheduleFeignServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScheduleFeignServiceApplication.class, args);
    }
}
