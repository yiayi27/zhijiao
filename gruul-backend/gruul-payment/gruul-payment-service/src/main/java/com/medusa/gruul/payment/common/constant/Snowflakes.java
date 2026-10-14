package com.medusa.gruul.payment.common.constant;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

public interface Snowflakes {
    /**
     * 商户id生成器
     */
    Snowflake MERCHANT_ID_SNOWFLAKE = IdUtil.getSnowflake(1, 1);
    /**
     * 退款单号生成器
     */
    Snowflake REFUND_NO_SNOWFLAKE = IdUtil.getSnowflake(1, 2);
    /**
     * 商户订单号生成器
     */
    Snowflake OUT_TRADE_NO_SNOWFLAKE = IdUtil.getSnowflake(1, 3);
}
