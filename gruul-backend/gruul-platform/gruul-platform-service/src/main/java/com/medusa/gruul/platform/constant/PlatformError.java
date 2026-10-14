package com.medusa.gruul.platform.constant;

import com.medusa.gruul.common.core.util.IResultCode;
import lombok.RequiredArgsConstructor;

/**
 * 平台错误码 100-9999
 *
 * @author 张治保
 * @since 2025/4/15
 */
@RequiredArgsConstructor
public enum PlatformError implements IResultCode {
    /**
     * 短信验证码已失效
     */
    SMS_CODE_EXPIRED(100, "短信验证码已失效"),

    /**
     * 短信验证码错误
     */
    SMS_CODE_ERROR(101, "短信验证码错误"),

    /**
     * 微信小程序密钥未配置
     */
    WECHAT_MINI_SECRET_NOT_CONFIG(102, "微信小程序密钥未配置"),
    ;

    private final int code;
    private final String msg;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
