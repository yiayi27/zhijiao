package com.medusa.gruul.solitaire.service.manager.impl;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.medusa.gruul.common.data.handler.IMetaObjectHandler;


import com.medusa.gruul.solitaire.api.model.dto.manager.SolitaireInfoDto;
import com.medusa.gruul.solitaire.api.model.vo.manager.SolitaireInfoVo;
import com.medusa.gruul.solitaire.service.manager.ISolitaireInfoService;
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

@RunWith(SpringRunner.class)
@MybatisPlusTest
@ActiveProfiles("unit-test")
//引入需要创建的Bean
@Import({SolitaireInfoServiceImpl.class, IMetaObjectHandler.class})
@ImportAutoConfiguration({
        RibbonAutoConfiguration.class,
        FeignRibbonClientAutoConfiguration.class,
        FeignAutoConfiguration.class})
public class SolitaireInfoServiceImplTest {

    @Resource
    private ISolitaireInfoService solitaireInfoService;


    @Test
    public void getInfo() {
        ISecurity.userMust().setShopId("100002100001");
        SolitaireInfoVo solitaireInfoVo = solitaireInfoService.getInfo();
        Assert.assertNotNull(solitaireInfoVo);
        (solitaireInfoVo.toString());
    }

    @Test
    public void saveInfo() {
        
        ISecurity.userMust().setShopId("100002100001");
        SolitaireInfoDto solitaireActivityDto = new SolitaireInfoDto();
        solitaireActivityDto.setId(1L);
        solitaireActivityDto.setActivityState(0);
        try{
            solitaireInfoService.saveInfo(solitaireActivityDto);
            Assert.assertTrue(true);
        }catch (Exception e){
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
}