package com.medusa.gruul.platform.constant;

import com.medusa.gruul.common.core.constant.Keys;

/**
 * 魔法值常量(无法直接使用字符串,仅为去除p3c报错)
 *
 * @author whh
 */
public interface MeConstant {

    int STATUS_OK = 200;
    int STATUS_502 = 502;
    int STATUS_404 = 404;

    String SHOP_CREATE_LOCK_PREFIX = Keys.PLATFORM + ":shop:create";

    /**
     * 开放平台二维码 扫描状态 缓存key
     */
    String WECHAT_OPEN_QR_CACHE = "wechat:open:qr";

    /**
     * 个人账号资料分布式锁前缀 后面追加手机号
     */
    String ACCOUNT_LOCK_PREFIX = Keys.PLATFORM + ":accountlock";
    

    /**
     * 店铺小程序码缓存key
     */
    String SHOP_MINIAPP_CODE_CACHE_PREFIX = Keys.PLATFORM + ":shop:miniapp:code";

}
