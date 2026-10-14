package com.medusa.gruul.finance.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * The enum Brokerage type enum.
 *
 * @description: BrokerageTypeEnum
 * @author: alan
 * @date: 2024/2/22 16:54
 */
@Getter
@Schema(title = "佣金类型枚举")
public enum BrokerageTypeEnum {
    //类型：0->普通佣金;1->激励计划
    /**
     * 普通佣金
     */
    ORDINARY(0, "普通佣金"),
    /**
     * 激励计划
     */
    INCENTIVE(1, "激励计划"),
    /**
     * 提货佣金
     */
    DELIVER(2, "提货佣金"),
    /**
     * 送货上门
     */
    HOME_DELIVERY(3, "送货上门"),
    /**
     * 月结佣金
     */
    MONTHLY(4, "月结佣金"),
    /**
     * 佣金提现
     */
    WITHDRAWAL(5, "佣金提现"),
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


    BrokerageTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
