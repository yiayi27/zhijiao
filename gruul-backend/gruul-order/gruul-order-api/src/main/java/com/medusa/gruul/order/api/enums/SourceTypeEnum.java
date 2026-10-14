package com.medusa.gruul.order.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 订单来源枚举
 *
 * @author alan
 * @date 2023/10/1 22:02
 */
@Getter
@Schema(title = "订单来源枚举")
public enum SourceTypeEnum {

    /**
     * 小程序
     */
    MINI_PROGRAM(100, "小程序"),
    /**
     * 接龙
     */
    SOLITAIRE(101, "接龙"),
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

    SourceTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
