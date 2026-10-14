package com.medusa.gruul.logistics.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;


import com.medusa.gruul.logistics.model.param.LogisticsExpressPrintParam;
import com.medusa.gruul.logistics.model.vo.LogisticsExpressPrintVo;
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
public class LogisticsExpressPrintMapperTest {

    @Resource
    private LogisticsExpressPrintMapper logisticsExpressPrintMapper;

    @Test
    public void queryAllLogisticsExpressPrintList() {
        
        ISecurity.userMust().setShopId("100002100001");
        LogisticsExpressPrintParam logisticsExpressPrintParam = new LogisticsExpressPrintParam();
        IPage<LogisticsExpressPrintVo> page = new Page<>(logisticsExpressPrintParam.getCurrent(), logisticsExpressPrintParam.getSize());
        List<LogisticsExpressPrintVo> logisticsExpressPrintVos = logisticsExpressPrintMapper.queryAllLogisticsExpressPrintList(page, logisticsExpressPrintParam);
        Assert.assertNotNull(logisticsExpressPrintVos);
        (logisticsExpressPrintVos.toString());
    }

    @Test
    public void queryLogisticsExpressPrintList() {
        
        ISecurity.userMust().setShopId("100002100001");
        LogisticsExpressPrintParam logisticsExpressPrintParam = new LogisticsExpressPrintParam();
        IPage<LogisticsExpressPrintVo> page = new Page<>(logisticsExpressPrintParam.getCurrent(), logisticsExpressPrintParam.getSize());
        List<LogisticsExpressPrintVo> logisticsExpressPrintVos = logisticsExpressPrintMapper.queryLogisticsExpressPrintList(page, logisticsExpressPrintParam);
        Assert.assertNotNull(logisticsExpressPrintVos);
        (logisticsExpressPrintVos.toString());
    }

    @Test
    public void queryLogisticsExpressPrintInfo() {
        
        ISecurity.userMust().setShopId("100002100001");
        Long id = 10L;
        LogisticsExpressPrintVo logisticsExpressPrintVo = logisticsExpressPrintMapper.queryLogisticsExpressPrintInfo(id);
        Assert.assertNotNull(logisticsExpressPrintVo);
        (logisticsExpressPrintVo.toString());
    }
}