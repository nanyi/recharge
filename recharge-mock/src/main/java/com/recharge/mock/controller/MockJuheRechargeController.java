package com.recharge.mock.controller;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.recharge.common.entity.Result;
import com.recharge.common.entity.StatusCode;
import com.recharge.common.enums.OrderStatusEnum;
import com.recharge.common.recharge.RechargeRequest;
import com.recharge.common.recharge.RechargeResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 聚合充值mock
 * </p>
 *
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@RestController
@RequestMapping("/juheapi")
@Api(value = "聚合APIMock", tags = {"JuHeMock"})
public class MockJuheRechargeController extends BaseController {

    @ApiOperation("手机充值")
    @PostMapping(value = "/recharge")
    public Result<RechargeResponse> add(@RequestBody RechargeRequest request) {
        Result<RechargeResponse> result = new Result<RechargeResponse>();
        RechargeResponse response = new RechargeResponse();
        response.setMoblie(request.getMobile());
        response.setOrderNo(request.getOrderNo());
        response.setTradeNo(IdWorker.get32UUID());
        response.setPamt(request.getPamt());
        result.setData(response);
        result.setCode(StatusCode.BALANCE_NOT_ENOUGH);
        result.setMsg("余额不足");
        return result;
    }

    @ApiOperation("订单状态")
    @GetMapping(value = "/orderState")
    public Result<RechargeResponse> orderState(@RequestParam("outorderNo") String outorderNo, @RequestParam("tradeNo") String tradeNo) {
        Result<RechargeResponse> result = new Result<RechargeResponse>();
        RechargeResponse response = new RechargeResponse();
        response.setStatus(OrderStatusEnum.FAIL.getCode());
        response.setOrderNo(outorderNo);
        result.setCode(StatusCode.ERROR);
        result.setMsg("充值失败");
        return result;
    }

}
