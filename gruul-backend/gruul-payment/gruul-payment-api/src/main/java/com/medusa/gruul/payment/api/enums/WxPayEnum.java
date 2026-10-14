package com.medusa.gruul.payment.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 微信交易类型
 *
 * @author whh
 */
@Getter
@AllArgsConstructor
public enum WxPayEnum {


    /**
     * 微信交易类型枚举
     */
    JSAPI_MINI(1, "JSAPI支付（小程序appId支付）", null),

    Native(2, "Native支付", null),

    APP(3, "app支付", null),

    JSAPI_MP(4, "JSAPI支付（公众号appId支付）", null),

    H5(6, "H5支付", null),

    /**
     * 支付宝支付
     */
    ALIPAY(401, "支付宝支付", "WAP"),
    /**
     * UNKNOWN
     */
    UNKNOWN(-1, "未知支付", null);


    /**
     * 类型
     */
    private final Integer type;

    /**
     * 描述
     */
    private final String description;

    /**
     * ips
     */
    private final String code;


    public static WxPayEnum getPayByTradeType(Integer type) {
        return Arrays.stream(values())
                .filter(wxPayEnum -> wxPayEnum.getType().equals(type))
                .findFirst()
                .orElse(WxPayEnum.UNKNOWN);
    }
}
