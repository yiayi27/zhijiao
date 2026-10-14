package com.medusa.gruul.goods.mapper.api;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;


import com.medusa.gruul.goods.api.model.vo.api.ApiShowCategoryVo;
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
public class ApiShowCategoryMapperTest {

    @Resource
    private ApiShowCategoryMapper apiShowCategoryMapper;

    @Test
    public void queryAllApiShowCategoryList() {
        ISecurity.userMust().setShopId("100002100001");
        List<ApiShowCategoryVo> apiShowCategoryVos = apiShowCategoryMapper.queryAllApiShowCategoryList();
        Assert.assertNotNull(apiShowCategoryVos);
        (apiShowCategoryVos.toString());
    }

    @Test
    public void queryApiSupermarketShowCategoryList() {
        ISecurity.userMust().setShopId("100002100001");
        Long saleMode = 0L;
        List<ApiShowCategoryVo> apiShowCategoryVos = apiShowCategoryMapper.queryApiSupermarketShowCategoryList(saleMode);
        Assert.assertNotNull(apiShowCategoryVos);
        (apiShowCategoryVos.toString());
    }

    @Test
    public void queryApiAssembleShowCategoryList() {
        ISecurity.userMust().setShopId("100002100001");
        Long saleMode = 0L;
        Integer productType = 0;
        String productIds = "374,375";
        List<ApiShowCategoryVo> apiShowCategoryVos = apiShowCategoryMapper.queryApiAssembleShowCategoryList(saleMode, productType, productIds);
        Assert.assertNotNull(apiShowCategoryVos);
        (apiShowCategoryVos.toString());
    }
}