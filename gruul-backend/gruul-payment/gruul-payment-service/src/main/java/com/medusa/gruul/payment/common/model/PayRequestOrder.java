package com.medusa.gruul.payment.common.model;

import com.egzosn.pay.spring.boot.core.bean.MerchantPayOrder;
import com.medusa.gruul.payment.api.enums.PayType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 支付订单详情
 * @author 张治保
 * date 2024/12/20
 */
@Getter
@Setter
@NoArgsConstructor
public class PayRequestOrder extends MerchantPayOrder {
    /**
     * 订单id
     */
    private String orderId;
    /**
     * 支付类型
     */
    private PayType payType;
    /**
     * 交易类型 1--JSAPI支付（小程序appId支付）、2--Native支付、3--app支付，4--JSAPI支付（公众号appId支付）   6-H5支付 ,必要参数
     */
    private Integer tradeType;
    /**
     * 异步接收支付结果通知的回调地址,回调业务处理成功之后需返回success未返回则会根据第三方回调次数进行多次返回,队列名和回调参数必须选一个
     */
    private String notifyUrl;
    /**
     * 路由键,路由键和回调url必须选一个
     * 用于消息队列监听数据
     */
    private String routeKey;
    /**
     * 店铺id
     */
    private String shopId;
}
