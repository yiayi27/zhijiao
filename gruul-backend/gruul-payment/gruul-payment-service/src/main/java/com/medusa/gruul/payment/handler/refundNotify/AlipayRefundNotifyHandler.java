package com.medusa.gruul.payment.handler.refundNotify;

import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.common.annotation.RefundNotifyHandler;
import com.medusa.gruul.payment.common.constant.AliConst;
import com.medusa.gruul.payment.service.IPaymentRefundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 张治保
 * date 2022/1/25
 */
@Slf4j
@Component
@RefundNotifyHandler(PayType.ALIPAY)
public class AlipayRefundNotifyHandler extends AbstractRefundNotifyHandler {

    public AlipayRefundNotifyHandler(IPaymentRefundService paymentRefundService, AmqpTemplate amqpTemplate) {
        super(paymentRefundService, amqpTemplate);
    }

    @Override
    protected String refundNo(Map<String, Object> message) {
        log.info("::::::支付宝退款回调::::::");
        return (String)message.get(AliConst.Refund.OUT_BIZ_NO);
    }
}
