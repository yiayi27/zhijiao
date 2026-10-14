package com.medusa.gruul.discount.web.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 满减活动状态枚举
 * @author lcysike
 * @since 2023-12-09
 */
@Getter
public enum FullScaleStatusEnum {
    /**
     * 未开始
     */
    BE_GOING(0, "未开始"),
    /**
     * 进行中
     */
    ON_GOING(1, "进行中"),
    /**
     * 已结束
     */
    OVER(2, "已结束");

    @EnumValue
    /**
     * 值
     */
    private final int status;

    /**
     * 描述
     */
    private final String desc;

    FullScaleStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

}
