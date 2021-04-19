package com.recharge.web.controller;

import com.recharge.common.entity.Result;
import com.recharge.common.recharge.RechargeRequest;
import com.recharge.common.recharge.RechargeResponse;
import com.recharge.trade.entity.OrderTrade;
import com.recharge.web.service.OrderProcessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Slf4j
@Controller
@Api(value = "话费服务-订单操作视图返回", tags = {"Web"})
public class OrderController {
    @Autowired
    private OrderProcessService orderProcessService;
    @Autowired
    private RocketMQTemplate template;

    @ApiOperation("返回主页视图")
    @GetMapping(value = "/")
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    /**
     * 充值操作(充值订单)
     * 订单请求信息
     *
     * @return
     */
    @ApiOperation("充值操作")
    @GetMapping(value = "/crtorder")
    public ModelAndView createRechargeOrder(@RequestParam("mobile") String mobile,
                                            @RequestParam("pamt") double pamt,
                                            @RequestParam("factPrice") double factPrice,
                                            @RequestParam("brandId") String brandId,
                                            @RequestParam("categoryId") String categoryId) {
        RechargeRequest rechargeRequest = new RechargeRequest();
        rechargeRequest.setMobile(mobile);
        rechargeRequest.setPamt(pamt);
        rechargeRequest.setFactPrice(factPrice);
        rechargeRequest.setBrandId(brandId);
        rechargeRequest.setCategoryId(categoryId);
        // rechargeRequest.setErrorCode(1001);

        Result<RechargeResponse> result = null;
        ModelAndView view = null;

        try {
            // 对接订单系统
            result = orderProcessService.recharge(rechargeRequest);
        } catch (Exception e) {
            e.printStackTrace();
            view = new ModelAndView("recharge");
        }
        if (result.getCode() == 200) {
            //成功
            view = new ModelAndView("pay");
            view.addObject("result", result);
        } else {
            //失败
            view = new ModelAndView("recharge");
        }
        return view;
    }


    /**
     * 选择订单支付方式
     *
     * @return
     */
    @ApiOperation("选择支付方式")
    @GetMapping(value = "/payorder")
    public ModelAndView payorder(@RequestParam("orderNo") String orderNo) {
        OrderTrade orderTrade = null;
        try {
            // 根据订单号查询待支付订单
            orderTrade = orderProcessService.queryOrderByNo(orderNo);
            // 调用支付服务完成支付, 接收支付结果

            // 支付后通知供应商对接模块-异步通知
            RechargeRequest request = new RechargeRequest();
            request.setOrderNo(orderNo);
            request.setMobile(orderTrade.getMobile());
            request.setPamt(orderTrade.getSalesPrice());
            request.setTradeNo(orderTrade.getOrderNo());
            // request.setErrorCode(1001);

            template.convertAndSend("pay", request);
        } catch (Exception e) {
            return new ModelAndView("payfail");
        }
        ModelAndView view = new ModelAndView("paysuccess");
        view.addObject("orderTrade", orderTrade);
        return view;
    }

    @ApiOperation("订单列表")
    @GetMapping(value = "/orderList")
    public ModelAndView orderList() {
        List<OrderTrade> orderList = null;
        try {
            orderList = orderProcessService.queryAllOrder();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ModelAndView view = new ModelAndView("myOrder");
        view.addObject("orderList", orderList);
        return view;
    }

    @ApiOperation("移除订单")
    @GetMapping(value = "/remove")
    public String remove(@RequestParam("orderNo") String orderNo) {
        orderProcessService.removeOrderTrade(orderNo);
        return "redirect:orderList";
    }
}
