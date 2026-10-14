package com.medusa.gruul.payment.handler.pay;

import com.egzosn.pay.common.http.UriVariables;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.system.model.Platform;
import com.medusa.gruul.payment.api.model.pay.PayParam;
import com.medusa.gruul.payment.common.annotation.PlatformHandler;
import com.medusa.gruul.payment.common.model.PayRequestOrder;
import com.medusa.gruul.payment.service.MultiPayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * app支付
 *
 * @author 张治保
 * date 2022/1/11
 */
@Slf4j
@Component
@PlatformHandler({Platform.APP})
public class AppPayHandler extends AbstractPlatformPayHandler {

    protected final PayServiceManager payServiceManager;

    public AppPayHandler(MultiPayOrderService multiPayOrderService, PayServiceManager payServiceManager) {
        super(multiPayOrderService);
        this.payServiceManager = payServiceManager;
    }

    @Override
    protected Object payData(PayParam payParam, PayRequestOrder payOrder) {
        log.info("===App支付:正在生成支付订单详情");

        Map<String, Object> orderInfo = payServiceManager.app(payOrder);
        return switch (payParam.getPayType()) {
            case ALIPAY -> UriVariables.getMapToParameters(orderInfo);
            case WECHAT -> orderInfo;
            default -> throw new ServiceException("暂不支持的支付方式.");
        };
    }
}
