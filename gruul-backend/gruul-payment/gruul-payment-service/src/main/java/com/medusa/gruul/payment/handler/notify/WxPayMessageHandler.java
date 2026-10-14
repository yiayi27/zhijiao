package com.medusa.gruul.payment.handler.notify;

import com.egzosn.pay.common.api.PayMessageHandler;
import com.egzosn.pay.common.bean.PayOutMessage;
import com.egzosn.pay.common.exception.PayErrorException;
import com.egzosn.pay.wx.v3.api.WxPayService;
import com.egzosn.pay.wx.v3.bean.response.WxPayMessage;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.common.constant.WxConst;
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
 * 微信支付回调处理器
 * @author 张治保
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WxPayMessageHandler implements PayMessageHandler<WxPayMessage, WxPayService> {

    private final MultiPayVerifyService multiPayVerifyService;
    private final MultiPayOrderService orderPaymentService;
    private final MultiPayRefundService multiPayRefundService;
    @Override
    public PayOutMessage handle(WxPayMessage payMessage, Map<String, Object> context, WxPayService payService) throws PayErrorException {
        log.info("::微信支付::notify::{}", Jackson.writeValueAsString(payMessage));
        Map<String, Object> payMessageDetail = payMessage.getPayMessage();
        Object refundStatus = payMessageDetail.get(WxConst.Refund.REFUND_STATUS);
        String outTradeNo = payMessage.getOutTradeNo();
        /*
         * 退款通知
         */
        if (refundStatus != null){
            log.info("::微信支付::notify::{}","退款回调");
            multiPayRefundService.refundNotify(PayType.WECHAT,payMessage);
            return payService.getPayOutMessage(NotifyResponse.WECHAT_SUCCESS.getCode(), NotifyResponse.WECHAT_SUCCESS.getMsg());
        }
        /*
         * 支付结果通知
         */
        log.info("::微信支付::notify::{}","支付回调");
        TradeStatus tradeStatus = multiPayVerifyService.verifyPayStatus(PayType.WECHAT, outTradeNo);
        log.info("::微信支付::notify::交易状态:{}",tradeStatus);
        if ( TradeStatus.SUCCESS ==  tradeStatus){
            orderPaymentService.success(payMessage);
        }
        INotifyResponse response = PayHelper.notifyResponse(tradeStatus, PayType.WECHAT);
        log.info("::::::支付回调完毕::::::");
        return payService.getPayOutMessage(response.getCode(), response.getMsg());
    }

}
