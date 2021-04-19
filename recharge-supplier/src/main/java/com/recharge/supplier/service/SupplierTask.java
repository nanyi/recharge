package com.recharge.supplier.service;

import com.recharge.common.recharge.RechargeRequest;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
public interface SupplierTask {
    /**
     * 取消状态检查任务
     *
     * @param orderNo
     */
    void cancelCheckTask(String orderNo);

    /**
     * 远程调用异常重试
     */
    void rechargeException();

    /**
     * 供应商轮转
     */
    void roundRecharge();

    /**
     * 添加重试任务
     *
     * @param rechargeRequest
     */
    void addRetryTask(RechargeRequest rechargeRequest);

    /**
     * 重试 拉取/消费重试任务
     */
    void retryRecharge();
}
