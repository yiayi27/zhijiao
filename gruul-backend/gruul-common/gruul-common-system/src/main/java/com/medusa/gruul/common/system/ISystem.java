package com.medusa.gruul.common.system;

import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.system.model.ClientType;
import com.medusa.gruul.common.system.model.Platform;
import com.medusa.gruul.common.system.model.Systems;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author 张治保
 * @since 2025/3/31
 */
public interface ISystem {

    ScopedValue<Systems> SYSTEM = ScopedValue.newInstance();

    /**
     * 获取当前请求的系统信息
     *
     * @return 当前请求的系统信息
     */
    static Systems get() {
        return SYSTEM.isBound() ? SYSTEM.get() : Systems.EMPTY;
    }

    /**
     * 获取当前请求的店铺ID
     *
     * @return 当前请求的店铺ID
     */
    static String shopId() {
        return todo(Systems::shopId);
    }

    /**
     * 获取当前请求的IP地址
     *
     * @return 当前请求的IP地址
     */
    static String ip() {
        return todo(Systems::ip);
    }

    /**
     * 获取当前请求的平台类型
     * @return 当前请求的平台类型
     */
    static Platform platform() {
        return todo(Systems::platform);
    }

    /**
     * 获取当前请求的客户端类型
     *
     * @return 当前请求的客户端类型
     */
    static ClientType clientType() {
        return todo(Systems::clientType);
    }

    /**
     * 获取当前请求的系统信息
     *
     * @param getter 获取系统信息的函数
     * @return 系统信息
     */
    private static <T> T todo(Function<Systems, T> getter) {
        T apply = getter.apply(get());
        if (apply == null || (apply instanceof String str && str.isEmpty())) {
            throw new ServiceException(SystemCode.PARAM_MISS);
        }
        return apply;
    }

    /**
     * 切换多店铺
     *
     * @param shopId   店铺ID
     * @param supplier 获取系统信息的函数
     * @return 系统信息
     */
    static <T> T switchTenant(String shopId, Supplier<T> supplier) {
        return ScopedValue.getWhere(
                SYSTEM,
                get().copy(shopId),
                supplier);
    }

    /**
     * 切换多店铺
     *
     * @param shopId   店铺ID
     * @param runnable 执行的代码块
     */
    static void switchTenant(String shopId, Runnable runnable) {
        ScopedValue.runWhere(
                SYSTEM,
                get().copy(shopId),
                runnable
        );
    }

    /**
     * 禁用多店铺
     *
     * @param runnable 执行的代码块
     */
    static void disableTenant(Runnable runnable) {
        switchTenant(Systems.DEFAULT_SHOP_ID, runnable);
    }

    /**
     * 禁用多店铺
     *
     * @param supplier 获取系统信息的函数
     * @return 系统信息
     */
    static <T> T disableTenant(Supplier<T> supplier) {
        return switchTenant(Systems.DEFAULT_SHOP_ID, supplier);
    }
}
