package com.medusa.gruul.integral.web.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 虚拟商品(积分商品 积分优惠券)状态枚举
 *
 * @author xiaoq
 * @since 2023-11-19 13:24
 */
@Getter
@Schema(title = "状态枚举")
public enum VirtualProductStatusEnum {
    /**
     * 下架
     */
    SELL_OFF(0, "下架"),
    /**
     * 上架
     */
    SELL_ON(1, "上架"),
    /**
     * 已售完
     */
    SELL_OUT(2, "已售完");
    @EnumValue
    /**
     * 值
     */ private final int status;

    /**
     * 描述
     */
    private final String desc;

    VirtualProductStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}
