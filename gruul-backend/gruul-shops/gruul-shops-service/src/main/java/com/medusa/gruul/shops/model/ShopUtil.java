package com.medusa.gruul.shops.model;

import com.medusa.gruul.common.redis.util.RedisUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * @author 张治保
 * @since 2025/4/22
 */
public interface ShopUtil {

    /**
     * 删除装修缓存
     *
     * @param shopId 店铺id
     * @param task   删除任务
     */
    static void deleteRenovationCache(String shopId, Runnable task) {
        String key = RedisUtil.key(ShopConstant.RENOVATION_CACHE, shopId);
        String pattern = RedisUtil.key(ShopConstant.RENOVATION_ASSEMBLY_CACHE, shopId, "*");
        Set<String> keys = RedisUtil.keys(pattern);
        HashSet<String> deleteKeys = new HashSet<>(keys);
        deleteKeys.add(key);
        RedisUtil.doubleDeletion(
                task,
                () -> RedisUtil.delete(deleteKeys)
        );
    }
}
