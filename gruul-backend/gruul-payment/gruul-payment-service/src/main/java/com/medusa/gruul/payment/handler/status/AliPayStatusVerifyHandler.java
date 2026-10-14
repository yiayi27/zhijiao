package com.medusa.gruul.payment.handler.status;

import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.egzosn.pay.spring.boot.core.bean.MerchantQueryOrder;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.common.annotation.PayStatusVerifyHanlder;
import com.medusa.gruul.payment.common.constant.AliConst;
import com.medusa.gruul.payment.common.enums.TradeStatus;
import com.medusa.gruul.payment.service.MultiPayOrderService;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 张治保
 * date 2024/12/23
 */
@Component
@PayStatusVerifyHanlder(PayType.ALIPAY)
public class AliPayStatusVerifyHandler extends AbstractPayStatusVerifyHandler {

    private final PayServiceManager payServiceManager;

    public AliPayStatusVerifyHandler(MultiPayOrderService multiPayOrderService, PayServiceManager payServiceManager) {
        super(multiPayOrderService);
        this.payServiceManager = payServiceManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected TradeStatus handle(String detailsId, String outTradeNo) {
        /*
         * 查询交易订单
         */
        MerchantQueryOrder queryOrder = new MerchantQueryOrder();
        queryOrder.setDetailsId(detailsId);
        queryOrder.setOutTradeNo(outTradeNo);
        Map<String, Object> queryOut = payServiceManager.query(queryOrder);
        /*
         * 查询交易状态
         */
        Map<String, Object> query = (Map<String,Object>)queryOut.get(AliConst.TRADE_QUERY_KEY);
        if (query == null){
            throw new ServiceException(SystemCode.NOT_FOUND);
        }
        if (!AliConst.SUCCESS_CODE.equals(query.get(AliConst.CODE))){
            throw new ServiceException((String)query.get(AliConst.MSG));
        }
        /*
         * 返回对应状态
         */
        String tradeStatus = (String)query.get(AliConst.TRADE_STATUS);
        switch (tradeStatus){
            case AliConst.TradeStatus.TRADE_SUCCESS:
            case AliConst.TradeStatus.TRADE_FINISHED:
                return TradeStatus.SUCCESS;
            case AliConst.TradeStatus.WAIT_BUYER_PAY:
                return TradeStatus.WAIT_FOR_PAY;
            case AliConst.TradeStatus.TRADE_CLOSED:
                return TradeStatus.CLOSED;
            default:
                break;
        }
        throw new ServiceException("未找到对应的交易状态");
    }
}
