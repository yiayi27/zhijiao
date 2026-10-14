package com.medusa.gruul.payment.service;

import com.egzosn.pay.common.bean.PayMessage;
import com.medusa.gruul.payment.api.model.pay.MorePayParam;
import com.medusa.gruul.payment.api.model.pay.PayParam;
import com.medusa.gruul.payment.common.model.PayRequestOrder;
import com.medusa.gruul.payment.common.model.OrderPaymentRecord;


/**
 * @author 张治保
 * date 2024/12/17
 */
public interface MultiPayOrderService {
    /**
     * 支付订单预处理
     * @param payParam 支付参数
     * @return 支付订单
     */
    PayRequestOrder pretreatment(PayParam payParam);
    /**
     * 创建支付订单信息
     * @param payParam 支付参数
     * @param detailsId 支付详情id
     * @return 支付订单
     */
    PayRequestOrder createOrder(String detailsId, MorePayParam payParam);

    /**
     * 订单支付记录 初始化
     * @param payOrder 支付订单
     */
    void init(PayRequestOrder payOrder);

    /**
     * 支付成功后的处理逻辑
     * @param payMessage 支付消息
     */
    void success(PayMessage payMessage);

    /**
     * 获取支付记录
     * @param outTradeNo 商户订单号
     * @return 支付记录
     */
    OrderPaymentRecord paymentRecord(String outTradeNo);
}
