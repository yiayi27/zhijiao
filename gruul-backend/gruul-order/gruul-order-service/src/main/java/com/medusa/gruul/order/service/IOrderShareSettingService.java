package com.medusa.gruul.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.order.api.entity.OrderShareSetting;

/**
 * <p>
 * 订单设置表 服务类
 * </p>
 *
 * @author alan
 * @since 2023-09-02
 */
public interface IOrderShareSettingService extends IService<OrderShareSetting> {

    /**
     * Init.
     * @param shopId 店铺id
     */
    void init(String shopId);

    /**
     * Gets setting.
     *
     * @return the setting
     */
    OrderShareSetting getSetting();
}
