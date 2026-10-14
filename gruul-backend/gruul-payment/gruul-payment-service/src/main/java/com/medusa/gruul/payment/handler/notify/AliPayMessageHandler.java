package com.medusa.gruul.payment.handler.notify;

import com.egzosn.pay.ali.api.AliPayService;
import com.egzosn.pay.ali.bean.AliPayMessage;
import com.egzosn.pay.common.api.PayMessageHandler;
import com.egzosn.pay.common.bean.PayOutMessage;
import com.egzosn.pay.common.exception.PayErrorException;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.common.constant.AliConst;
import com.medusa.gruul.payment.common.enums.INotifyResponse;
import com.medusa.gruul.payment.common.enums.NotifyResponse;
import com.medusa.gruul.payment.common.enums.TradeStatus;
import com.medusa.gruul.payment.common.helper.PayHelper;
import com.medusa.gruul.payment.service.MultiPayOrderService;
import com.medusa.gruul.payment.service.MultiPayRefundService;
import com.medusa.gruul.payment.service.MultiPayVerifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 支付宝支付回调处理器
 * @author 张治保
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AliPayMessageHandler implements PayMessageHandler<AliPayMessage, AliPayService> {

    private final MultiPayOrderService orderPaymentService;
    private final MultiPayVerifyService multiPayVerifyService;
    private final MultiPayRefundService multiPayRefundService;
    /**
     * 处理支付回调消息的处理器接口
     * @param payMessage 支付消息
     * @param context    上下文，如果handler或interceptor之间有信息要传递，可以用这个
     * @param payService 支付服务
     */
    @Override
    public PayOutMessage handle(AliPayMessage payMessage, Map<String, Object> context, AliPayService payService) throws PayErrorException {

        log.info("::支付宝支付::notify::{}", Jackson.writeValueAsString(payMessage));
        String outTradeNo = payMessage.getOutTradeNo();
        Map<String, Object> payMessageDetail = payMessage.getPayMessage();
        /*
         * 退款通知
         */
        if (payMessageDetail.get(AliConst.Refund.REFUND_FEE)!= null){
            log.info("::支付宝支付::notify::{}","退款回调");
            multiPayRefundService.refundNotify(PayType.ALIPAY,payMessage);
            return payService.getPayOutMessage(NotifyResponse.ALI_SUCCESS.getCode(), NotifyResponse.ALI_SUCCESS.getMsg());
        }
        /*
         * 支付结果通知
         */
        log.info("::支付宝支付::notify::{}","支付回调");
        TradeStatus tradeStatus = multiPayVerifyService.verifyPayStatus(PayType.ALIPAY, outTradeNo);
        log.info("::支付宝支付::notify::交易状态:{}",tradeStatus);
        if ( TradeStatus.SUCCESS == tradeStatus){
            orderPaymentService.success(payMessage);
        }
        INotifyResponse response =  PayHelper.notifyResponse(tradeStatus, PayType.ALIPAY);
        log.info("::::::支付回调完毕::::::");
        return payService.getPayOutMessage(response.getCode(), response.getMsg());
    }

}
