package com.recharge.supplier.service;

import com.recharge.common.recharge.CheckStatusRequest;
import com.recharge.common.recharge.RechargeRequest;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
public interface SupplierService {
    /**
     * 添加状态检查查询任务
     *
     * @param checkStatusRequest
     */
    void addCheckStatusTask(CheckStatusRequest checkStatusRequest);

    /**
     * 状态检查任务
     */
    void checkStatus();

    /**
     * 对接下单成功后检查充值状态
     *
     * @param checkStatusRequest
     */
    void checkStatus(CheckStatusRequest checkStatusRequest);

    /**
     * 对接供应商下单
     *
     * @param rechargeRequest
     */
    void recharge(RechargeRequest rechargeRequest);
}
