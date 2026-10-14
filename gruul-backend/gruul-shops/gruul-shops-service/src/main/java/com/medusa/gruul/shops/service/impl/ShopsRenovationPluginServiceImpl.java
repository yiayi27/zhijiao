package com.medusa.gruul.shops.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.shops.api.entity.ShopsRenovationPlugin;
import com.medusa.gruul.shops.mapper.ShopsRenovationPluginMapper;
import com.medusa.gruul.shops.model.ShopConstant;
import com.medusa.gruul.shops.model.ShopUtil;
import com.medusa.gruul.shops.model.param.ShopsRenovationPluginParam;
import com.medusa.gruul.shops.model.vo.ShopsRenovationPluginVo;
import com.medusa.gruul.shops.service.ShopsRenovationPluginService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author create by zq
 * @since created in 2024/01/14
 */
@Service
public class ShopsRenovationPluginServiceImpl extends ServiceImpl<ShopsRenovationPluginMapper, ShopsRenovationPlugin> implements ShopsRenovationPluginService {

    /**
     * 店铺装修模板 - 全局控件属性配置新增&修改
     *
     * @param param 店铺装修模板页面信息
     * @return Result
     */
    @Override
    @Redisson(name = ShopConstant.RENOVATION_PLUGIN_LOCK, key = "@S.user.shopId")
    public Result<Void> addPlugin(ShopsRenovationPluginParam param) {
        String shopId = ISystem.shopId();
        ShopUtil.deleteRenovationCache(
                shopId,
                () -> {
                    //如果已存在 则直接更新
                    if (this.lambdaQuery()
                            .eq(ShopsRenovationPlugin::getShopId, shopId)
                            .exists()
                    ) {
                        this.lambdaUpdate()
                                .set(ShopsRenovationPlugin::getPluginProperties, param.getPluginProperties())
                                .set(ShopsRenovationPlugin::getPluginNameCn, param.getPluginNameCn())
                                .set(ShopsRenovationPlugin::getPluginNameEn, param.getPluginNameEn())
                                .set(ShopsRenovationPlugin::getSpare, param.getSpare())
                                .eq(ShopsRenovationPlugin::getShopId, shopId)
                                .update();

                    } else {
                        this.save(
                                new ShopsRenovationPlugin()
                                        .setShopId(shopId)
                                        .setPluginProperties(param.getPluginProperties())
                                        .setSpare(param.getSpare())
                                        .setPluginNameCn(param.getPluginNameCn())
                                        .setPluginNameEn(param.getPluginNameEn())
                                        .setIsSelection("1")
                                        .setIsMandatory("0")
                        );
                    }
                }
        );
        return Result.ok();

    }


    /**
     * 店铺装修模板 - 全局控件属性配置查询list
     *
     * @return Result
     */
    @Override
    public List<ShopsRenovationPluginVo> listPlugin() {
        ShopsRenovationPlugin plugin = this.lambdaQuery()
                        .eq(ShopsRenovationPlugin::getShopId, ISystem.shopId())
                        .one();
        if (plugin == null) {
            return List.of(ShopsRenovationPluginVo.defaultPlugin());
        }
        return List.of(new ShopsRenovationPluginVo()
                .setPluginProperties(plugin.getPluginProperties())
                .setPluginNameCn(plugin.getPluginNameCn())
                .setPluginNameEn(plugin.getPluginNameEn())
                .setIsSelection(CommonConstants.STRING_ONE)
                .setIsMandatory(CommonConstants.STRING_ZERO));
    }


}
