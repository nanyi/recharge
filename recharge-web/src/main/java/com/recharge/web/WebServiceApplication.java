package com.recharge.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.recharge.trade.mapper", "com.recharge.web.*"})
public class WebServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebServiceApplication.class, args);
    }
}
