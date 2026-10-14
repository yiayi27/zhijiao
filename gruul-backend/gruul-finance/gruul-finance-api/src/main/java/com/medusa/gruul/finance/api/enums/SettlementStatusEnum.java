package com.medusa.gruul.finance.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * The enum Settlement status enum.
 *
 * @description: SettlementStatusEnum
 * @author: alan
 * @date: 2024/2/22 16:54
 */
@Getter
@Schema(title = "结算状态枚举")
public enum SettlementStatusEnum {
    //结算状态：0->未确认；1->已确认；2->已撤销
    /**
     * 未确认
     */
    UNCONFIRMED(0, "未确认"),
    /**
     * 已确认
     */
    CONFIRMED(1, "已确认"),
    /**
     * 已撤销
     */
    REVOKED(2, "已撤销"),
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


    SettlementStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
