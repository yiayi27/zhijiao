package com.medusa.gruul.shops.model;

/**
 * @Description: shop常量
 * @Author: xiaoq
 * @Date: 2024/10/17 10:01
 */
public interface ShopConstant {
    /**
     * 默认引导页 1
     */
    String GUIDE_PAGE_ONE = "https://oss-tencent.bgniao.cn/api/guide_page1.png";

    /**
     * 默认引导页 2
     */
    String GUIDE_PAGE_TWO = "https://oss-tencent.bgniao.cn/api/guide_page2.png";

    /**
     * 店铺装修插件分布式锁 key 前缀
     */
    String RENOVATION_PLUGIN_LOCK = "shops:renovation:plugin:lock";


    /**
     * 店铺装修缓存前缀 + shopId
     */
    String RENOVATION_CACHE = "shops:renovation:cache";

    /**
     * 店铺装修页面缓存 key前缀 + shopId + pageId
     */
    String RENOVATION_ASSEMBLY_CACHE = "shops:renovation:page:cache";
}

