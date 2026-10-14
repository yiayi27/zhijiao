package com.medusa.gruul.afs.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;


/**
 * The enum Afs order type enum.
 *
 * @author alan
 * @Description: 售后订单类型枚举
 * @date 2023/10/1 22:02
 */
@Getter
@Schema(title = "售后订单类型枚举")
public enum AfsOrderTypeEnum {
    /**
     * Point exchange afs order type enum.
     */
    POINT_EXCHANGE(1, "补货"),
    /**
     * Point refund afs order type enum.
     */
    POINT_REFUND(2, "退款"),
    /**
     * Refund afs order type enum.
     */
    REFUND(3, "退款"),
    /**
     * Exchange afs order type enum.
     */
    EXCHANGE(4, "重新发货"),
    /**
     * Return refund afs order type enum.
     */
    RETURN_REFUND(5, "退货退款"),
    /**
     * Return refund afs order type enum.
     */
    RETURN_EXCHANGE(6, "退货换货");


    /**
     * 值
     */
    @EnumValue
    private final int code;
    /**
     * 描述
     */
    private final String desc;

    AfsOrderTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * Is leader apply integer.
     *
     * @return the integer
     */
    public Integer isLeaderApply() {
        if (this.equals(POINT_EXCHANGE) || this.equals(POINT_REFUND)) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Is exchange boolean.
     *
     * @return the boolean
     */
    public Boolean isExchange() {
        return this.equals(EXCHANGE) || this.equals(POINT_EXCHANGE);
    }

    /**
     * Is refund boolean.
     *
     * @return the boolean
     */
    public Boolean isRefund() {
        return this.equals(REFUND) || this.equals(RETURN_REFUND);
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        switch (this) {
            case POINT_EXCHANGE:
            case EXCHANGE:
                return "HH";
            case POINT_REFUND:
            case REFUND:
                return "TK";
            case RETURN_REFUND:
                return "TH";
            default:
                return "";
        }
    }
}
