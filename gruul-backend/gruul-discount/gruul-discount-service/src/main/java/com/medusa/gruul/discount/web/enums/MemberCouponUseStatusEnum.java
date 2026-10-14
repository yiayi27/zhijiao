package com.medusa.gruul.discount.web.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 优惠劵状态枚举
 * @author lcysike
 * @since 2023-12-09
 */
@Getter
public enum MemberCouponUseStatusEnum {
    /**
     * 未开始
     */
    NOT_USE(0, "未使用"),
    /**
     * 进行中
     */
    HAS_USE(1, "已使用"),
    /**
     * 已结束
     */
    HAS_EXPIRE(2, "已过期");

    @EnumValue
    /**
     * 值
     */
    private final int status;

    /**
     * 描述
     */
    private final String desc;

    MemberCouponUseStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

}
