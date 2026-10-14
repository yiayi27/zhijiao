package com.medusa.gruul.finance.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * The enum Channel type enum.
 *
 * @description: ChannelTypeEnum
 * @author: alan
 * @date: 2024/2/22 16:54
 */
@Getter
@Schema(title = "提现渠道")
public enum ChannelTypeEnum {
    //提现渠道:0->微信；1->支付宝；2->银行卡；3->线下结算
    /**
     * 微信
     */
    WECAHT(0, "微信"),
    /**
     * 支付宝
     */
    ALIPAY(1, "支付宝"),
    /**
     * 银行卡
     */
    BANK(2, "银行卡"),
    /**
     * 线下结算
     */
    OFFLINE(3, "线下结算"),
    ;


    /**
     * 值
     */
    @EnumValue
    private final int code;
    /**
     * 描述
     */
    private final String desc;


    ChannelTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
