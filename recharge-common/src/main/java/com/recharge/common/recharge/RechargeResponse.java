package com.recharge.common.recharge;

import com.recharge.common.enums.OrderStatusEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Data
public class RechargeResponse implements Serializable {

    private static final long serialVersionUID = -4956030216752736345L;

    private String moblie;

    private String orderNo;

    private String tradeNo;

    private double pamt;

    private int status = OrderStatusEnum.WARTING.getCode();

}
