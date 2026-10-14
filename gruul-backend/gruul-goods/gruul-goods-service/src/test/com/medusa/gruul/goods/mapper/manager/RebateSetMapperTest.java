package com.medusa.gruul.goods.mapper.manager;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;


import com.medusa.gruul.goods.api.model.vo.manager.RebateSetVo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@MybatisPlusTest
@ActiveProfiles("unit-test")
public class RebateSetMapperTest {

    @Resource
    private RebateSetMapper rebateSetMapper;

    @Test
    public void queryDefaultRebateSet() {
        ISecurity.userMust().setShopId("100002100001");
        RebateSetVo rebateSetVo = rebateSetMapper.queryDefaultRebateSet();
        Assert.assertNotNull(rebateSetVo);
        (rebateSetVo.toString());
    }
}