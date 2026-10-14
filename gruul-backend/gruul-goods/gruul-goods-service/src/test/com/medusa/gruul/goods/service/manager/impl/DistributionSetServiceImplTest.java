package com.medusa.gruul.goods.service.manager.impl;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.medusa.gruul.common.data.handler.IMetaObjectHandler;


import com.medusa.gruul.goods.api.model.vo.manager.DistributionSetVo;
import com.medusa.gruul.goods.service.manager.IDistributionSetService;
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

import java.util.List;


@RunWith(SpringRunner.class)
@MybatisPlusTest
@ActiveProfiles("unit-test")
//引入需要创建的Bean
@Import({DistributionSetServiceImpl.class, IMetaObjectHandler.class})
@ImportAutoConfiguration({
        RibbonAutoConfiguration.class,
        FeignRibbonClientAutoConfiguration.class,
        FeignAutoConfiguration.class})
public class DistributionSetServiceImplTest {

    @Resource
    private IDistributionSetService distributionSetService;

    @Test
    public void getByProductId() {
        ISecurity.userMust().setShopId("100002100001");
        Long id = 374L;
        List<DistributionSetVo> distributionSetVos = distributionSetService.getByProductId(id);
        Assert.assertNotNull(distributionSetVos);
        (distributionSetVos.toString());
    }
}