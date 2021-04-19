package com.recharge.common.recharge;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Data
public class RechargeRequest implements Serializable {

    private static final long serialVersionUID = 600264515412796636L;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 默认供应商编号
     */
    private String supply = "juheapi";

    /**
     * 供应商交易地址
     */
    private String rechargeUrl;

    /**
     * 充值手机号
     */
    private String mobile;

    /**
     * 品类归属品牌编码
     */
    private String brandId;

    /**
     * 品类编号
     */
    private String categoryId;
    /**
     * 待支付金额, 单位: 分
     */
    private double pamt;

    /**
     * 面值
     */
    private double factPrice;

    /**
     * 重试发送次数
     */
    private int repeat = 0;

    /**
     * 状态
     */
    private int state = 1;

    /**
     * 业务类型错误码---决定了任务的类型和优先级
     */
    private int errorCode;

    /**
     * 追踪编号
     */
    private String tradeNo;
}
