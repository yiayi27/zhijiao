package com.medusa.gruul.account.service.impl;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.medusa.gruul.account.api.entity.MemberPayRule;
import com.medusa.gruul.account.model.dto.MemberPayDto;
import com.medusa.gruul.account.service.IMemberPayRuleService;


import com.medusa.gruul.goods.api.constant.GoodsConstant;
import com.medusa.gruul.payment.api.model.dto.PayResultDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@MybatisPlusTest
@ActiveProfiles("unit-test")
//引入需要创建的Bean
@Import({MemberPayRuleServiceImpl.class})
public class MemberPayRuleServiceImplTest {
    @Resource
    private IMemberPayRuleService memberPayRuleService;

    @Test
    public void testGetMemberPayRule() {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        List<MemberPayRule> result = memberPayRuleService.getMemberPayRule();
        Assert.assertNotNull(result);
        (result);
    }

    @Test
    public void testSaveMemberPayRule() {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        List<MemberPayRule> memberPayRuleList = new ArrayList<>();
        MemberPayRule memberPayRule = new MemberPayRule();
        memberPayRule.setId(2L)
                .setState(1)
                .setDiscountAmount(new BigDecimal(20))
                .setPayAmount(new BigDecimal(100));
        memberPayRuleList.add(memberPayRule);
        MemberPayRule memberPayRule1 = new MemberPayRule();
        memberPayRule1.setId(3L)
                .setState(1)
                .setDiscountAmount(new BigDecimal(50))
                .setPayAmount(new BigDecimal(200));
        memberPayRuleList.add(memberPayRule1);
        MemberPayRule memberPayRule2 = new MemberPayRule();
        memberPayRule2.setId(6L)
                .setState(1)
                .setDiscountAmount(new BigDecimal(200))
                .setPayAmount(new BigDecimal(500));
        memberPayRuleList.add(memberPayRule2);
        memberPayRuleService.saveMemberPayRule(memberPayRuleList);
    }

    @Test
    public void testMemberPay() {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        String userId = "1248795148536647680";
        MemberPayDto memberPayDto = new MemberPayDto();
        memberPayDto.setPaymentType(1)
                .setDiscountAmount(new BigDecimal("0.01"))
                .setPayAmount(new BigDecimal("0.01"))
                .setUserId(userId));
        PayResultDto result = memberPayRuleService.memberPay(memberPayDto);
        Assert.assertNotNull(result);
        (result);
    }
}
