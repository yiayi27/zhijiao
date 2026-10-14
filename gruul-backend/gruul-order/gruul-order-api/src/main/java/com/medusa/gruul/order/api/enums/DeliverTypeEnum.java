package com.medusa.gruul.order.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 配送方式枚举
 *
 * @author alan
 * @date 2023/10/1 22:02
 */
@Getter
@Schema(title = "配送方式枚举")
public enum DeliverTypeEnum {
    /**
     * 用户自提
     */
    SELF_PICKUP(100, "用户自提"),
    /**
     * 送货上门
     */
    HOME_DELIVERY(101, "送货上门"),
    /**
     * 物流配送
     */
    LOGISTICS(102, "物流配送"),
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


    DeliverTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
