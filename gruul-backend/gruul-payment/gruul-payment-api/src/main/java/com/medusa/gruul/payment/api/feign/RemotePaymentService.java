package com.medusa.gruul.payment.api.feign;

import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.payment.api.entity.EntPay;
import com.medusa.gruul.payment.api.model.dto.*;
import com.medusa.gruul.payment.api.model.param.EntPayReQuestParam;
import com.medusa.gruul.payment.api.model.pay.PayParam;
import com.medusa.gruul.payment.api.model.pay.PayResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * @author whh
 * @date 2023/11/06
 */
@FeignClient(value = "payment")
public interface RemotePaymentService {

    /**
     * 支付请求
     *
     * @param payRequestDto 请求数据
     * @return 请求结果
     */
    @RequestMapping(value = "/pay/request", method = RequestMethod.POST)
    PayResultDto payRequest(@RequestBody PayRequestDto payRequestDto);

    /**
     * 发起支付请求
     * @param payParam 支付参数
     * @return 支付结果
     */
    @PostMapping("/multi/pay/page")
    Result<PayResult> toPayPage(@RequestBody @Validated PayParam payParam);


    /**
     * 获取指定订单交易状态
     *
     * @param outTradeNo    业务订单号
     * @param transactionId 交易号
     * @param payChannel    支付渠道
     * @return 状态
     */
    @RequestMapping(value = "/pay/status/{outTradeNo}", method = RequestMethod.GET)
    PayStatusDto getPayStatus(@PathVariable("outTradeNo") String outTradeNo,
                              @PathVariable("transactionId") String transactionId,
                              @RequestParam("payChannel") String payChannel);


    /**
     * 商户支付给个人请求
     *
     * @param param 请求数据
     * @return 请求结果
     */
    @RequestMapping(value = "/ent_pay/pay", method = RequestMethod.POST)
    EntPay payRequest(@RequestBody @Validated EntPayReQuestParam param);


    /**
     * 订单退款
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/pay/refund", method = RequestMethod.POST)
    Result<Void> payRefund(@RequestBody @Validated RefundRequestDto param);


    /**
     * 生成多店铺支付默认值
     *
     * @param shopId   shopId
     * @return Result
     */
    @GetMapping(value = "pay/default")
    Result<Void> paymentDefault(@RequestParam("shopId") String shopId);
}
