package com.medusa.gruul.integral.api.constant;

/**
 * @Description: 积分商城常用常量
 * @Author: xiaoq
 * @Date: 2023/12/19 14:22
 */
public interface IntegralConstant {
	/**
	 * 積分模块默认交换机
	 */
	String EXCHANGE_NAME = "gruul.integral.exchange";

	/**
	 * 支付模块默认交换机
	 */
	String PAYMENT_EXCHANGE = "payment.notify.exchange";


	String ORDER_EXCHANGE_NAME = "gruul.order.direct";

	//积分所使用的常量
	//	===================================================================================================\

    Integer INTEGRAL_GOODS_NAME_LENGTH = 16;
	/**
	 * 会员权益  --------------》  积分权益
	 */
	String INTEGRAL_PRIVILEGE_TYPE = "3";

	/**
	 * 会员权益 ----------------》 包邮权益
	 */
	String INTEGRAL_POSTAGE_PRIVILEGE_TYPE = "2";

	/**
	 * 字段拼接展示 例如 3.0倍积分
	 */
	String INTEGRAL_PRIVILEGE_NAME = "倍积分";

    /**
     * 投放区域常量
     */
	String DEFAULT_STRING_ONE = "-1";

    /**
     * 12位货号最小值
     */
    Long MIN_ID = 100000000000L;

    /**
     * 12位货号最大值
     */
    Long MAX_ID = 999999999999L;


    double MIN_PAY_FEE = 0.01;

    /**
     * 无需物流发货
     */
    String NO_LOGISTICS = "1000";

    /**
     * 物流发货
     */
    String LOGISTICS = "1001";

	//单元测试
	// ================================================================================================================/
	/**
	 * 单元测试 偏不。 用戶token值
	 */
	String TEST_USER_TOKEN = "account:75075fce108a2fb8f42185c0e45f09b1eadd21181c23b5f5de251c7e65a73cae";

	/**
	 * 单元测试 布谷鸟·开发 平台token值
	 */
	String TEST_PLATFROM_TOKEN = "platform:tk:1f40c6591f33ec0c97add53a8bb58d8ct8l0rla7932e479e536daf728808a2eecdc07a";
	/**
	 * 单元测试 偏不。 userShopId值
	 */
	String TEST_USER_ID = "1260737223138086912";

    //快递相关常量
    // ================================================================================================================/

    String SF_CODE = "sf";

    String ZT_CODE = "zt";

    String YT_CODE = "yt";

    String YD_CODE = "yd";

    String HT_CODE = "ht";

    String SF_NAME = "顺丰快递";
    String ZT_NAME = "中通快递";
    String YT_NAME = "圆通快递";
    String YD_NAME = "韵达快递";
    String HT_NAME = "百世快递";

}
