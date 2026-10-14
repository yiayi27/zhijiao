package com.medusa.gruul.payment.handler.refund;

import cn.hutool.core.util.NumberUtil;
import com.egzosn.pay.common.bean.RefundOrder;
import com.egzosn.pay.common.bean.RefundResult;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.egzosn.pay.wx.bean.WxRefundResult;
import com.egzosn.pay.wx.v3.bean.response.WxPayMessage;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.common.annotation.RefundHandler;
import com.medusa.gruul.payment.common.constant.Snowflakes;
import com.medusa.gruul.payment.common.constant.WxConst;
import com.medusa.gruul.payment.service.IPaymentRefundService;
import org.springframework.stereotype.Component;

/**
 * 微信退款处理器
 * @author 张治保
 * date 2022/1/20
 */

@Component
@RefundHandler(PayType.WECHAT)
public class WechatRefundHandler extends AbstractRefundHandler{
    public WechatRefundHandler(IPaymentRefundService paymentRefundService, PayServiceManager payServiceManager) {
        super(paymentRefundService, payServiceManager);
    }

    @Override
    protected RefundOrder refundOrder(Integer totalFee, String notifyParam) {
        RefundOrder refundOrder = new RefundOrder();
        refundOrder.setRefundNo(Snowflakes.REFUND_NO_SNOWFLAKE.nextIdStr());
        refundOrder.setRefundAmount(NumberUtil.div(totalFee, (Number) 100, 2));
        WxPayMessage wxPayMessage = Jackson.readValue(notifyParam, WxPayMessage.class);
        refundOrder.setTotalAmount(
            NumberUtil.div(wxPayMessage.getTotalFee(),100,2)
        );
        refundOrder.setOutTradeNo(wxPayMessage.getOutTradeNo());
        return refundOrder;
    }

    @Override
    protected Boolean refundSuccess(RefundResult refundResult) {
        WxRefundResult wxRefundResult = (WxRefundResult) refundResult;
        return WxConst.SUCCESSES.equals(wxRefundResult.getReturnCode()) && WxConst.SUCCESSES.equals(wxRefundResult.getResultCode());
    }
}
