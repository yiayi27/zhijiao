package com.medusa.gruul.common.core.auth.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 认证注册方式
 * @author 张治保
 * date 2024/11/29
 */
@Getter
@RequiredArgsConstructor
public enum AuthPlatform {
    /**
     * 微信开放平台认证
     */
    WECHAT_OPEN(0),
    /**
     * 微信小程序认证
     */
    WECHAT_MINI_APP(1),
    /**
     * 手机验证
     */
    MOBILE_CODE(2);
    @EnumValue
    @JsonValue
    private final Integer value;

}
