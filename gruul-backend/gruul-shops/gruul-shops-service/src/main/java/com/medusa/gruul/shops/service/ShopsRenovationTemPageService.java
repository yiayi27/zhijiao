package com.medusa.gruul.shops.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.shops.api.entity.ShopsRenovationPage;
import com.medusa.gruul.shops.model.param.ShopsRenovationPageParam;
import com.medusa.gruul.shops.model.vo.ShopsRenovationPageVo;
import com.medusa.gruul.shops.model.vo.ShopsRenovationTemplateVo;

import java.util.List;
import java.util.Set;


/**
 * 店铺装修模板页面
 * @author create by zq
 * @date created in 2024/01/14
 */
public interface ShopsRenovationTemPageService extends IService<ShopsRenovationPage> {


    /**
     * 保存模板页面
     *
     * @param param
     * @return Result
     */
    void saveTemplatePage(ShopsRenovationPageParam param);


    /**
     * 删除商铺装修模板页面 by ids or templateId
     *
     * @param pageIds 页面ids
     * @return Result
     */
    void delTemplatePage(Set<String> pageIds);


    /**
     * 获取商铺装修模板页面list
     *
     * @param param 店铺装修模板页面信息
     * @return Result
     */
    PageUtils<ShopsRenovationPageVo> listTemplatePage(ShopsRenovationPageParam param);


    /**
     * 获取模版信息
     *
     * @param modelId 模板id
     * @return Result
     */
    List<ShopsRenovationPage> getByModelId(String modelId);

    /**
     * 获取已经完成装修的商品组件
     *
     * @param param 店铺装修模板页面信息
     * @return PageUtils<ShopsRenovationPageVo>
     */
    PageUtils<ShopsRenovationPageVo> fitmentPrefectureEndPage(ShopsRenovationPageParam param);

    /**
     * 删除专区时匹配删除 装修数据   装修未配置及删除成功同返回true
     *
     * @param modelId
     * @return
     */
    Boolean delShopRenovationPageByModelId(String modelId);


    /**
     * 小程序装修默认聚合接口
     *
     * @return Result
     */
    ShopsRenovationTemplateVo defOne();
}


