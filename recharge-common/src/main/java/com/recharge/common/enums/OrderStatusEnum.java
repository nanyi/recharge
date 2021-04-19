package com.recharge.common.enums;

import lombok.Getter;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Getter
public enum OrderStatusEnum {

    /**
     * 订单状态
     */
    CREATE(0, "创建"),
    WARTING(1, "处理中"),
    SUCCESS(2, "成功"),
    FAIL(3, "失败"),
    UNAFFIRM(9, "未确认");

    private Integer code;
    private String desc;

    OrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
