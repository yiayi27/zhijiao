package com.medusa.gruul.solitaire.mapper.api;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;


import com.medusa.gruul.solitaire.api.model.vo.api.ApiSolitaireRecordVo;
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
public class ApiSolitaireRecordMapperTest {

    @Resource
    private ApiSolitaireRecordMapper apiSolitaireRecordMapper;

    @Test
    public void getSolitaireRecord() {
        
        ISecurity.userMust().setShopId("100002100001");
        Long activityId = 30L;
        List<ApiSolitaireRecordVo> apiSolitaireRecordVos = apiSolitaireRecordMapper.getSolitaireRecord(activityId);
        Assert.assertNotNull(apiSolitaireRecordVos);
        (apiSolitaireRecordVos.toString());
    }
}