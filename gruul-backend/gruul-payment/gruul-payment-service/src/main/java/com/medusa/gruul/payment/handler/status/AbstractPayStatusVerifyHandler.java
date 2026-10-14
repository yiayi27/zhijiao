package com.medusa.gruul.payment.handler.status;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.handler.Handler;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.payment.common.enums.TradeStatus;
import com.medusa.gruul.payment.common.model.OrderPaymentRecord;
import com.medusa.gruul.payment.service.MultiPayOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 支付状态校验器
 * @author 张治保
 * date 2024/12/23
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractPayStatusVerifyHandler implements Handler<TradeStatus> {

    private final MultiPayOrderService multiPayOrderService;

    @Override
    public TradeStatus handle(Object... params) {

        if (hasErrorParam(params, String.class)){
            throw new ServiceException(SystemCode.PARAM_BIND_ERROR);
        }
        String outTradeNo = cast(params[0],String.class);

        if (StrUtil.isBlank(outTradeNo) ) {
            throw new ServiceException(SystemCode.DATA_NOT_EXIST);
        }
        OrderPaymentRecord paymentRecord = multiPayOrderService.paymentRecord(outTradeNo);

        TradeStatus status = handle(paymentRecord.getDetailsId(), outTradeNo);
        log.info("支付状态检查结果:{}",status);
        return status;
    }

    /**
     * 处理支付状态
     *
     * @param detailsId 详情id
     * @param outTradeNo 商户订单号
     * @return 处理结果
     */
    protected abstract TradeStatus handle(String detailsId,String outTradeNo);
}
