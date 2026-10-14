package com.medusa.gruul.goods.service.manager.impl;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.medusa.gruul.common.data.handler.IMetaObjectHandler;


import com.medusa.gruul.goods.api.entity.RebateSet;
import com.medusa.gruul.goods.api.model.dto.manager.RebateSetDto;
import com.medusa.gruul.goods.service.manager.IRebateSetService;
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

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@MybatisPlusTest
@ActiveProfiles("unit-test")
//引入需要创建的Bean
@Import({RebateSetServiceImpl.class, IMetaObjectHandler.class})
@ImportAutoConfiguration({
        RibbonAutoConfiguration.class,
        FeignRibbonClientAutoConfiguration.class,
        FeignAutoConfiguration.class})
public class RebateSetServiceImplTest {

    @Resource
    private IRebateSetService rebateSetService;

    @Test
    public void selectRebateSet() {
        
        ISecurity.userMust().setShopId("100002100001");
        RebateSet rebateSet = rebateSetService.selectRebateSet();
        Assert.assertNotNull(rebateSet);
        (rebateSet.toString());
    }

    @Test
    public void getRebateSet() {
        String shopId = "100002100001";
        
        ISecurity.userMust().setShopId(shopId);
        RebateSet rebateSet = rebateSetService.getRebateSet(shopId);
        Assert.assertNotNull(rebateSet);
        (rebateSet.toString());
    }

    @Test
    public void saveRebateSet() {
        
        ISecurity.userMust().setShopId("100002100001");
        RebateSetDto rebateSetDto = new RebateSetDto();
        rebateSetDto.setId(32L);
        rebateSetDto.setBalanceUseCondition("0,1");
        rebateSetDto.setRebateCondition("0,1");
        rebateSetDto.setRebateRatio(new BigDecimal(80));
        try{
            rebateSetService.saveRebateSet(rebateSetDto);
            Assert.assertTrue(true);
        }catch (Exception e){
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
}