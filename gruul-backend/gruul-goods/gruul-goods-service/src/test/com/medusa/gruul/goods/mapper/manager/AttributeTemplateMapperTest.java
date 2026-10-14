package com.medusa.gruul.goods.mapper.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;


import com.medusa.gruul.goods.api.model.param.manager.AttributeTemplateParam;
import com.medusa.gruul.goods.api.model.vo.manager.AttributeTemplateSecondVo;
import com.medusa.gruul.goods.api.model.vo.manager.AttributeTemplateVo;
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
public class AttributeTemplateMapperTest {

    @Resource
    private AttributeTemplateMapper attributeTemplateMapper;

    @Test
    public void queryAllAttributeTemplateList() {
        ISecurity.userMust().setShopId("100002100001");
        List<AttributeTemplateVo> attributeTemplateVos = attributeTemplateMapper.queryAllAttributeTemplateList();
        Assert.assertNotNull(attributeTemplateVos);
        (attributeTemplateVos.toString());
    }

    @Test
    public void queryByParentId() {
        ISecurity.userMust().setShopId("100002100001");
        Long id = 541L;
        List<AttributeTemplateSecondVo> attributeTemplateSecondVos = attributeTemplateMapper.queryByParentId(id);
        Assert.assertNotNull(attributeTemplateSecondVos);
        (attributeTemplateSecondVos.toString());
    }

    @Test
    public void queryByPrimaryKey() {
        ISecurity.userMust().setShopId("100002100001");
        Long id = 541L;
        AttributeTemplateVo attributeTemplateVo = attributeTemplateMapper.queryByPrimaryKey(id);
        Assert.assertNotNull(attributeTemplateVo);
        (attributeTemplateVo.toString());
    }

    @Test
    public void queryAttributeTemplateList() {
        ISecurity.userMust().setShopId("100002100001");
        AttributeTemplateParam attributeTemplateParam = new AttributeTemplateParam();
        IPage<AttributeTemplateVo> page = new Page<>(attributeTemplateParam.getCurrent(), attributeTemplateParam.getSize());
        List<AttributeTemplateVo> attributeTemplateVos = attributeTemplateMapper.queryAttributeTemplateList(page, attributeTemplateParam);
        Assert.assertNotNull(attributeTemplateVos);
        (attributeTemplateVos.toString());
    }

    @Test
    public void queryDefaultAttributeTemplateList() {
        ISecurity.userMust().setShopId("100002100001");
        AttributeTemplateVo attributeTemplateVo = attributeTemplateMapper.queryDefaultAttributeTemplateList();
        Assert.assertNotNull(attributeTemplateVo);
        (attributeTemplateVo.toString());
    }
}