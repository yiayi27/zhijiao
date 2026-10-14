package com.medusa.gruul.common.core.constant;

/**
 * @Description: CommonConstants.java
 * @Author: alan
 * @Date: 2023/7/13 19:22
 */
public interface CommonConstants {

    /**
     * 用户token
     */
    String TOKEN = "token";
    /**
     * 用户
     */
    String CUR_USER = "curUser";

    /**
     * header 中shopID
     */
    String SHOP_ID = "shopId";

    /**
     * header 版本号字段
     */
    String VERSION = "version";

    /**
     * 默认店铺ID
     */
    String DEFAULT_SHOP_ID = "0";
    /**
     * 删除
     */
    String STATUS_DEL = "1";
    /**
     * 正常
     */
    String STATUS_NORMAL = "0";

    /**
     * 编码
     */
    String UTF8 = "UTF-8";

    /**
     * 成功标记
     */
    Integer SUCCESS = 200;
    /**
     * 失败标记
     */
    Integer FAIL = 400;
    /**
     * 默认页码
     */
    Integer DEFAULT_PAGE_INDEX = 1;
    /**
     * 默认每页条数
     */
    Integer DEFAULT_PAGE_SIZE = 10;

    /**
     * 一小时的毫秒数
     */
    Integer SECOND_OF_HOUR = 3600000;

    /**
     * 一天小时数
     */
    Integer HOUR_OF_DAY = 24;

    /**
     * 乐观锁冲突最大重试次数
     */
    Integer DEFAULT_MAX_RETRIES = 5;

    /**
     * 店铺信息RedisKey
     */
    String GLOBAL_REDIS_KEY = "global:";

    /**
     * 店铺信息RedisKey
     */
    String SHOP_INFO_REDIS_KEY = GLOBAL_REDIS_KEY + "platform:shopId";


    /**
     * pc端用户信息RedisKey
     */
    String PC_INFO_REDIS_KEY = GLOBAL_REDIS_KEY + "platform:tk";

    /**
     * 小程序或h5用户redisKey
     */
    String MINI_ACCOUNT_REDIS_KEY = GLOBAL_REDIS_KEY + "account";


    /**
     * 小程序端或h5端用户token前缀
     */
    String ACCOUNT_TOKEN_PREFIX = "account";
    /**
     * pc用户端token前缀
     */
    String PLATFORM_TOKEN_PREFIX = "platform";

    String STRING_ERROR_CODE = "ERROR";
    String STRING_SUCCESS_CODE = "200";

    /****************  Long数字常量  *****************/
    long LONG_ZERO = 0L;
    long LONG_ONE = 1L;
    long LONG_TWO = 2L;
    long LONG_THREE = 3L;
    long LONG_FOUR = 4L;
    long LONG_FIVE = 5L;
    long LONG_SIX = 6L;
    long LONG_SEVEN = 7L;
    long LONG_EIGHT = 8L;
    long LONG_NINE = 9L;
    long LONG_TEN = 10L;
    long LONG_TWELVE = 12L;


    /****************  数字常量  *****************/
    Integer NUMBER_NEGATIVE_ONE = -1;
    Integer NUMBER_ZERO = 0;
    Integer NUMBER_ONE = 1;
    Integer NUMBER_TWO = 2;
    Integer NUMBER_THREE = 3;
    Integer NUMBER_FOUR = 4;
    Integer NUMBER_FIVE = 5;
    Integer NUMBER_SIX = 6;
    Integer NUMBER_SEVEN = 7;
    Integer NUMBER_EIGHT = 8;
    Integer NUMBER_NINE = 9;
    Integer NUMBER_TEN = 10;
    Integer NUMBER_ELEVEN = 11;
    Integer NUMBER_TWELVE = 12;

    /****************  字符串常量  *****************/
    String NEGATIVE_ONE = "-1";
    String STRING_ZERO = "0";
    String STRING_ONE = "1";
    String STRING_TWO = "2";
    String STRING_THREE = "3";
    String STRING_FOUR = "4";
    String STRING_FIVE = "5";
    String STRING_SIX = "6";
    String STRING_SEVEN = "7";
    String STRING_EIGHT = "8";
    String STRING_NINE = "9";
    String STRING_TEN = "10";
    String STRING_ELEVEN = "11";
    String STRING_TWELVE = "12";

    /****************  正则表达式常量  *****************/

    /**
     * 匹配是否网址
     */
    String REG_URL = "[a-zA-z]+://[^\\s]*";


    /**
     * 限制查询一条数据
     */
    String SQL_LIMIT_1 = "LIMIT 1";

}
