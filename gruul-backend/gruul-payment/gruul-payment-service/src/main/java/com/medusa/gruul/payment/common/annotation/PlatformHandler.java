package com.medusa.gruul.payment.common.annotation;


import com.medusa.gruul.common.system.model.Platform;

import java.lang.annotation.*;


/**
 * 各平台支付注解
 * @author 张治保
 * date 2024/11/29
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PlatformHandler {
    Platform[] value();
}
