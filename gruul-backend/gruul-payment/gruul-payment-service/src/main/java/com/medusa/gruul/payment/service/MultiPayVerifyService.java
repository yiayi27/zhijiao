package com.medusa.gruul.payment.service;

import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.common.enums.TradeStatus;

/**
 * 支付状态校验
 * @author 张治保
 * date 2021年12月17日
 */
public interface MultiPayVerifyService {

    /**
     * 校验支付状态
     * @param payType 支付类型
     * @param outTradeNo 外部订单号
     * @return 交易状态
     */
    TradeStatus verifyPayStatus(PayType payType, String outTradeNo);

    /**
     * 获取支付状态并尝试更新支付记录信息
     * @param payType 支付类型
     * @param outTradeNo 外部订单号
     * @return 交易状态
     */
    TradeStatus getPayStatus(PayType payType,String outTradeNo);
}
