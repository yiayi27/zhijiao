package com.medusa.gruul.shops.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.data.annotation.EscapeShop;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.shops.api.entity.ShopsRenovationAssembly;
import com.medusa.gruul.shops.api.entity.ShopsRenovationPage;
import com.medusa.gruul.shops.mapper.ShopsRenovationTemPageAssMapper;
import com.medusa.gruul.shops.model.ShopConstant;
import com.medusa.gruul.shops.model.ShopUtil;
import com.medusa.gruul.shops.model.param.ShopsRenovationAssemblyParam;
import com.medusa.gruul.shops.model.vo.ShopsRenovationAssemblyVo;
import com.medusa.gruul.shops.service.ShopsRenovationTemPageAssService;
import com.medusa.gruul.shops.service.ShopsRenovationTemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;


/**
 * @author create by zq
 * @date created in 2024/01/14
 */
@Service
public class ShopsRenovationTemPageAssServiceImpl extends ServiceImpl<ShopsRenovationTemPageAssMapper, ShopsRenovationAssembly> implements ShopsRenovationTemPageAssService {
    @Autowired
    ShopsRenovationTemPageService shopsRenovationTemPageService;
    @Autowired
    ShopsRenovationTemPageAssMapper shopsRenovationTemPageAssMapper;

    /**
     * 页面插件属性配置新增&修改
     *
     * @param params 店铺装修模板页面组件信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTemplatePageAssembly(List<ShopsRenovationAssemblyParam> params) {
        Long pageId = params.getFirst().getPageId();
        if (null == pageId) {
            throw SystemCode.PARAM_VALID_ERROR.exception();
        }
        String shopId = ISystem.shopId();
        ShopUtil.deleteRenovationCache(
                shopId,
                () -> {
                    //删除当前页面已存在的装修信息
                    this.lambdaUpdate()
                            .eq(ShopsRenovationAssembly::getPageId, pageId)
                            .remove();
                    List<ShopsRenovationAssembly> assemblies = new ArrayList<>();
                    for (int sort = 0; sort < params.size(); sort++) {
                        ShopsRenovationAssemblyParam param = params.get(sort);
                        assemblies.add(
                                new ShopsRenovationAssembly()
                                        .setPageId(pageId)
                                        .setProperties(param.getProperties())
                                        .setShopId(shopId)
                                        .setSort(sort)
                        );
                    }
                    this.saveBatch(assemblies);
                }
        );


    }


    /**
     * 店铺装修模板 - 页面插件属性配置逻辑删除 by ids
     *
     * @param ids 属性配置ids
     */
    @Override
    public void delTemplatePageAssembly(Set<Long> ids) {
        ShopUtil.deleteRenovationCache(
                ISystem.shopId(),
                () -> this.lambdaUpdate()
                        .in(ShopsRenovationAssembly::getId, ids)
                        .update()
        );
    }

    /**
     * 店铺装修模板 - 页面插件属性配置查询list
     *
     * @param param 店铺装修模板页面组件信息
     * @return Result
     */
    @Override
    public List<ShopsRenovationAssemblyVo> listTemplatePageAssembly(ShopsRenovationAssemblyParam param) {
        Long pageId = param.getPageId();
        if (pageId == null) {
            return List.of();
        }
        String cacheKey = RedisUtil.key(ShopConstant.RENOVATION_ASSEMBLY_CACHE, ISystem.shopId(), pageId);

        return RedisUtil.getCache(
                () -> {
                    List<ShopsRenovationAssemblyVo> cacheList = RedisUtil.getCacheList(cacheKey, ShopsRenovationAssemblyVo.class);
                    if (CollUtil.isEmpty(cacheList)) {
                        return null;
                    }
                    return cacheList;
                },
                () -> this.lambdaQuery()
                        .eq(ShopsRenovationAssembly::getPageId, pageId)
                        .list()
                        .stream()
                        .sorted(Comparator.comparingInt(ShopsRenovationAssembly::getSort))
                        .map(
                                assembly -> new ShopsRenovationAssemblyVo()
                                        .setId(assembly.getId())
                                        .setPageId(assembly.getPageId())
                                        .setProperties(assembly.getProperties())
                        )
                        .toList(),
                assemblies -> RedisUtil.setCacheList(cacheKey, assemblies),
                STR."\{cacheKey}_lock"
        );
    }


    /**
     * 店铺装修模板 - 页面插件属性配置查询list by page id
     *
     * @param pageId 页面id
     * @return List<ShopsRenovationAssembly>
     */
    @Override
    @EscapeShop
    public List<ShopsRenovationAssembly> listTemplatePageAssemblyByPageId(Long pageId) {
        return this.lambdaQuery()
                .eq(ShopsRenovationAssembly::getPageId, pageId)
                .list()
                .stream()
                .sorted(Comparator.comparingInt(ShopsRenovationAssembly::getSort))
                .toList();
    }


    /**
     * 修改专区名称
     *
     * @param shopId      店铺id
     * @param linkName    专区原名称
     * @param newLinkName 专区新名称
     * @return boolean
     */
    @Override
    public boolean updateSpecialArea(String shopId, String linkName, String newLinkName) {

        ShopsRenovationPage shopsRenovationPage = shopsRenovationTemPageService.getOne(
                new LambdaQueryWrapper<ShopsRenovationPage>()
                        .eq(ShopsRenovationPage::getIsDef, CommonConstants.NUMBER_ONE)
                        .last(CommonConstants.SQL_LIMIT_1));
        if (shopsRenovationPage == null) {
            return true;
        }
        Long pageId = shopsRenovationPage.getId();
        List<ShopsRenovationAssembly> shopsRenovationAssemblies = shopsRenovationTemPageAssMapper.listTemplatePageAssemblyByPageId(pageId);
        if (CollectionUtil.isEmpty(shopsRenovationAssemblies)) {
            return true;
        }
        String properties;
        for (ShopsRenovationAssembly shopsRenovationAssembly : shopsRenovationAssemblies) {
            properties = shopsRenovationAssembly.getProperties().replaceAll(STR."\"\{linkName}\"", STR."\"\{newLinkName}\"");
            shopsRenovationAssembly.setProperties(properties);
        }
        return this.updateBatchById(shopsRenovationAssemblies);
    }

}
