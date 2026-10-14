package com.medusa.gruul.goods.mapper.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;


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
public class SortingCategoryMapperTest {

    @Resource
    private SortingCategoryMapper sortingCategoryMapper;

    @Test
    public void querySortingCategoryList() {
        ISecurity.userMust().setShopId("100002100001");
        SortingCategoryParam sortingCategoryParam = new SortingCategoryParam();
        IPage<SortingCategoryVo> page = new Page<>(sortingCategoryParam.getCurrent(), sortingCategoryParam.getSize());
        List<SortingCategoryVo> sortingCategoryVos = sortingCategoryMapper.querySortingCategoryList(page, sortingCategoryParam);
        Assert.assertNotNull(sortingCategoryVos);
        (sortingCategoryVos.toString());
    }

    @Test
    public void queryAllSortingCategoryList() {
        ISecurity.userMust().setShopId("100002100001");
        SortingCategoryParam sortingCategoryParam = new SortingCategoryParam();
        List<SortingCategoryVo> sortingCategoryVos = sortingCategoryMapper.queryAllSortingCategoryList(sortingCategoryParam);
        Assert.assertNotNull(sortingCategoryVos);
        (sortingCategoryVos.toString());
    }

    @Test
    public void querySortingCategoryByProductId() {
        ISecurity.userMust().setShopId("100002100001");
        Long productId = 375L;
        SortingCategoryVo productVos = sortingCategoryMapper.querySortingCategoryByProductId(productId);
        Assert.assertNotNull(productVos);
        (productVos.toString());
    }

    @Test
    public void queryDefaultSortingCategory() {
        ISecurity.userMust().setShopId("100002100001");
        SortingCategoryVo sortingCategoryVo = sortingCategoryMapper.queryDefaultSortingCategory();
        Assert.assertNotNull(sortingCategoryVo);
        (sortingCategoryVo.toString());
    }
}