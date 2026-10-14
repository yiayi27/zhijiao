package com.medusa.gruul.payment.common.annotation;

import com.medusa.gruul.payment.api.enums.PayType;

import java.lang.annotation.*;

/**
 * @author 张治保
 * date 2024/12/23
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PayStatusVerifyHanlder {
    PayType value();
}
