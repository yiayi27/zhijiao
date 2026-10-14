package com.medusa.gruul.solitaire.service.manager.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.data.handler.IMetaObjectHandler;


import com.medusa.gruul.goods.api.feign.RemoteGoodsService;
import com.medusa.gruul.solitaire.api.model.dto.manager.SolitaireActivityDto;
import com.medusa.gruul.solitaire.api.model.param.manager.SolitaireActivityParam;
import com.medusa.gruul.solitaire.api.model.vo.manager.SolitaireActivitySimpleVo;
import com.medusa.gruul.solitaire.api.model.vo.manager.SolitaireActivityVo;
import com.medusa.gruul.solitaire.service.manager.ISolitaireActivityService;
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

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@MybatisPlusTest
@ActiveProfiles("unit-test")
//引入需要创建的Bean
@Import({SolitaireActivityServiceImpl.class, IMetaObjectHandler.class})
@ImportAutoConfiguration({
        RibbonAutoConfiguration.class,
        FeignRibbonClientAutoConfiguration.class,
        FeignAutoConfiguration.class})
public class SolitaireActivityServiceImplTest {

    @Resource
    private ISolitaireActivityService solitaireActivityService;


    @MockBean
    RemoteGoodsService remoteGoodsService;

    @Test
    public void getActivityList() {
        
        ISecurity.userMust().setShopId("100002100001");
        SolitaireActivityParam solitaireActivityParam = new SolitaireActivityParam();
        IPage<SolitaireActivityVo> page = solitaireActivityService.getActivityList(solitaireActivityParam);
        Assert.assertNotNull(page.getRecords());
        (page.getRecords().toString());
    }

    @Test
    public void getActivityById() {
        
        ISecurity.userMust().setShopId("100002100001");
        Long id = 31L;
        SolitaireActivitySimpleVo solitaireActivitySimpleVo = solitaireActivityService.getActivityById(id);
        Assert.assertNotNull(solitaireActivitySimpleVo);
        (solitaireActivitySimpleVo.toString());
    }

    @Test
    public void addActivity() {
        
        ISecurity.userMust().setShopId("100002100001");
        SolitaireActivityDto solitaireActivityDto = new SolitaireActivityDto();
        solitaireActivityDto.setName("测试");
        solitaireActivityDto.setStartTime(DateUtil.endOfDay(new Date()));
        solitaireActivityDto.setEndTime(DateUtil.tomorrow());
        solitaireActivityDto.setPreheatHour(0);
        solitaireActivityDto.setProductType(0);
//        solitaireActivityDto.setContent("这是一场接龙活动");
        try{
            solitaireActivityService.addActivity(solitaireActivityDto);
            Assert.assertTrue(true);
        }catch (Exception e){
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void updateActivity() {
        
        ISecurity.userMust().setShopId("100002100001");
        SolitaireActivityDto solitaireActivityDto = new SolitaireActivityDto();
        solitaireActivityDto.setId(31L);
        solitaireActivityDto.setName("测试接龙活动");
        solitaireActivityDto.setStartTime(DateUtil.beginOfDay(new Date()));
        solitaireActivityDto.setEndTime(DateUtil.endOfDay(new Date()));
        solitaireActivityDto.setPreheatHour(0);
        solitaireActivityDto.setProductType(0);
        solitaireActivityDto.setContent("这是一场接龙活动");
        try{
            solitaireActivityService.updateActivity(solitaireActivityDto);
            Assert.assertTrue(true);
        }catch (Exception e){
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void changeActivityStatus() {
        
        ISecurity.userMust().setShopId("100002100001");
        Long id = 31L;
        try{
            solitaireActivityService.changeActivityStatus(id);
            Assert.assertTrue(true);
        }catch (Exception e){
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void deleteActivity() {
        
        ISecurity.userMust().setShopId("100002100001");
        Long id = 31L;
        try{
            solitaireActivityService.deleteActivity(id);
            Assert.assertTrue(true);
        }catch (Exception e){
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void findActivityListForTask() {
        
        ISecurity.userMust().setShopId("100002100001");
        List<SolitaireActivityVo> solitaireActivityVos = solitaireActivityService.findActivityListForTask();
        Assert.assertNotNull(solitaireActivityVos);
        (solitaireActivityVos.toString());
    }

    @Test
    public void updateActivityForTask() {
        
        ISecurity.userMust().setShopId("100002100001");
        SolitaireActivityVo solitaireActivityVo = new SolitaireActivityVo();
        solitaireActivityVo.setId(30L);
        solitaireActivityVo.setName("测试接龙活动");
        solitaireActivityVo.setStartTime(DateUtil.beginOfDay(new Date()));
        solitaireActivityVo.setEndTime(DateUtil.endOfDay(new Date()));
        solitaireActivityVo.setPreheatHour(0);
        solitaireActivityVo.setProductType(0);
        solitaireActivityVo.setStatus(0);
        solitaireActivityVo.setContent("这是一场接龙活动");
        try{
            solitaireActivityService.updateActivityForTask(solitaireActivityVo);
            Assert.assertTrue(true);
        }catch (Exception e){
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
}