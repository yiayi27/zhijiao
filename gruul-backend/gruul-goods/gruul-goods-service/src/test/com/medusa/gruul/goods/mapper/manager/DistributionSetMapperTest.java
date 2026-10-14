package com.medusa.gruul.goods.mapper.manager;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;


import com.medusa.gruul.goods.api.model.vo.manager.DistributionSetVo;
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
public class DistributionSetMapperTest {

    @Resource
    private DistributionSetMapper distributionSetMapper;

    @Test
    public void getByProductId() {
        ISecurity.userMust().setShopId("100002100001");
        Long id = 2L;
        List<DistributionSetVo> distributionSetVos = distributionSetMapper.getByProductId(id);
        Assert.assertNotNull(distributionSetVos);
        (distributionSetVos.toString());
    }
}