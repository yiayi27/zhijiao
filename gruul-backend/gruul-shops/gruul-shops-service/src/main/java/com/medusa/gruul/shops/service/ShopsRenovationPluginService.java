package com.medusa.gruul.shops.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.shops.api.entity.ShopsRenovationPlugin;
import com.medusa.gruul.shops.model.param.ShopsRenovationPluginParam;
import com.medusa.gruul.shops.model.vo.ShopsRenovationPluginVo;

import java.util.List;


/**
 * @author create by zq
 * @date created in 2024/01/14
 */
public interface ShopsRenovationPluginService extends IService<ShopsRenovationPlugin> {


    /**
     * 店铺装修模板 - 全局控件属性配置新增&修改
     *
     * @param param 店铺装修模板页面信息
     * @return Result
     */
    Result<Void> addPlugin(ShopsRenovationPluginParam param);


    /**
     * 店铺装修模板 - 全局控件属性配置查询list
     *
     * @return Result
     */
    List<ShopsRenovationPluginVo> listPlugin();

}
