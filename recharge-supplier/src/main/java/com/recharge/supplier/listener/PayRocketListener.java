package com.recharge.supplier.listener;

import com.recharge.common.recharge.RechargeRequest;
import com.recharge.supplier.service.SupplierService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "pay", consumerGroup = "order-paid-consumer")
public class PayRocketListener implements RocketMQListener<RechargeRequest> {
    @Autowired
    private SupplierService supplierService;

    /**
     * 监听支付消息 对接供应商下单
     *
     * @param rechargeRequest
     */
    @Override
    public void onMessage(RechargeRequest rechargeRequest) {
        log.info("payRocketListener监听到了支付成功消息, {}", rechargeRequest);
        supplierService.recharge(rechargeRequest);
    }
}
