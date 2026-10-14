package com.medusa.gruul.payment.handler.pay;

import com.egzosn.pay.ali.bean.AliTransactionType;
import com.egzosn.pay.common.bean.TransactionType;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.egzosn.pay.wx.v3.bean.WxTransactionType;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.system.model.Platform;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.api.model.pay.PayParam;
import com.medusa.gruul.payment.common.annotation.PlatformHandler;
import com.medusa.gruul.payment.common.model.PayRequestOrder;
import com.medusa.gruul.payment.service.MultiPayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * h5平台支付
 *
 * @author 张治保
 * date 2022/1/11
 */
@Slf4j
@Component
@PlatformHandler({Platform.PC})
public class PCPayHandler extends AbstractPlatformPayHandler {

    protected final PayServiceManager payServiceManager;

    public PCPayHandler(MultiPayOrderService multiPayOrderService, PayServiceManager payServiceManager) {
        super(multiPayOrderService);
        this.payServiceManager = payServiceManager;
    }

    @Override
    protected Object payData(PayParam payParam, PayRequestOrder payOrder) {
        log.info("===支付PC.正在生成二维码图像");
        PayType payType = payParam.getPayType();
        TransactionType transactionType = switch (payType) {
            case ALIPAY -> AliTransactionType.SWEEPPAY;
            case WECHAT -> WxTransactionType.NATIVE;
            default -> throw new IllegalStateException(STR."Unexpected value: \{payType}");
        };
        payOrder.setTransactionType(transactionType);
        try {
            return payServiceManager.toQrPay(payOrder);
        } catch (IOException exception) {
            log.error("生成图像时发生异常", exception);
            throw new ServiceException("生成图像时发生异常");
        }
    }
}
