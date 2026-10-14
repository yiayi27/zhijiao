package com.medusa.gruul.order.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.common.core.constant.Keys;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.order.api.entity.OrderShareSetting;
import com.medusa.gruul.order.mapper.OrderShareSettingMapper;
import com.medusa.gruul.order.service.IOrderShareSettingService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单设置表 服务实现类
 * </p>
 *
 * @author alan
 * @since 2023-09-02
 */
@Service
public class OrderShareSettingServiceImpl extends ServiceImpl<OrderShareSettingMapper, OrderShareSetting> implements IOrderShareSettingService {

    @Override
    public void init(String shopId) {
        OrderShareSetting shareSetting = this.lambdaQuery().eq(OrderShareSetting::getShopId, shopId).one();
        if (ObjectUtil.isNull(shareSetting)) {
            shareSetting = new OrderShareSetting();
            shareSetting.setShopId(shopId);
            this.save(shareSetting);
        }
    }

    @Override
    public OrderShareSetting getSetting() {
        String shopId = ISystem.shopId();
        OrderShareSetting shareSetting = Jackson.readValue(RedisUtil.getCacheObject(RedisUtil.key(Keys.ORDER_SHARE_SETTING, shopId)), OrderShareSetting.class);
        if (ObjectUtil.isNull(shareSetting)) {
            shareSetting = this.getOne(null);
            RedisUtil.setCacheObject(RedisUtil.key(Keys.ORDER_SHARE_SETTING, shopId), shareSetting);
        }
        return shareSetting;
    }
}
