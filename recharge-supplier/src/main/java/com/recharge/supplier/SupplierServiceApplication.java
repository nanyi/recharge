package com.recharge.supplier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@EnableScheduling
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.recharge.feign.agent"})
@ComponentScan(basePackages = {"com.recharge.common.cache", "com.recharge.supplier", "com.recharge.trade.mapper", "com.recharge.supplier.*"})
public class SupplierServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SupplierServiceApplication.class, args);
    }

    /**
     * 远程调用
     *
     * @return
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
