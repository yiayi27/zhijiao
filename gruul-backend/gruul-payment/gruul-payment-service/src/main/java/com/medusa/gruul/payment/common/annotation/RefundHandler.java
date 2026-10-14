package com.medusa.gruul.payment.common.annotation;

import com.medusa.gruul.payment.api.enums.PayType;

import java.lang.annotation.*;

/**
 * 退款处理器注解
 * @author 张治保
 * date 2024/11/29
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RefundHandler {
    PayType value();
}
