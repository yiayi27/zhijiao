package com.medusa.gruul.payment.handler.pay;

import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.handler.Handler;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.payment.api.model.pay.PayParam;
import com.medusa.gruul.payment.api.model.pay.PayResult;
import com.medusa.gruul.payment.common.model.PayRequestOrder;
import com.medusa.gruul.payment.service.MultiPayOrderService;
import lombok.RequiredArgsConstructor;

/**
 * 各平台调起支付处理器 抽象超类
 * @author 张治保
 * date 2022/1/11
 */
@RequiredArgsConstructor
public abstract class AbstractPlatformPayHandler implements Handler<PayResult> {

    protected final MultiPayOrderService multiPayOrderService;
    @Override
    public PayResult handle(Object... params) {
        if (hasErrorParam(params, PayParam.class)) {
            throw new ServiceException(SystemCode.PARAM_VALID_ERROR);
        }
        PayParam payParam = cast(params[0], PayParam.class);
        PayRequestOrder payOrder = multiPayOrderService.pretreatment(payParam);
        return new PayResult()
            .setOutTradeNo(payOrder.getOutTradeNo())
            .setData(
                this.payData(payParam, payOrder)
            );
    }

    /**
     * 获取调起支付所需参数
     * @param payParam 支付参数
     * @param payOrder 支付订单
     * @return 调起支付所需数据
     */
    protected abstract Object payData(PayParam payParam, PayRequestOrder payOrder);
}
