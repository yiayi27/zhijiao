package com.medusa.gruul.discount.web.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 优惠劵适用商品类型枚举
 * @author lcysike
 * @since 2023-12-09
 */
@Getter
public enum CouponProductTypeEnum {
    /**
     * 全部商品参与
     */
    ALL_JOIN(0, "全部商品参与"),
    /**
     * 指定商品参与
     */
    SOME_JOIN(1, "指定商品参与"),
    /**
     * 指定商品不参与
     */
    SOME_NOT_JOIN(2, "指定商品不参与");

    @EnumValue
    /**
     * 值
     */
    private final int status;

    /**
     * 描述
     */
    private final String desc;

    CouponProductTypeEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

}
