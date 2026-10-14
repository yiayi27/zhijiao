package com.medusa.gruul.platform.api.model;

/**
 * @author 张治保
 * @since 2025/4/19
 */
public interface PlatformConstant {
    /**
     * 店铺信息缓存前缀
     */
    String SHOP_INFO_CACHE_PREFIX = "platform:shop:info";


    /**
     * 店铺小程序 appid 与密钥缓存key 前缀 后面追加店铺id
     * 字段 appid、secret
     */
    String SHOP_MINIAPP_SECRET_CACHE_PREFIX = "platform:shop:miniapp:secret";


}
