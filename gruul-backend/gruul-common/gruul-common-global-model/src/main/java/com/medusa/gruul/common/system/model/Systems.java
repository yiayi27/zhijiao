package com.medusa.gruul.common.system.model;

/**
 * @param shopId     店铺店铺id
 * @param ip         ip地址
 * @param platform   当前客户端平台
 * @param clientType 当前客户端类型
 * @author 张治保
 * @since 2025/3/31
 */
public record Systems(String shopId, String ip, Platform platform, ClientType clientType) {

    public static String DEFAULT_SHOP_ID = "0";

    public static final Systems EMPTY = new Systems(DEFAULT_SHOP_ID, null, null, null);

    /**
     * copy 一个新对象
     *
     * @return 新对象
     */
    public Systems copy(String newShopId) {
        return new Systems(newShopId, ip, platform, clientType);
    }
}
