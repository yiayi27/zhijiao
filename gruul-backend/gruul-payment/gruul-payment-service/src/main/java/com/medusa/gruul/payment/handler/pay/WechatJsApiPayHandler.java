package com.medusa.gruul.payment.handler.pay;

import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.medusa.gruul.common.system.model.Platform;
import com.medusa.gruul.payment.api.model.pay.PayParam;
import com.medusa.gruul.payment.common.annotation.PlatformHandler;
import com.medusa.gruul.payment.common.model.PayRequestOrder;
import com.medusa.gruul.payment.service.MultiPayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 微信原生
 *
 * @author 张治保
 * date 2022/1/11
 */
@Slf4j
@Component
@PlatformHandler({Platform.MINI_APP})
public class WechatJsApiPayHandler extends AbstractPlatformPayHandler {

    protected final PayServiceManager payServiceManager;

    public WechatJsApiPayHandler(MultiPayOrderService multiPayOrderService, PayServiceManager payServiceManager) {
        super(multiPayOrderService);
        this.payServiceManager = payServiceManager;
    }

    @Override
    protected Object payData(PayParam payParam, PayRequestOrder payOrder) {
        log.info("===微信JSAPI支付:正在生成微信支付订单详情");
        return payServiceManager.getOrderInfo(payOrder);
    }
}
