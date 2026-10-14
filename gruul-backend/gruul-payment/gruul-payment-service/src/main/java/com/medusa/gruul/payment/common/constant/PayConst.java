package com.medusa.gruul.payment.common.constant;

/**
 * @author 张治保
 * @since 2025/4/24
 */
public interface PayConst {

    /**
     * 支付配置缓存key
     */
    String PAY_CONFIG_CACHE_KEY = "payment:pay:config";


    /**
     * 支付方式和对应支付配置对应关系缓存key
     */
    String PAY_TYPE_CONFIG_CACHE_KEY = "payment:pay:type";
}
