package com.medusa.gruul.goods.util;

import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.constant.Keys;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.goods.api.model.vo.api.ApiShoppingCartVo;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * @since 2025/4/25
 */
public interface GoodUtil {


    /**
     * 购物车sku缓存key
     *
     * @param shopId 店铺id
     * @param userId 用户id
     * @param skuId  sku id todo 不严谨 考虑加上商品id
     * @return 购物车sku缓存key
     */
    static String cartCacheKey(String shopId, String userId, Long skuId) {
        return RedisUtil.key(Keys.SHOPPING_CART, shopId, userId, skuId);
    }

    /**
     * 获取当前用户的购物车缓存列表 缓存的匹配key
     *
     * @param shopId 店铺id
     * @param userId 用户id
     * @return 购物车缓存列表匹配key
     */
    static String userCartCachePatternKey(String shopId, String userId) {
        return RedisUtil.key(Keys.SHOPPING_CART, shopId, userId, "*");
    }

    /**
     * 获取指定店铺的购物车缓存列表 缓存的匹配key
     *
     * @return 购物车缓存列表匹配key
     */
    static String allShopCartCachePatternKey(String shopId) {
        return RedisUtil.key(Keys.SHOPPING_CART, shopId, "*", "*");
    }

    /**
     * 获取所有店铺的购物车缓存列表 缓存的匹配key
     *
     * @return 购物车缓存列表匹配key
     */
    static String allCartCachePatternKey() {
        return RedisUtil.key(Keys.SHOPPING_CART, "*", "*", "*");
    }

    /**
     * 缓存购物车商品信息
     *
     * @param shopId 店铺id
     * @param userId 用户id
     * @param skuId  sku id todo 不严谨 考虑加上商品id
     * @param cart   购物车商品信息
     */
    static void cacheCartSkuInfo(String shopId, String userId, Long skuId, ApiShoppingCartVo cart) {
        RedisUtil.setCacheMap(cartCacheKey(shopId, userId, skuId), cart);
    }

    /**
     * 获取购物车商品信息
     *
     * @param shopId 店铺id
     * @param userId 用户id
     * @param skuId  sku id todo 不严谨 考虑加上商品id
     * @return 购物车商品信息
     */
    static ApiShoppingCartVo getCacheCartSkuInfo(String shopId, String userId, Long skuId) {
        return RedisUtil.getCacheMap(cartCacheKey(shopId, userId, skuId), ApiShoppingCartVo.class);
    }

    /**
     * 批量更新购物车选中状态
     *
     * @param keys         购物车商品缓存key
     * @param selectStatus 选中状态 0未选中 1选中
     */
    static void updateCartSelectStatus(Set<String> keys, boolean selectStatus) {
        RedisUtil.batchPutIfPresent(
                keys.stream()
                        .collect(
                                Collectors.toMap(
                                        key -> key,
                                        _ -> Map.of("selectStatus", selectStatus ? CommonConstants.NUMBER_ONE : CommonConstants.NUMBER_ZERO)
                                )
                        )
        );
    }
}
