package com.medusa.gruul.order.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.order.api.entity.OrderSetting;
import com.medusa.gruul.order.mapper.OrderSettingMapper;
import com.medusa.gruul.order.service.IOrderSettingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 * 订单设置表 服务实现类
 * </p>
 *
 * @author alan
 * @since 2023-09-02
 */
@Service
public class OrderSettingServiceImpl extends ServiceImpl<OrderSettingMapper, OrderSetting> implements IOrderSettingService {

    /**
     * 初始化
     * @param shopId 店铺id
     */
    @Override
    public void init(String shopId) {
        OrderSetting setting = this.lambdaQuery().eq(OrderSetting::getShopId, shopId).one();
        if (ObjectUtil.isNull(setting)) {
            setting = new OrderSetting();
            setting.setShopId(shopId);
            setting.setFlashOrderOvertime(30L);
            setting.setNormalOrderOvertime(60L);
            setting.setConfirmOvertime(CommonConstants.NUMBER_SEVEN);
            setting.setFinishOvertime(CommonConstants.NUMBER_SEVEN);
            setting.setCommentOvertime(CommonConstants.NUMBER_THREE);
            setting.setOpenEvaluate(true);
            setting.setAfsApplyNumber(CommonConstants.NUMBER_THREE);
            setting.setMerchantConfirmOvertime(CommonConstants.NUMBER_THREE);
            setting.setUserReturnOvertime(CommonConstants.NUMBER_SEVEN);
            setting.setKdAppId("");
            setting.setKdAppKey("");
            setting.setPaymentModel(CommonConstants.STRING_ONE);
            setting.setCustomFrom("[{\"key\":\"买家留言\",\"type\":\"text\",\"required\":false," +
                    "\"placeholder\":\"请输入45个字内说明\"}]");
            setting.setRatio(BigDecimal.valueOf(0.3));
            setting.setCeiling(BigDecimal.valueOf(0.5));
            setting.setOrderNotify(false);
            this.save(setting);
        }
    }

    /**
     * 更新订单设置
     *
     * @param setting the setting
     * @return Result<OrderSetting>
     */
    @Override
    public OrderSetting update(OrderSetting setting) {
        if (ObjectUtil.isNotNull(setting.getId())) {
            this.saveOrUpdate(setting);
            return setting;
        }
        return null;
    }
}
