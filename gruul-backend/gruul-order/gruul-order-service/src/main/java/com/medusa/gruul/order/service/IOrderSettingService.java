package com.medusa.gruul.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.order.api.entity.OrderSetting;

/**
 * <p>
 * 订单设置表 服务类
 * </p>
 *
 * @author alan
 * @since 2023-09-02
 */
public interface IOrderSettingService extends IService<OrderSetting> {

    /**
     * 初始化
     * @param shopId  店铺id
     */
    void init(String shopId);

    /**
     * 更新订单设置
     *
     * @param setting the setting
     * @return Result<OrderSetting>
     */
    OrderSetting update(OrderSetting setting);
}
