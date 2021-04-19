package com.recharge.common.recharge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckStatusRequest {
    /**
     * supplier: 不同供应商查询订单充值状态API地址不一样, 需要根据供应商编号去获取
     * orderNo: 充吧系统生成的订单号, 充吧支付成功后通知对接模块下单时已经将充吧订单号传递过来了
     * tradeNo: 供应商系统生成的唯一的交易号, 当我们调用供应商对接下单API后由供应商生成的唯一交易号, 并返回给了充值系统
     */
    private String supplier;
    private String orderNo;
    private String tradeNo;
}
