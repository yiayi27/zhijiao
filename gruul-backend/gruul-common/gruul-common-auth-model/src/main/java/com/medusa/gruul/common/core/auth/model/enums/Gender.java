package com.medusa.gruul.common.core.auth.model.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 性别枚举
 * @author 张治保
 * date 2024/11/30
 */
@Getter
@RequiredArgsConstructor
public enum Gender {
    /**
     * 未知
     */
    UNKNOWN(0),
    /**
     * 男性
     */
    MALE(1),
    /**
     * 女性
     */
    FEMALE(2);
    @EnumValue
    @JsonValue
    private final Integer value;

}
