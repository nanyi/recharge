package com.recharge.supplier.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.recharge.common.cache.CacheService;
import com.recharge.common.entity.*;
import com.recharge.common.enums.OrderStatusEnum;
import com.recharge.common.enums.TaskTypeEnum;
import com.recharge.common.recharge.CheckStatusRequest;
import com.recharge.common.recharge.RechargeRequest;
import com.recharge.common.recharge.RechargeResponse;
import com.recharge.common.utils.ProtostuffUtil;
import com.recharge.feign.agent.ScheduleServiceAgent;
import com.recharge.supplier.config.SupplierConfig;
import com.recharge.trade.entity.OrderTrade;
import com.recharge.trade.mapper.OrderTradeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 1.8
 */
@Slf4j
@Service
public class SupplierServiceImpl implements SupplierService {
    @Autowired
    private SupplierConfig supplierConfig;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private SupplierTask supplierTask;
    @Autowired
    private OrderTradeMapper orderTradeMapper;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private ScheduleServiceAgent scheduleServiceAgent;

    @Override
    public void addCheckStatusTask(CheckStatusRequest checkStatusRequest) {
        Task task = new Task();
        TaskTypeEnum taskTypeEnum = TaskTypeEnum.STATE_CHECK;
        task.setTaskType(taskTypeEnum.getTaskType());
        task.setPriority(taskTypeEnum.getPriority());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, supplierConfig.getStateCheckTime());
        task.setExecuteTime(calendar.getTimeInMillis());

