package com.medusa.gruul.sms.api.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 短信的模板类型枚举
 *
 * @author lcysike
 */
@Getter
@AllArgsConstructor
public enum SmsTemplateTypeEnum {
    // 验证码
    VERIFICATION_CODE(1),
    // 通知
    NOTICE(2),
    // 营销
    PROMOTION(3);

    /**
     * 类型
     */
    private final int type;

}
