package com.recharge.supplier.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Data
@Component
@ConfigurationProperties(prefix = "supplier")
public class SupplierConfig {
    /**
     * 加载供应商api地址
     */
    private Map<String, String> apis;

    /**
     * 最大重试次数
     */
    private int maxrepeat;

    /**
     * 订单状态
     */
    private Map<String, String> checkStateApis;

    /**
     * 订单充值状态检查时间
     */
    private int stateCheckTime;
}
