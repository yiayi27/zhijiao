package com.medusa.gruul.payment.service.impl;

import com.egzosn.pay.common.bean.PayMessage;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.handler.Handler;
import com.medusa.gruul.common.core.util.SpringUtils;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.common.annotation.PayStatusVerifyHanlder;
import com.medusa.gruul.payment.common.enums.TradeStatus;
import com.medusa.gruul.payment.common.helper.PayHelper;
import com.medusa.gruul.payment.service.MultiPayOrderService;
import com.medusa.gruul.payment.service.MultiPayVerifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @author 张治保
 * date 2024/12/23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MultiPayVerifyServiceImpl implements MultiPayVerifyService {
    private final MultiPayOrderService multiPayOrderService;

    @Override
    public TradeStatus verifyPayStatus(PayType payType, String outTradeNo) {
        Handler<TradeStatus> tradeStatusHandler = SpringUtils.getBean(PayStatusVerifyHanlder.class, payType);
        if (tradeStatusHandler == null) {
            throw new ServiceException("系统异常,处理器不存在");
        }
        return tradeStatusHandler.handle(outTradeNo);
    }

    /**
     * 获取支付状态并尝试更新支付记录信息
     * @param payType 支付类型
     * @param outTradeNo 外部订单号
     * @return 交易状态
     */
    @Override
    public TradeStatus getPayStatus(PayType payType, String outTradeNo) {
        TradeStatus tradeStatus = this.verifyPayStatus(payType, outTradeNo);
        /*
         * 确认支付成功后是否已更新
         */
        if (TradeStatus.SUCCESS == tradeStatus) {
            try {
                multiPayOrderService.success(getMessage(payType, outTradeNo));
            } catch (Exception ignore) {
            }
        }
        return tradeStatus;
    }


    private PayMessage getMessage(PayType payType, String outTradeNo) {
        PayMessage payMessage = new PayMessage(
                new HashMap<String, Object>(6) {
                    {
                        put("notify", false);
                        put("type", payType.name());
                        put("out_trade_no", outTradeNo);
                    }
                }
        );
        payMessage.setPayType(
                PayHelper.wayType(ISystem.platform(), payType).getType()
        );
        return payMessage;
    }
}