        task.setParameters(ProtostuffUtil.serialize(checkStatusRequest));
        // 添加状态检查任务
        ResponseMessage result = scheduleServiceAgent.push(task);
        // 供应商系统能够正常回调则需要取消状态检查任务,
        // 取消任务需要任务id,
        // 回调成功后修改订单状态知道订单号
        // 因此要将订单号和任务id做一个映射存储
        if (result.getCode() == StatusCode.OK) {
            cacheService.hPut(Constants.ORDER_CHECKED, checkStatusRequest.getOrderNo(), String.valueOf(result.getData()));
        }
    }

    @Override
    @Scheduled(fixedRate = 1000)
    public void checkStatus() {
        // 消费状态检查任务-发起状态检查
        TaskTypeEnum statecheck = TaskTypeEnum.STATE_CHECK;
        ResponseMessage poll = scheduleServiceAgent.poll(statecheck.getTaskType(), statecheck.getPriority());
        if (poll.isFlag()) {
            if (poll.getData() != null) {
                String taskStr = JSON.toJSONString(poll.getData());
                Task task = JSON.parseObject(taskStr, new TypeReference<Task>() {
                });
                CheckStatusRequest statusRequest = ProtostuffUtil.deserialize(task.getParameters(), CheckStatusRequest.class);
                log.info("消费任务时从拉取的任务数据{}", statusRequest);
                // 调用状态检查接口进行状态检查
                checkStatus(statusRequest);
            }
        }
    }

    @Override
    public void checkStatus(CheckStatusRequest checkStatusRequest) {
        // 获取状态检查接口地址
        String checkStatusApi = supplierConfig.getCheckStateApis().get(checkStatusRequest.getSupplier());
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // 封装请求参数-实际业务中看供应商需要传递哪些参数, 实际情况中可能要根据不同的供应商传递不同的参数,
        // 那就要在这个逻辑中添加不同的条件分支
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("outorderNo", checkStatusRequest.getOrderNo());
        map.add("tradeNo", checkStatusRequest.getTradeNo());

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(checkStatusApi, httpEntity, String.class);
        Result<RechargeResponse> result = JSON.parseObject(responseEntity.getBody(), new TypeReference<Result<RechargeResponse>>() {
        });

        assert result != null;
        if (result.getCode() == StatusCode.OK) {
            log.info("订单状态检查, 订单成功{}", checkStatusRequest);
            updateTrade(checkStatusRequest.getOrderNo(), result.getData().getStatus());
        } else {
            // 订单失败
            log.info("订单状态检查, 订单失败{}", checkStatusRequest);
            updateTrade(checkStatusRequest.getOrderNo(), OrderStatusEnum.FAIL.getCode());
        }
    }

    @Override
    public void recharge(RechargeRequest rechargeRequest) {
        // 限制最大重试次数 如果超过了最大次数, 停止供应商对接, 订单失败-实际业务中是对接订单服务
        if (rechargeRequest.getRepeat() > supplierConfig.getMaxrepeat()) {
            updateTrade(rechargeRequest.getOrderNo(), OrderStatusEnum.FAIL.getCode());
            return;
        }

        String checkSupply = checkSupply(rechargeRequest.getSupply());
        if (checkSupply != null) {
            rechargeRequest.setSupply(checkSupply);
        } else {
            updateTrade(rechargeRequest.getOrderNo(), OrderStatusEnum.FAIL.getCode());
            return;
        }

        Result<RechargeResponse> result = null;
        try {
            result = doDispatchSupplier(rechargeRequest);
        } catch (Exception e) {
            log.error("recharge exception, {}", e.getMessage());
            // 添加远程调用重试任务
            rechargeRequest.setErrorCode(StatusCode.REMOTE_ERROR);
            supplierTask.addRetryTask(rechargeRequest);
            return;
        }

        if (result != null) {
            // 判断成功还是失败
            if (result.getCode() == StatusCode.OK) {
                log.info("下单成功, 等待充值处理回调.");
                // 特别注意此时订单状态还不能修改为充值成功-供应商回调之后才能修改为成功
                // 充值处理中等待确认
                updateTrade(rechargeRequest.getOrderNo(), OrderStatusEnum.UNAFFIRM.getCode());
                log.info("下单成功, 添加状态检查任务, 1分钟后进行状态检查");
                addCheckStatusTask(new CheckStatusRequest(rechargeRequest.getSupply(), result.getData().getOrderNo(), result.getData().getTradeNo()));
                return;
            } else {
                // 失败就分好几种
                // 余额不足轮转, 下单失败重试等
                if (result.getCode() == StatusCode.BALANCE_NOT_ENOUGH) {
                    // 将我们余额不足的供应商放入reids 排除集合中
                    cacheService.sAdd(Constants.EXCLUDE_SUPPLIER, rechargeRequest.getSupply());
                    String nextSupply = nextSupply();
                    System.out.println("轮转到新的供应商为:" + nextSupply);
                    if (nextSupply != null) {
                        rechargeRequest.setSupply(nextSupply);
                        rechargeRequest.setRepeat(0);
                        rechargeRequest.setErrorCode(StatusCode.BALANCE_NOT_ENOUGH);
                    } else {
                        // 没有供应商了
                        updateTrade(rechargeRequest.getOrderNo(), OrderStatusEnum.FAIL.getCode());
                        return;
                    }
                } else if (result.getCode() == StatusCode.ORDER_REQ_FAILED) {
                    // 重试逻辑的编写-添加重试任务
                    rechargeRequest.setErrorCode(StatusCode.ORDER_REQ_FAILED);
                }
                supplierTask.addRetryTask(rechargeRequest);
            }
        }
    }

    private String checkSupply(String supply) {
        Set<String> excludes = cacheService.setMembers(Constants.EXCLUDE_SUPPLIER);
        if (excludes.contains(supply)) {
            return nextSupply();
        } else {
            return supply;
        }
    }

    private String nextSupply() {
        Set<String> excludes = cacheService.setMembers(Constants.EXCLUDE_SUPPLIER);
        Map<String, String> allApis = supplierConfig.getApis();
        for (String supply : allApis.keySet()) {
            if (!excludes.contains(supply)) {
                return supply;
            }
        }
        return null;
    }

    private void updateTrade(String orderNo, int orderStatus) {
        // 修改订单状态
        QueryWrapper<OrderTrade> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        OrderTrade orderTrade = orderTradeMapper.selectOne(queryWrapper);
        if (orderTrade != null) {
            orderTrade.setOrderStatus(orderStatus);
            orderTradeMapper.updateById(orderTrade);
        }
    }

    /**
     * 对接逻辑分发
     *
     * @param rechargeRequest
     */
    private Result<RechargeResponse> doDispatchSupplier(RechargeRequest rechargeRequest) {
        // 设置供应商的调用地址:
        String url = supplierConfig.getApis().get(rechargeRequest.getSupply());
        rechargeRequest.setRechargeUrl(url);

        // 根据需要对接的供应商的编号确定不同的对接方式-不同的api需要传递的参数类型和参数名称等各不相同
        if (Constants.JU_HE_API.equals(rechargeRequest.getSupply())) {
            // 对接聚合
            return doPostJuhe(rechargeRequest);
        } else if (Constants.JI_SU_API.equals(rechargeRequest.getSupply())) {
            // 对接极速
            return doPostJisu(rechargeRequest);
        }

        return null;
    }

    private Result<RechargeResponse> doPostJuhe(RechargeRequest rechargeRequest) {
        // 聚合要求传递的是json格式的数据
        // 创建并设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 创建请求实体
        HttpEntity httpEntity = new HttpEntity(JSON.toJSONString(rechargeRequest), headers);
        // 发送请求
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(rechargeRequest.getRechargeUrl(), httpEntity, String.class);

        Result<RechargeResponse> result = JSON.parseObject(responseEntity.getBody(), new TypeReference<Result<RechargeResponse>>() {
        });

        log.info("聚合对接响应反序列化结果-{}", result);

        return result;
    }

    private Result<RechargeResponse> doPostJisu(RechargeRequest rechargeRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // 设置表单参数
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("mobile", rechargeRequest.getMobile());
        map.add("amount", rechargeRequest.getPamt() + "");
        map.add("outorderNo", rechargeRequest.getOrderNo());
        map.add("repeat", "" + rechargeRequest.getRepeat());

        // 模拟请求失败
        // map.add("req_status", "" + StatusCode.ERROR);
        // 模拟请求成功
        map.add("req_status", "" + StatusCode.OK);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(rechargeRequest.getRechargeUrl(), requestEntity, String.class);
        // 转换成统一对象
        Result<RechargeResponse> result = JSON.parseObject(responseEntity.getBody(), new TypeReference<Result<RechargeResponse>>() {
        });

        log.info("极速对接响应反序列化结果-{}", result);

        return result;
    }
}
