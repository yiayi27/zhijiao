package com.medusa.gruul.goods.mapper.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.medusa.gruul.common.core.constant.CommonConstants;


import com.medusa.gruul.goods.api.model.param.manager.SupplierParam;
import com.medusa.gruul.goods.api.model.vo.manager.SupplierVo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@MybatisPlusTest
@ActiveProfiles("unit-test")
public class SupplierMapperTest {

    @Resource
    private SupplierMapper supplierMapper;

    @Test
    public void queryAllSupplierList() {
        ISecurity.userMust().setShopId("100002100001");
        List<SupplierVo> sortingCategoryVos = supplierMapper.queryAllSupplierList();
        Assert.assertNotNull(sortingCategoryVos);
        (sortingCategoryVos.toString());
    }

    @Test
    public void queryAllCount() {
        ISecurity.userMust().setShopId("100002100001");
        Integer count = supplierMapper.queryAllCount();
        Assert.assertNotNull(count);
        (count.toString());
    }

    @Test
    public void queryByPrimaryKey() {
        ISecurity.userMust().setShopId("100002100001");
        Long id = 83L;
        SupplierVo supplierVo = supplierMapper.queryByPrimaryKey(id);
        Assert.assertNotNull(supplierVo);
        (supplierVo.toString());
    }

    @Test
    public void querySupplierList() {
        ISecurity.userMust().setShopId("100002100001");
        SupplierParam supplierParam = new SupplierParam();
        IPage<SupplierVo> page = new Page<>(supplierParam.getCurrent(), supplierParam.getSize());
        List<SupplierVo> sortingCategoryVos = supplierMapper.querySupplierList(page, supplierParam);
        Assert.assertNotNull(sortingCategoryVos);
        (sortingCategoryVos.toString());
    }

    @Test
    public void queryDataSetSupplierList() {
        ISecurity.userMust().setShopId("100002100001");
        List<Long> supplierIds = new ArrayList<>(CommonConstants.NUMBER_TWO);
        supplierIds.add(83L);
        supplierIds.add(84L);
        List<SupplierVo> sortingCategoryVos = supplierMapper.queryDataSetSupplierList(supplierIds);
        Assert.assertNotNull(sortingCategoryVos);
        (sortingCategoryVos.toString());
    }
}