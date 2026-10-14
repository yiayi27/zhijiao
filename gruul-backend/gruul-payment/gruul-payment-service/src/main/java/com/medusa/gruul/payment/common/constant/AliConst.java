package com.medusa.gruul.payment.common.constant;

/**
 * @author 张治保
 * date 2024/12/22
 */
public interface AliConst {

    String SUCCESSES = "SUCCESS";
    String CODE = "code";
    String SUCCESS_CODE = "10000";
    String MSG = "msg";
    String TRADE_STATUS = "trade_status";
    String SUB_CODE = "sub_code";
    String SUB_CODE_SUCCESS = "ACQ.TRADE_HAS_SUCCESS";
    String SUB_MSG = "sub_msg";

    String TRADE_QUERY_KEY ="alipay_trade_query_response";
    String REFUND_SUCCESS = "REFUND_SUCCESS";

    /**
     * 交易状态：
     * WAIT_BUYER_PAY（交易创建，等待买家付款）、
     * TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、
     * TRADE_SUCCESS（交易支付成功）、
     * TRADE_FINISHED（交易结束，不可退款）
     */
    interface TradeStatus{
        String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_CLOSED = "TRADE_CLOSED";
        String TRADE_SUCCESS = "TRADE_SUCCESS";
        String TRADE_FINISHED = "TRADE_FINISHED";
    }

    interface Refund{
        String REFUND_FEE = "refund_fee";
        String GMT_REFUND = "gmt_refund";
        String OUT_BIZ_NO = "out_biz_no";
    }

}
