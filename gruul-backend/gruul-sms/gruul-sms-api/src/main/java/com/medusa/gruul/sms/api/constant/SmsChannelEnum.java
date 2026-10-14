package com.medusa.gruul.sms.api.constant;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 短信渠道枚举
 *
 * @author lcysike
 */
@Getter
@AllArgsConstructor
public enum SmsChannelEnum {
    //阿里云
    ALIYUN("ALIYUN", "阿里云"),
    //腾讯云
    TENCENT("TENCENT", "腾讯云");

    /**
     * 编码
     */
    private final String code;
    /**
     * 名字
     */
    private final String name;

    public static SmsChannelEnum getByCode(String code) {
        return ArrayUtil.firstMatch(o -> o.getCode().equals(code), values());
    }

}

