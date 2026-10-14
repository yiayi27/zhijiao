package com.medusa.gruul.payment.service;

import com.egzosn.pay.common.bean.NoticeRequest;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.api.model.pay.OrderPayParam;
import com.medusa.gruul.payment.api.model.pay.PayParam;
import com.medusa.gruul.payment.api.model.pay.PayResult;

/**
 * @author 张治保
 * date 2021年12月17日
 */
public interface MultiPayService {

    /**
     * 订单支付跳转toPay
     *
     * @param params 订单支付参数
     * @return 拉起需要的数据
     */
    Object toPay(OrderPayParam params);

    /**
     * 支付
     *
     * @param payParam 支付参数
     * @return 支付结果
     */
    PayResult toPay(PayParam payParam);

    /**
     * notify 支付回调
     *
     * @param detailsId 支付配置详情id
     * @param request   回调数据
     * @return 是否成功
     */
    String notify(String detailsId, NoticeRequest request);


    /**
     * 渲染前端页面地址
     *
     * @param payType    支付方式
     * @param outTradeNo 外部订单号
     * @return 需要跳转的页面url
     */
    String viewJump(PayType payType, String outTradeNo);
}
