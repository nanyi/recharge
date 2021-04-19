package com.recharge.mock.controller;

import com.alibaba.fastjson.JSON;
import com.recharge.common.cache.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Slf4j
@RestController
public class BaseController {
    @Value("${notify-url}")
    String notifyUrl;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CacheService cacheService;

    protected static final ExecutorService NOTIFY_EXECUTOR = Executors.newFixedThreadPool(50);

    /**
     * 订单id
     * 交易状态 state=1 成功 state=-1 失败
     *
     * @param orderNo
     */
    public void rechargeNotify(String orderNo, String tradeNo, int orderStatus) {
        try {
            NOTIFY_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 充值
                        Thread.sleep(5000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // 充值结果回调
                    MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
                    map.add("orderNo", orderNo);
                    map.add("tradeNo", tradeNo);
                    map.add("status", orderStatus + "");

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map, headers);
                    log.warn("回调接口={}", notifyUrl);
                    try {
                        String result = restTemplate.postForEntity(notifyUrl, entity, String.class).getBody();
                        log.warn("充值业务-通知: 返回报文={}", result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (RejectedExecutionException e) {
            log.error("供货线程池达到限额", e);
        }
    }


}
