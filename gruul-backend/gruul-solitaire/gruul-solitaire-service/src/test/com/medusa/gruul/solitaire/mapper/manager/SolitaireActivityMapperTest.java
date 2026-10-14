package com.medusa.gruul.solitaire.mapper.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;


import com.medusa.gruul.solitaire.api.model.param.manager.SolitaireActivityParam;
import com.medusa.gruul.solitaire.api.model.vo.manager.SolitaireActivityVo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.List;

@RunWith(SpringRunner.class)
@MybatisPlusTest
@ActiveProfiles("unit-test")
public class SolitaireActivityMapperTest {

    @Resource
    private SolitaireActivityMapper solitaireActivityMapper;

    @Test
    public void queryActivityList() {
        
        ISecurity.userMust().setShopId("100002100001");
        SolitaireActivityParam solitaireActivityParam = new SolitaireActivityParam();
        IPage<SolitaireActivityVo> page = new Page<>(solitaireActivityParam.getCurrent(), solitaireActivityParam.getSize());
        List<SolitaireActivityVo> solitaireActivityVos = solitaireActivityMapper.queryActivityList(page, solitaireActivityParam);
        Assert.assertNotNull(solitaireActivityVos);
        (solitaireActivityVos.toString());
    }

    @Test
    public void queryActivityById() {
        
        ISecurity.userMust().setShopId("100002100001");
        Long activityId = 31L;
        SolitaireActivityVo solitaireActivityVo = solitaireActivityMapper.queryActivityById(activityId);
        Assert.assertNotNull(solitaireActivityVo);
        (solitaireActivityVo.toString());
    }

    @Test
    public void queryValidActivity() {
        
        ISecurity.userMust().setShopId("100002100001");
        Long activityId = 0L;
        List<SolitaireActivityVo> solitaireActivityVos = solitaireActivityMapper.queryValidActivity(activityId);
        Assert.assertNotNull(solitaireActivityVos);
        (solitaireActivityVos.toString());
    }

    @Test
    public void queryActivityListForTask() {
        
        ISecurity.userMust().setShopId("100002100001");
        List<SolitaireActivityVo> solitaireActivityVos = solitaireActivityMapper.queryActivityListForTask();
        Assert.assertNotNull(solitaireActivityVos);
        (solitaireActivityVos.toString());
    }

    @Test
    public void updateActivityForTask() {
        
        ISecurity.userMust().setShopId("100002100001");
        SolitaireActivityVo solitaireActivityVo = new SolitaireActivityVo();
        solitaireActivityVo.setId(31L);
        solitaireActivityVo.setStatus(0);
        try{
            solitaireActivityMapper.updateActivityForTask(solitaireActivityVo);
            Assert.assertTrue(true);
        }catch (Exception e){
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
}