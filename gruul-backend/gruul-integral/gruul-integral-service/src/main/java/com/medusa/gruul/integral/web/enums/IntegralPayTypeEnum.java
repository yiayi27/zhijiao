package com.medusa.gruul.integral.web.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @Description: 积分支付类型枚举
 * @Author: xiaoq
 * @Date: 2023/12/5 11:43
 */
@Getter
@Schema(title = "积分支付类型枚举")
public enum IntegralPayTypeEnum {

    /**
     *
     */
    WECHAT_PAY(101, "微信支付"),

    ALI_PAY(102, "支付宝支付"),

    BALANCE_PAY(100, "余额支付");


    @EnumValue
    /**
     * 值
     */ private final int status;

    /**
     * 描述
     */
    private final String desc;

    IntegralPayTypeEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}
