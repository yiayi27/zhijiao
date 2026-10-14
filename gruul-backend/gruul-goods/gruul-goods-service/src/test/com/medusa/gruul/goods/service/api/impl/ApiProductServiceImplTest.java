package com.medusa.gruul.goods.service.api.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.medusa.gruul.assemble.api.feign.AssActAssociationService;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.data.handler.IMetaObjectHandler;
import com.medusa.gruul.common.data.tenant.CurUserContextHolder;


import com.medusa.gruul.discount.api.feign.RemoteDiscountService;
import com.medusa.gruul.discount.api.model.vo.api.ApiCouponVo;
import com.medusa.gruul.discount.api.model.vo.manager.FullScaleVo;
import com.medusa.gruul.goods.api.model.param.api.ApiProductParam;
import com.medusa.gruul.goods.api.model.vo.api.ApiAliveProductVo;
import com.medusa.gruul.goods.api.model.vo.api.ApiProductVo;
import com.medusa.gruul.goods.api.model.vo.api.ApiShowCategoryProductVo;
import com.medusa.gruul.goods.service.api.IApiProductService;
import com.medusa.gruul.shipping.api.feign.RemoteShippingFeignService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.ribbon.FeignRibbonClientAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@MybatisPlusTest
@ActiveProfiles("unit-test")
//引入需要创建的Bean
@Import({ApiProductServiceImpl.class, IMetaObjectHandler.class})
@ImportAutoConfiguration({
        RibbonAutoConfiguration.class,
        FeignRibbonClientAutoConfiguration.class,
        FeignAutoConfiguration.class})
public class ApiProductServiceImplTest {

    @Resource
    private IApiProductService apiProductService;

    @MockBean
    RemoteDiscountService remoteDiscountService;

    @MockBean
    RemoteShippingFeignService remoteShippingFeignService;

    @MockBean
    AssActAssociationService remoteAssActAssociationService;

    @Test
    public void getProductById() {
        ISecurity.userMust().setShopId("100002100001");
        Long id = 374L;
        ApiProductVo apiProductVo = apiProductService.getProductById(id);
        Assert.assertNotNull(apiProductVo);
        (apiProductVo.toString());
    }

    @Test
    public void getPageList() {
        ISecurity.userMust().setShopId("100002100001");
        ApiProductParam apiProductParam = new ApiProductParam();
        IPage<ApiAliveProductVo> page = apiProductService.getPageList(apiProductParam);
        Assert.assertNotNull(page.getRecords());
        (page.getRecords().toString());
    }

    @Test
    public void getApiAssembleProductList() {
        ISecurity.userMust().setShopId("100002100001");
        ApiProductParam apiProductParam = new ApiProductParam();
        List<ApiProductVo> apiProductVos = apiProductService.getApiAssembleProductList(JSONUtil.toJsonStr(apiProductParam));
        Assert.assertNotNull(apiProductVos);
        (apiProductVos.toString());
    }

    @Test
    public void getAssembleProductList() {
        ISecurity.userMust().setShopId("100002100001");
        ApiProductParam apiProductParam = new ApiProductParam();
        IPage<ApiAliveProductVo> page = apiProductService.getAssembleProductList(apiProductParam);
        Assert.assertNotNull(page.getRecords());
        (page.getRecords().toString());
    }

    @Test
    public void getSupermarketList() {
        ISecurity.userMust().setShopId("100002100001");
        ApiProductParam apiProductParam = new ApiProductParam();
        IPage<ApiAliveProductVo> page = apiProductService.getSupermarketList(apiProductParam);
        Assert.assertNotNull(page.getRecords());
        (page.getRecords().toString());
    }

    @Test
    public void getCouponByProductId() {
        ISecurity.userMust().setShopId("100002100001");
        CurUserContextHolder.setCurUser("account:eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2MDIzMjYzODV9.jUB3a4UOc8CSaHmWjg2SL3WkkUUVmMC4ehjsPCR94b8");
        Long productId = 374L;
        List<ApiCouponVo> apiCouponVos = apiProductService.getCouponByProductId(productId);
        Assert.assertNotNull(apiCouponVos);
        (apiCouponVos.toString());
    }

    @Test
    public void getFullScaleByProductId() {
        ISecurity.userMust().setShopId("100002100001");
        CurUserContextHolder.setCurUser("account:eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2MDIzMjYzODV9.jUB3a4UOc8CSaHmWjg2SL3WkkUUVmMC4ehjsPCR94b8");
        Long productId = 374L;
        List<FullScaleVo> fullScaleVos = apiProductService.getFullScaleByProductId(productId);
        Assert.assertNotNull(fullScaleVos);
        (fullScaleVos.toString());
    }

    @Test
    public void getAliveProductList() {
        ISecurity.userMust().setShopId("100002100001");
        Long[] ids = {374L};
        String launchArea = "";
        Long saleMode = 0L;
        List<ApiAliveProductVo> aliveProductVos = apiProductService.getAliveProductList(ids, launchArea, saleMode);
        Assert.assertNotNull(aliveProductVos);
        (aliveProductVos.toString());
    }

    @Test
    public void getSolitaireActivityProductByParam() {
        ISecurity.userMust().setShopId("100002100001");
        Integer productType = 0;
        Long[] productIds = {374L};
        Integer distributionMode = 0;
        Integer current = 1;
        Integer size = 10;
        IPage<ApiProductVo> page = apiProductService.getSolitaireActivityProductByParam(productType, productIds, distributionMode, current, size);
        Assert.assertNotNull(page.getRecords());
        (page.getRecords().toString());
    }

    @Test
    public void getSolitaireActivityProduct() {
        ISecurity.userMust().setShopId("100002100001");
        Integer productType = 0;
        List<Long> productIds = new ArrayList<>(CommonConstants.NUMBER_TWO);
        productIds.add(374L);
        List<ApiProductVo> apiProductVos = apiProductService.getSolitaireActivityProduct(productType, productIds);
        Assert.assertNotNull(apiProductVos);
        (apiProductVos.toString());
    }

    @Test
    public void getAliveProductListGroupByCategory() {
        ISecurity.userMust().setShopId("100002100001");
        Long[] ids = {374L};
        List<ApiShowCategoryProductVo> apiShowCategoryProductVos = apiProductService.getAliveProductListGroupByCategory(ids);
        Assert.assertNotNull(apiShowCategoryProductVos);
        (apiShowCategoryProductVos.toString());

    }

    @Test
    public void getAliveProductListByCategory() {
        ISecurity.userMust().setShopId("100002100001");
        Long[] ids = {374L};
        String launchArea = "";
        Long saleMode = 0L;
        List<ApiShowCategoryProductVo> apiShowCategoryProductVos = apiProductService.getAliveProductListByCategory(ids, launchArea, saleMode);
        Assert.assertNotNull(apiShowCategoryProductVos);
        (apiShowCategoryProductVos.toString());
    }
}