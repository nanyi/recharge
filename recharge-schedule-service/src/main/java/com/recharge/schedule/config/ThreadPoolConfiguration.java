package com.recharge.schedule.config;

import com.recharge.schedule.service.VisiableThreadPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@EnableAsync
@Configuration
public class ThreadPoolConfiguration {
    @Bean("threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        threadPool.setCorePoolSize(5);
        // 设置最大线程数
        threadPool.setMaxPoolSize(100);
        // 设置线程超时等待时间
        threadPool.setKeepAliveSeconds(60);
        // 设置任务等待队列的大小
        threadPool.setQueueCapacity(100);
        // 设置线程池内线程的名称前缀
        threadPool.setThreadNamePrefix("thread pool task executor");
        // 设置任务拒绝策略
        threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 直接初始化
        threadPool.initialize();
        return threadPool;
    }

    @Bean("visiableThreadPool")
    public ThreadPoolTaskExecutor visiableThreadPool(){
        ThreadPoolTaskExecutor visiableThreadPool = new VisiableThreadPool();
        visiableThreadPool.setCorePoolSize(10);
        visiableThreadPool.setMaxPoolSize(1000);
        visiableThreadPool.setKeepAliveSeconds(60);
        visiableThreadPool.setQueueCapacity(1000);
        visiableThreadPool.setThreadNamePrefix("visiable thread pool");
        visiableThreadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        visiableThreadPool.initialize();
        return visiableThreadPool;
    }
}
