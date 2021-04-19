package com.recharge.trade.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Data
@TableName("order_trade")
public class OrderTrade implements Serializable {
    private static final long serialVersionUID = 9218893698876950325L;

    @TableId
    private Long id;

    @TableField(value = "gmt_create", fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = "gmt_modified", fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @TableField("brand_id")
    private String brandId;

    @TableField("category_id")
    private String categoryId;

    @TableField("order_status")
    private Integer orderStatus;

    @TableField("sales_price")
    private double salesPrice;

    @TableField("face_price")
    private double facePrice;

    @TableField("mobile")
    private String mobile;

    @TableField("trade_no")
    private Long tradeNo;

    @TableField("order_no")
    private String orderNo;

    @TableField("order_time")
    private Date orderTime;
}
