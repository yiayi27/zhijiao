package com.medusa.gruul.finance.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * The enum Trade type enum.
 *
 * @description: FlowTypeEnum.java
 * @author: alan
 * @date: 2024/2/22 16:54
 */
@Getter
@Schema(title = "交易类型枚举")
public enum TradeTypeEnum {
    //交易类型:0->商品交易;1->退款; 6->用户余额提现;
    /**
     * 商品交易
     */
    TRADE(0, "商品交易"),
    /**
     * 退款
     */
    RETURN(1, "退款"),
    /**
     * 用户余额提现
     */
    DISTRIBUTOR_FOR_USER(6, "用户余额提现"),
    /**
     * 用户返利
     */
    REBATE(7, "用户返利");


    /**
     * 值
     */
    @EnumValue
    private final int code;
    /**
     * 描述
     */
    private final String desc;


    TradeTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
