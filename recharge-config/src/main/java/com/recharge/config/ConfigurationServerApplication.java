package com.recharge.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@EnableConfigServer
@SpringBootApplication
public class ConfigurationServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigurationServerApplication.class, args);
    }
}
