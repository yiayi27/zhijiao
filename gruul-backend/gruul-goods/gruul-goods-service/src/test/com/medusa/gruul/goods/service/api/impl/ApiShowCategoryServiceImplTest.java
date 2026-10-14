package com.medusa.gruul.goods.service.api.impl;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.medusa.gruul.common.data.handler.IMetaObjectHandler;


import com.medusa.gruul.goods.api.model.vo.api.ApiShowCategoryVo;
import com.medusa.gruul.goods.service.api.IApiShowCategoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.ribbon.FeignRibbonClientAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.List;

@RunWith(SpringRunner.class)
@MybatisPlusTest
@ActiveProfiles("unit-test")
//引入需要创建的Bean
@Import({ApiShowCategoryServiceImpl.class, IMetaObjectHandler.class})
@ImportAutoConfiguration({
        RibbonAutoConfiguration.class,
        FeignRibbonClientAutoConfiguration.class,
        FeignAutoConfiguration.class})
public class ApiShowCategoryServiceImplTest {

    @Resource
    private IApiShowCategoryService apiShowCategoryService;

    @Test
    public void getAllApiShowCategoryList() {
        ISecurity.userMust().setShopId("100002100001");
        List<ApiShowCategoryVo> apiShowCategoryVos = apiShowCategoryService.getAllApiShowCategoryList();
        Assert.assertNotNull(apiShowCategoryVos);
        (apiShowCategoryVos.toString());
    }

    @Test
    public void getApiShowCategoryListBySaleMode() {
        ISecurity.userMust().setShopId("100002100001");
        Long saleMode = 0L;
        List<ApiShowCategoryVo> apiShowCategoryVos = apiShowCategoryService.getApiShowCategoryListBySaleMode(saleMode);
        Assert.assertNotNull(apiShowCategoryVos);
        (apiShowCategoryVos.toString());
    }
}