package com.medusa.gruul.payment.handler.refund;

import cn.hutool.core.util.NumberUtil;
import com.egzosn.pay.ali.bean.AliPayMessage;
import com.egzosn.pay.ali.bean.AliRefundResult;
import com.egzosn.pay.common.bean.RefundOrder;
import com.egzosn.pay.common.bean.RefundResult;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.common.annotation.RefundHandler;
import com.medusa.gruul.payment.common.constant.AliConst;
import com.medusa.gruul.payment.common.constant.Snowflakes;
import com.medusa.gruul.payment.service.IPaymentRefundService;
import org.springframework.stereotype.Component;

/**
 * @author 张治保
 * date 2022/1/20
 */

@Component
@RefundHandler(PayType.ALIPAY)
public class AlipayRefundHandler extends AbstractRefundHandler{
    public AlipayRefundHandler(IPaymentRefundService paymentRefundService, PayServiceManager payServiceManager) {
        super(paymentRefundService, payServiceManager);
    }

    @Override
    protected RefundOrder refundOrder(Integer refundFee, String notifyParam) {
        RefundOrder refundOrder = new RefundOrder();
        refundOrder.setRefundNo(Snowflakes.REFUND_NO_SNOWFLAKE.nextIdStr());
        refundOrder.setRefundAmount(NumberUtil.div(refundFee, (Number) 100, 2));
        AliPayMessage aliPayMessage = Jackson.readValue(notifyParam, AliPayMessage.class);
        refundOrder.setTotalAmount(aliPayMessage.getTotalAmount());
        refundOrder.setOutTradeNo(aliPayMessage.getOutTradeNo());
        return refundOrder;
    }

    @Override
    protected Boolean refundSuccess(RefundResult refundResult) {
        AliRefundResult aliRefundResult = (AliRefundResult) refundResult;
        return AliConst.SUCCESS_CODE.equals(aliRefundResult.getCode());
    }
}
