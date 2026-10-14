package com.medusa.gruul.solitaire.mapper.api;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;


import com.medusa.gruul.solitaire.api.model.vo.api.ApiSolitaireActivityVo;
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
public class ApiSolitaireActivityMapperTest {

    @Resource
    private ApiSolitaireActivityMapper apiSolitaireActivityMapper;

    @Test
    public void queryCurrentActivity() {
        
        ISecurity.userMust().setShopId("100002100001");
        List<ApiSolitaireActivityVo> apiSolitaireActivityVos = apiSolitaireActivityMapper.queryCurrentActivity();
        Assert.assertNotNull(apiSolitaireActivityVos);
        (apiSolitaireActivityVos.toString());
    }

    @Test
    public void queryReadyAndGoingActivity() {
        
        ISecurity.userMust().setShopId("100002100001");
        List<ApiSolitaireActivityVo> apiSolitaireActivityVos = apiSolitaireActivityMapper.queryReadyAndGoingActivity();
        Assert.assertNotNull(apiSolitaireActivityVos);
        (apiSolitaireActivityVos.toString());
    }

    @Test
    public void queryActivityById() {
        
        ISecurity.userMust().setShopId("100002100001");
        Long activityId = 31L;
        ApiSolitaireActivityVo apiSolitaireActivityVo = apiSolitaireActivityMapper.queryActivityById(activityId);
        Assert.assertNotNull(apiSolitaireActivityVo);
        (apiSolitaireActivityVo.toString());
    }
}