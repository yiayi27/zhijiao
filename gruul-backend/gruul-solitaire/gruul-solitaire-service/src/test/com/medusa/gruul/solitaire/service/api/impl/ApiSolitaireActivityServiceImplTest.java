package com.medusa.gruul.solitaire.service.api.impl;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.medusa.gruul.common.data.handler.IMetaObjectHandler;


import com.medusa.gruul.order.api.model.OrderVo;
import com.medusa.gruul.solitaire.service.api.IApiSolitaireActivityService;
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
import com.medusa.gruul.common.core.util.Result;


@RunWith(SpringRunner.class)
@MybatisPlusTest
@ActiveProfiles("unit-test")
//引入需要创建的Bean
@Import({ApiSolitaireActivityServiceImpl.class, IMetaObjectHandler.class})
@ImportAutoConfiguration({
        RibbonAutoConfiguration.class,
        FeignRibbonClientAutoConfiguration.class,
        FeignAutoConfiguration.class})
public class ApiSolitaireActivityServiceImplTest {

    @Resource
    private IApiSolitaireActivityService apiSolitaireActivityService;


    @Test
    public void getCurrentActivity() {
        
        ISecurity.userMust().setShopId("100002100001");
        Result result = apiSolitaireActivityService.getCurrentActivity();
        Assert.assertNotNull(result);
        (result.getData().toString());
    }

    @Test
    public void getCurrentActivityById() {
        
        ISecurity.userMust().setShopId("100002100001");
        CurUserContextHolder.setCurUser("account:eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2MDIzMjYzODV9.jUB3a4UOc8CSaHmWjg2SL3WkkUUVmMC4ehjsPCR94b8");
        Long id = 31L;
        Result result = apiSolitaireActivityService.getCurrentActivityById(id);
        Assert.assertNotNull(result);
        (result.getData().toString());
    }

    @Test
    public void getCurrentActivityProduct() {
        
        ISecurity.userMust().setShopId("100002100001");
        Long activityId = 31L;
        String path = "https://test.superprism.cn/";
        Result result = apiSolitaireActivityService.getCurrentActivityProduct(activityId, path);
        Assert.assertNotNull(result);
        (result.getData().toString());
    }

    @Test
    public void saveCurrentActivityRecord() {
        
        ISecurity.userMust().setShopId("100002100001");
        OrderVo orderVo = new OrderVo();
        orderVo.setSolitaireActivityId(31L);
        orderVo.setShopId("100002100001");
        orderVo.setId(1L);
        orderVo.setUserName("张三");
        try{
            apiSolitaireActivityService.saveCurrentActivityRecord(orderVo);
            Assert.assertTrue(true);
        }catch (Exception e){
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void getCurrentActivityRecord() {
        
        ISecurity.userMust().setShopId("100002100001");
        Long activityId = 31L;
        String path = "https://test.superprism.cn/";
        Result result = apiSolitaireActivityService.getCurrentActivityRecord(activityId, path);
        Assert.assertNotNull(result);
        (result.getData().toString());
    }

    @Test
    public void getActivityByProduct() {
        
        ISecurity.userMust().setShopId("100002100001");
        Long productId = 12873L;
        String path = "https://test.superprism.cn/";
        Result result = apiSolitaireActivityService.getActivityByProduct(productId);
        Assert.assertNotNull(result);
        (result.getData().toString());
    }
}