package com.medusa.gruul.platform.model;

import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.platform.constant.MeConstant;

import java.util.function.Supplier;

/**
 * @author 张治保
 * @since 2025/4/8
 */
public interface PlatformUtil {

    /**
     * 账号锁
     *
     * @param mobile   手机号
     * @param supplier 业务逻辑
     * @param <T>      返回值类型
     * @return 返回值
     */
    static <T> T accountLock(String mobile, Supplier<T> supplier) {
        return RedisUtil.lockRun(
                RedisUtil.getLockKey(MeConstant.ACCOUNT_LOCK_PREFIX, mobile),
                supplier
        );
    }

    /**
     * 账号锁
     *
     * @param mobile   手机号
     * @param runnable 业务逻辑
     */
    static void accountLock(String mobile, Runnable runnable) {
        RedisUtil.lockRun(
                RedisUtil.getLockKey(MeConstant.ACCOUNT_LOCK_PREFIX, mobile),
                runnable
        );
    }
}
