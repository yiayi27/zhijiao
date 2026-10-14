package com.medusa.gruul.payment.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2024/12/17
 */
@Getter
@RequiredArgsConstructor
public enum PayType {
    /**
     * aliPay 支付宝， wxPay微信..等等
     */
    BALANCE("balance"),
    /**
     * 微信支付
     */
    WECHAT("wxV3Pay"),
    /**
     * 支付宝
     */
    ALIPAY("aliPay");

    /**
     * 支付类型(支付渠道) 详情查看com.egzosn.pay.spring.boot.core.merchant.PaymentPlatform对应子类，
     */
    @EnumValue
    private final String platform;

}
