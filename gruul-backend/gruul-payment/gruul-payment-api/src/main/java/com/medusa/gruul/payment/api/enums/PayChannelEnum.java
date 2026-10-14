package com.medusa.gruul.payment.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author whh
 * @date 2023/11/06
 */

@Getter
@AllArgsConstructor
public enum PayChannelEnum {
    /**
     * 微信
     */
    WX(1, "微信支付");


    /**z
     * 类型
     */
    private Integer type;

    /**
     * 描述
     */
    private String description;
}
