package com.recharge.mock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.recharge.*"})
public class MockServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MockServiceApplication.class, args);
    }

    @Value("${openProxy}")
    private String openProxy;

    @Bean
    RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(60000);
        requestFactory.setReadTimeout(60000);
        InetSocketAddress addr = new InetSocketAddress("127.0.0.1", 8888);
        // http 代理
        Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
        if ("1".equals(openProxy)) {
            requestFactory.setProxy(proxy);
        }
        return new RestTemplate(requestFactory);
    }
}
