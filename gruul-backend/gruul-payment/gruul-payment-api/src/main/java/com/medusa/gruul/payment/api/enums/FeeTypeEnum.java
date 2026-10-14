package com.medusa.gruul.payment.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author whh
 * @date 2023/11/06
 */

@Getter
@AllArgsConstructor
public enum FeeTypeEnum {

    /**
     * 货币类型 CNY：人民币(默认使用人民币作为单位)
     */
    CNY("CNY", "人民币(默认使用人民币作为单位)", "156"),
    /**
     * 美元
     */
    USD("USD","美元","-1");
    /**
     * 名称
     */
    private final String name;

    /**
     * 货币描述
     */
    private final String description;


    /**
     * ips 货币类型
     */
    private final String ipsCode;
}
