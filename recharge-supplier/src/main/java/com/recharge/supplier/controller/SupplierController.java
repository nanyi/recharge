package com.recharge.supplier.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.recharge.common.cache.CacheService;
import com.recharge.common.entity.Constants;
import com.recharge.supplier.service.SupplierTask;
import com.recharge.trade.entity.OrderTrade;
import com.recharge.trade.mapper.OrderTradeMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Slf4j
@RestController
@Api(value = "供应商API", tags = "Supplier")
public class SupplierController {
    @Autowired
    private CacheService cacheService;
    @Autowired
    protected OrderTradeMapper orderTradeMapper;
    @Autowired
    private SupplierTask supplierTask;

    @ApiOperation("订单回调")
    @PostMapping(value = "/order/notify")
    public String notify(@RequestParam("orderNo") String orderNo, @RequestParam("tradeNo") String tradeNo, @RequestParam("status") String status) {
        log.info("充值回调成功修改订单{}-状态为{}", orderNo, status);
        updateTrade(orderNo, Integer.parseInt(status));

        log.info("回调成功后取消状态检查任务");
        supplierTask.cancelCheckTask(orderNo);
        return "sucess";
    }

    private void updateTrade(String orderNo, int orderStatus) {
        // 修改订单状态
        QueryWrapper<OrderTrade> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        OrderTrade orderTrade = orderTradeMapper.selectOne(queryWrapper);
        if (orderTrade != null) {
            orderTrade.setOrderStatus(orderStatus);
            orderTradeMapper.update(orderTrade, queryWrapper);
        }
    }

    @ApiOperation("供应商恢复")
    @GetMapping("/recovery")
    public String recovery(@RequestParam("supply") String supply) {
        Set<String> excluedes = cacheService.setMembers(Constants.EXCLUDE_SUPPLIER);
        if (excluedes.contains(supply)) {
            cacheService.sRemove(Constants.EXCLUDE_SUPPLIER, supply);
            return "恢复成功!";
        } else {
            return "供应商编号不存在!";
        }
    }
}
