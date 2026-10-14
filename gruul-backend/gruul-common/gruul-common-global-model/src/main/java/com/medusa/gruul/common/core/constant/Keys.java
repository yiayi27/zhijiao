package com.medusa.gruul.common.core.constant;

/**
 * redis缓存key
 *
 * @author 张治保
 * date 2024/12/4
 */
public interface Keys {
    String AUTH = "auth";
    /**
     * 用户手机号校验相关 如验证码
     */
    String MOBILE = AUTH + ":mobile";
    /**
     * 用户信息相关
     */
    String USER = AUTH + ":user";
    /**
     * 用户信息最后修改事件
     */
    String USER_LAST_MODIFY_TIME = USER + ":last-modify";
    /**
     * 用户token相关
     */
    String USER_PLATFORM_JTI = USER + ":platform:tokenId";
    /**
     * 用户额外信息相关
     */
    String USER_EXTENDS = USER + ":extends";
    /**
     * 用户登录信息相关
     */
    String USER_LOGIN = USER + ":login";

    /**
     * 用户账号信息相关
     */
    String USER_ACCOUNT = "account";

    /**
     * 小程序或h5用户redisKey
     */
    String MINI_ACCOUNT_REDIS_KEY = "global:account";

    //-------------------平台服务---------------------
    /**
     * 平台配置信息
     */
    String PLATFORM = "platform";

    /**
     * pc端用户信息RedisKey
     */
    String PC_INFO_REDIS_KEY = "global:platform:tk";

    /**
     * 商户申请代理
     */
    String MERCHANTS_APPLY_AGENT_LIMIT = "MERCHANTS-APPLY-AGENT-LIMIT:";

    //-------------------oss服务---------------------
    /**
     * oss信息
     */
    String OSS = "oss";
    /**
     * oss配置信息
     */
    String OSS_CONFIG = "Oss:Config";

    //-------------------订单服务---------------------
    /**
     * 订单晒单信息
     */
    String ORDER_SHARE_SETTING = "Order:Share:Setting:";

    /**
     * 订单创建失败信息
     */
    String ORDER_FAILED = "Order:Failed:";

    //-------------------商品服务---------------------
    /**
     * 商品信息
     */
    String GOODS_PRODUCT = "Goods:Product:";

    /**
     * 商品sku库存
     */
    String GOODS_SKU_STOCK = "Goods:Sku:Stock";

    /**
     * 购物车商品信息
     */
    String SHOPPING_CART = "Shopping:Cart";

    //-------------------接龙服务---------------------
    /**
     * 资讯阅读信息
     */
    String INFORMATION_READ = "Information:Read:";

    //-------------------店铺服务---------------------
    /**
     * 店铺装修信息
     */
    String SHOPS_RENOVATION = "Information:Read:";


}
