package com.recharge.mock.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.recharge.common.entity.Constants;
import com.recharge.common.entity.Result;
import com.recharge.common.entity.StatusCode;
import com.recharge.common.enums.OrderStatusEnum;
import com.recharge.common.recharge.RechargeResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 极速充值mock
 * </p>
 *
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Slf4j
@RestController
@RequestMapping("/jisuapi")
@Api(value = "极速APIMock", tags = {"JiSuMock"})
public class MockJisuRechargeController extends BaseController {

    /**
     * @param mobile
     * @param amount
     * @param outorderNo
     * @param reqStatus   可以自定义成功失败
     * @param orderStatus 可以自定义回调订单状态
     * @return
     */
    @ApiOperation("手机充值")
    @PostMapping(value = "/mobile-recharge/recharge")
    public Result<RechargeResponse> add(@RequestParam("mobile") String mobile, @RequestParam("amount") String amount, @RequestParam("outorderNo") String outorderNo, @RequestParam(value = "req_status", defaultValue = "200", required = false) int reqStatus, @RequestParam(value = "order_status", defaultValue = "2", required = false) int orderStatus) {
        Result<RechargeResponse> result = new Result<RechargeResponse>();
        RechargeResponse response = new RechargeResponse();
        response.setMoblie(mobile);
        response.setOrderNo(outorderNo);
        String tradeNo = IdWorker.get32UUID();
        log.info("tradeNo " + tradeNo);
        response.setTradeNo(tradeNo);
        response.setPamt(Double.valueOf(amount));
        result.setData(response);
        // 充值请求成功
        if (reqStatus == StatusCode.OK) {
            result.setCode(StatusCode.OK);
            // 实际充值 结果异步通知
            rechargeNotify(outorderNo, tradeNo, orderStatus);
            // 模拟供应商订单保存
            response.setStatus(orderStatus);
            cacheService.hPut(Constants.JI_SU_ORDER, outorderNo + tradeNo, JSON.toJSONString(result));
        } else if (reqStatus == StatusCode.ERROR) {
            // 充值请求失败
            result.setCode(StatusCode.ORDER_REQ_FAILED);
            result.setMsg("请求充值失败, 请重试");
        }
        return result;
    }

    @ApiOperation("订单状态")
    @GetMapping(value = "/mobile-recharge/orderState")
    public Result<RechargeResponse> orderState(@RequestParam("outorder") String outorderNo, @RequestParam("tradeNo") String tradeNo) {
        Result<RechargeResponse> result;
        String key = outorderNo + tradeNo;
        log.info("/mobile-recharge/orderState " + outorderNo);
        log.info("/mobile-recharge/orderState " + tradeNo);

        if (cacheService.hExists(Constants.JI_SU_ORDER, key)) {
            result = JSON.parseObject(String.valueOf(cacheService.hGet(Constants.JI_SU_ORDER, key)), new TypeReference<Result<RechargeResponse>>() {
            });
        } else {
            result = new Result<RechargeResponse>();
            RechargeResponse response = new RechargeResponse();
            response.setStatus(OrderStatusEnum.FAIL.getCode());
            response.setOrderNo(outorderNo);
            result.setCode(StatusCode.ERROR);
            result.setMsg("充值失败");
        }
        return result;
    }
}
