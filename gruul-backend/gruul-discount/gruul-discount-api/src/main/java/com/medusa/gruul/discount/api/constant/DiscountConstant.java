package com.medusa.gruul.discount.api.constant;

/**
 * 优惠劵常量
 *
 * @author shuke
 */
public interface DiscountConstant {

    /**
     * 16位货号最小值
     */
    Long MIN_ID = 1000000000000000L;

    /**
     * 16位货号最大值
     */
    Long MAX_ID = 9999999999999999L;

    /**
     * 一小时的毫秒数
     */
    int SECOND_OF_HOUR = 3600000;

    /**
     * 一天的小时数
     */
    int HOUR_OF_DAY = 24;

    /**
     * 测试店铺shopId
     */
    String TEST_SHOP_ID = "100002100001";

    /**
     * 测试店铺shopId
     */
    String TEST_USER_TOKEN = "account:e0e8167bc903519c7dd7b7961d7e248ba41c7efb99c16c7d55667f8b0942d0e4";


}
