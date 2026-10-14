package com.medusa.gruul.finance.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * The enum Show mode enum.
 *
 * @description: ShowModeEnum
 * @author: alan
 * @date: 2024/2/22 16:54
 */
@Getter
@Schema(title = "佣金显示模式")
public enum ShowModeEnum {
    //类型：0->所有用户可见;2->不显示
    /**
     * 所有用户可见
     */
    ALL(0, "所有用户可见"),
    /**
     * 不显示
     */
    NONE(2, "不显示");

    /**
     * 值
     */
    @EnumValue
    private final int code;
    /**
     * 描述
     */
    private final String desc;


    ShowModeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
