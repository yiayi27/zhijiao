package com.medusa.gruul.discount.web.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 优惠劵有效时间类型枚举
 * @author lcysike
 * @since 2023-12-09
 */
@Getter
public enum CouponEffectiveTypeEnum {
    /**
     * 固定时间
     */
    TIME_BEGIN(0, "固定时间"),
    /**
     * 领劵立即生效
     */
    NOW_BEGIN(1, "领劵立即生效");

    @EnumValue
    /**
     * 值
     */
    private final int status;

    /**
     * 描述
     */
    private final String desc;

    CouponEffectiveTypeEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    /**
     * 根据状态值获取对应的枚举实例
     */
    public static CouponEffectiveTypeEnum fromStatus(int status) {
        for (CouponEffectiveTypeEnum type : values()) {
            if (type.getStatus() == status) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的状态值: " + status);
    }

}
