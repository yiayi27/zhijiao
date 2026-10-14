package com.medusa.gruul.finance.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * The enum Account type enum.
 *
 * @description: AccountTypeEnum.java
 * @author: alan
 * @date: 2024/2/22 16:54
 */
@Getter
@Schema(title = "账户类型枚举")
public enum AccountTypeEnum {
    /**
     * 用户
     */
    USER(3, "用户"),
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


    AccountTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
