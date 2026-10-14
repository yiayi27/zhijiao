package com.medusa.gruul.payment.web.controller;


import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.log.annotation.SysLog;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.api.model.pay.OrderPayParam;
import com.medusa.gruul.payment.api.model.pay.PayParam;
import com.medusa.gruul.payment.api.model.pay.PayResult;
import com.medusa.gruul.payment.common.enums.TradeStatus;
import com.medusa.gruul.payment.common.helper.HttpRequestNoticeParams;
import com.medusa.gruul.payment.service.MultiPayService;
import com.medusa.gruul.payment.service.MultiPayVerifyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author 张治保
 * date 2024/12/14
 */
@Slf4j
@RestController
@RequestMapping("/multi/pay")
@RequiredArgsConstructor
public class MultiPayController {

    private final MultiPayService multiPayService;

    private final MultiPayVerifyService multiPayVerifyService;

    /**
     * 订单支付跳转toPay
     *
     * @param params 订单支付参数
     * @return 拉起需要的数据
     */
    @SysLog("调起订单支付页面")
    @GetMapping(value = "/page")
    public Result<Object> toPayPage(@Validated OrderPayParam params) {
        return Result.ok(
                multiPayService.toPay(params)
        );
    }

    /**
     * 支付
     *
     * @param payParam 支付参数
     */
    @SysLog("获取调起支付所需数据")
    @PostMapping("/page")
    public Result<PayResult> toPayPage(@RequestBody @Validated PayParam payParam) {
        return Result.ok(
                multiPayService.toPay(payParam)
        );
    }

    /**
     * notify 支付回调
     *
     * @param detailsId 支付配置详情id
     * @param request   回调数据
     * @return 是否成功
     */
    @PreAuthorize("permitAll()")
    @SysLog("支付/退款 notify回调地址")
    @RequestMapping(value = "/notify/{detailsId}",produces = MediaType.TEXT_PLAIN_VALUE)
    public String payBack(HttpServletRequest request,
                          @PathVariable String detailsId) {
        return multiPayService.notify(detailsId, new HttpRequestNoticeParams(request));
    }

    /**
     * 渲染前端页面地址
     *
     * @param payType    支付方式
     * @param outTradeNo 外部订单号
     * @return 需要跳转的页面url
     */
    @PreAuthorize("permitAll()")
    @SysLog("重定向到页面跳转地址")
    @RequestMapping(value = "/return/{payType}")
    public void viewJump(
            @PathVariable PayType payType,
            @RequestParam("out_trade_no") String outTradeNo,
            HttpServletResponse response
    ) throws IOException {
        String jumpUrl = multiPayService.viewJump(payType, outTradeNo);
        response.sendRedirect(jumpUrl);
    }

    /**
     * 获取支付状态并尝试更新支付记录信息
     * @param payType 支付类型
     * @param outTradeNo 外部订单号
     * @return 交易状态
     */
    @SysLog("获取支付状态")
    @GetMapping("/trade/status/{payType}/{outTradeNo}")
    public Result<TradeStatus> tradeStatus(
            @PathVariable PayType payType,
            @PathVariable String outTradeNo
    ) {
        return Result.ok(
                multiPayVerifyService.getPayStatus(payType, outTradeNo)
        );
    }
}
