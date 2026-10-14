package com.medusa.gruul.payment.service;


import com.egzosn.pay.common.bean.PayMessage;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.api.model.dto.RefundRequestDto;

/**
 * 支付退款服务
 * @author 张治保
 * date 2021年12月17日
 */
public interface MultiPayRefundService {

    /**
     * 退款申请
     * @param param 退款参数
     */
    void refund(RefundRequestDto param);

    /**
     * notify 退款回调
     * @param payType 支付类型
     * @param message 退款消息内容
     */
    void refundNotify(PayType payType,PayMessage message);
}
