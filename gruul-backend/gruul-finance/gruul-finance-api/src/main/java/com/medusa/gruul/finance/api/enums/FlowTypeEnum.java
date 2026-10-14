package com.medusa.gruul.finance.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * The enum Flow type enum.
 *
 * @description: FlowTypeEnum.java
 * @author: alan
 * @date: 2024/2/22 16:54
 */
@Getter
@Schema(title = "资金流向枚举")
public enum FlowTypeEnum {
    //资金流向:0->收入;1->支出
    /**
     * 收入
     */
    IN(0, "收入"),
    /**
     * 支出
     */
    OUT(1, "支出"),
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


    FlowTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
