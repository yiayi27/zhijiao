package com.medusa.gruul.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.payment.api.entity.Payment;
import com.medusa.gruul.payment.api.model.dto.PayStatusDto;
import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author whh
 * @since 2023-11-04
 */
public interface IPaymentService extends IService<Payment> {

    /**
     * 统一回调入口
     *
     * @param xmlResult xml
     * @return "" || success
     */
    String notify(String xmlResult);

    /**
     * 获取指定订单交易状态
     *
     * @param outTradeNo    业务订单号
     * @param payChannel    支付渠道
     * @param shopId      店铺标识
     * @param transactionId
     * @return 状态
     */
    PayStatusDto getPayStatus(String outTradeNo, String payChannel, String shopId, String transactionId);


    /**
     * 回调函数共用
     *
     * @param payment
     * @param decryptData
     * @return
     */
    String innerHandleCallback(Payment payment, String decryptData);


    /**
     * 退款回调
     *
     * @param request
     * @return string
     * @throws Exception
     */
    String wxPayRefundNotify(HttpServletRequest request) throws Exception;

}
