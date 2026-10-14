package com.medusa.gruul.payment.common.constant;

/**
 * 微信支付常量
 * @author 张治保
 * date 2024/12/22
 */
public interface WxConst {
    String TRADE_TYPE = "trade_type";

    String SUCCESSES = "SUCCESS";
    String RETURN_CODE = "return_code";
    String RETURN_MSG = "return_msg";
    String RESULT_CODE = "result_code";
    String RESULT_MSG = "err_code_des";
    String TRADE_STATE = "trade_state";
    String TRADE_MSG = "trade_state_desc";
    String OUT_TRADE_NO = "out_trade_no";

    interface TradeStatus{
        String SUCCESS = "SUCCESS";
        String REFUND = "REFUND";
        String NOTPAY = "NOTPAY";
        String CLOSED = "CLOSED";
        String REVOKED = "REVOKED";
        String USERPAYING = "USERPAYING";
        String PAYERROR = "PAYERROR";
        String ACCEPT = "ACCEPT";
    }
    interface Refund{
        String REQ_INFO = "req_info";
        String REFUND_STATUS = "refund_status";
        String OUT_REFUND_NO= "out_refund_no";
        //退款状态
        String SUCCESS = "SUCCESS";
        String ERROR = "CHANGE";
        String CLOSE = "REFUNDCLOSE";

    }
}
