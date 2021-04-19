package com.recharge.web.service;

import com.recharge.common.entity.Result;
import com.recharge.common.recharge.RechargeRequest;
import com.recharge.common.recharge.RechargeResponse;
import com.recharge.trade.entity.OrderTrade;

import java.util.List;

/**
 * <p>
 * 订单系统集成
 * </p>
 *
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
public interface OrderProcessService {
    /**
     * 充值对接订单系统
     *
     * @return
     * @throws Exception
     */
    Result<RechargeResponse> recharge(RechargeRequest request) throws Exception;

    /**
     * 获取订单信息
     *
     * @param orderNo
     * @return
     * @throws Exception
     */
    OrderTrade queryOrderByNo(String orderNo) throws Exception;

    /**
     * 获取所有订单
     *
     * @return
     * @throws Exception
     */
    List<OrderTrade> queryAllOrder() throws Exception;

    void removeOrderTrade(String orderNo);
}
