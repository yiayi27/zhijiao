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
 * 手机网站支付
 *
 * @author 张治保
 * date 2022/1/11
 */
@Slf4j
@Component
@PlatformHandler({Platform.H5})
public class H5PayHandler extends AbstractPlatformPayHandler {

    protected final PayServiceManager payServiceManager;

    public H5PayHandler(MultiPayOrderService multiPayOrderService, PayServiceManager payServiceManager) {
        super(multiPayOrderService);
        this.payServiceManager = payServiceManager;
    }


    @Override
    protected Object payData(PayParam payParam, PayRequestOrder payOrder) {
        log.info("===wap网站/手机网站正在生成支付表单:");
        return payServiceManager.toPay(payOrder);
    }
}
