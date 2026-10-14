package com.medusa.gruul.shops.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.shops.api.entity.ShopsPartner;

import java.util.List;

/**
 * @author create by zq
 * @date created in 2024/01/14
 */
public interface ShopsPartnerService extends IService<ShopsPartner> {

    /**
     * 获取店铺list
     *
     * @return list
     */
    Result<List<ShopsPartner>> listShopsPartner();


    /**
     * 获取总店 by shopId
     *
     * @param shopId
     * @return shops
     */
    ShopsPartner oneByShopId(String shopId);

    /**
     * 新增默认店铺
     *
     * @param pass  密码
     * @param phone 手机号
     * @return Result
     */
    Result<ShopsPartner> saveShopsPartner(String pass, String phone);

}
