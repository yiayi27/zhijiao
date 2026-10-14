package com.medusa.gruul.payment.common.enums;

/**
 * 交易状态枚举
 * @author 张治保
 * date 2024/12/23
 */
public enum  TradeStatus {
    /**
     * 支付成功
     */
    SUCCESS,
    /**
     * 支付关闭
     */
    CLOSED,
    /**
     * 等待支付
     */
    WAIT_FOR_PAY
}
