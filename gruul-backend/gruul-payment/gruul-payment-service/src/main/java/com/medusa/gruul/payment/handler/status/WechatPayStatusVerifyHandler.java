package com.medusa.gruul.payment.handler.status;

import cn.hutool.core.util.StrUtil;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.egzosn.pay.spring.boot.core.bean.MerchantQueryOrder;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.common.annotation.PayStatusVerifyHanlder;
import com.medusa.gruul.payment.common.constant.WxConst;
import com.medusa.gruul.payment.common.enums.TradeStatus;
import com.medusa.gruul.payment.service.MultiPayOrderService;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 张治保
 * date 2024/12/23
 */
@Component
@PayStatusVerifyHanlder(PayType.WECHAT)
public class WechatPayStatusVerifyHandler extends AbstractPayStatusVerifyHandler {

    private final PayServiceManager payServiceManager;

    public WechatPayStatusVerifyHandler(MultiPayOrderService multiPayOrderService, PayServiceManager payServiceManager) {
        super(multiPayOrderService);
        this.payServiceManager = payServiceManager;
    }

    @Override
    protected TradeStatus handle(String detailsId, String outTradeNo) {
        MerchantQueryOrder queryOrder = new MerchantQueryOrder();
        queryOrder.setDetailsId(detailsId);
        queryOrder.setOutTradeNo(outTradeNo);
        Map<String, Object> query = payServiceManager.query(queryOrder);
        if (this.isFail(query.get(WxConst.RETURN_CODE))) {
            throw new ServiceException((String) query.get(WxConst.RETURN_MSG));
        }
        if (this.isFail(query.get(WxConst.RESULT_CODE))) {
            throw new ServiceException((String) query.get(WxConst.RESULT_MSG));
        }
        String tradeState = (String) query.get(WxConst.TRADE_STATE);
        if (StrUtil.isBlank(tradeState)) {
            throw new ServiceException((String) query.get(WxConst.TRADE_MSG));
        }
        switch (tradeState) {
            case WxConst.TradeStatus.SUCCESS:
                return TradeStatus.SUCCESS;
            case WxConst.TradeStatus.NOTPAY:
            case WxConst.TradeStatus.USERPAYING:
            case WxConst.TradeStatus.ACCEPT:
                return TradeStatus.WAIT_FOR_PAY;
            case WxConst.TradeStatus.REFUND:
            case WxConst.TradeStatus.CLOSED:
            case WxConst.TradeStatus.REVOKED:
            case WxConst.TradeStatus.PAYERROR:
            default:
                return TradeStatus.CLOSED;
        }
    }


    private boolean isFail(Object state) {
        return !WxConst.SUCCESSES.equals(state);
    }
}
