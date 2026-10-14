package com.medusa.gruul.payment.common.annotation;

import com.medusa.gruul.payment.api.enums.PayType;

import java.lang.annotation.*;

/**
 * 退款通知处理器
 * @author 张治保
 * date 2022/1/25
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RefundNotifyHandler {
    PayType value();
}
