package com.medusa.gruul.account.service.impl;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.medusa.gruul.account.api.model.MemberCardInfoCenterDto;
import com.medusa.gruul.account.api.model.MemberTopCardInfoDto;
import com.medusa.gruul.account.model.dto.SetMemberDto;
import com.medusa.gruul.account.model.dto.UpdateMemberExpireTimeDto;
import com.medusa.gruul.account.model.dto.UpgradeMemberRankDto;
import com.medusa.gruul.account.model.vo.MemberCardRankListVo;
import com.medusa.gruul.account.model.vo.MemberCardUpgradeVo;
import com.medusa.gruul.account.model.vo.MemberInnerListVo;
import com.medusa.gruul.account.model.vo.MemberListVo;
import com.medusa.gruul.account.service.IMemberInfoService;
import com.medusa.gruul.common.core.util.PageUtils;


import com.medusa.gruul.goods.api.constant.GoodsConstant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@MybatisPlusTest
@ActiveProfiles("unit-test")
//引入需要创建的Bean
@Import({MemberInfoServiceImpl.class})
public class MemberInfoServiceImplTest {
    @Resource
    private IMemberInfoService memberInfoService;

    @Test
    public void testMemberList() {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        PageUtils<List<MemberListVo>> result = memberInfoService.memberList(null, null, null, null, null, null, null, null, null, 1, 20, 1);
        Assert.assertNotNull(result);
        (result);
    }

    @Test
    public void testSetMember() {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        SetMemberDto setMemberDto = new SetMemberDto();
        setMemberDto.setUserId("1")
                .setCode("176472")
                .setPhone("12354546746")
                .setRankCode("1");
        memberInfoService.setMember(setMemberDto);
    }

    @Test
    public void testGetMemberCardRank() {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        List<MemberCardRankListVo> memberCardRankListVos = memberInfoService.getMemberCardRank();
        Assert.assertNotNull(memberCardRankListVos);
        (memberCardRankListVos);
    }

    @Test
    public void testUpdateMemberExpireTime() {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        UpdateMemberExpireTimeDto updateMemberExpireTimeDto = new UpdateMemberExpireTimeDto();
        updateMemberExpireTimeDto.setMemberNumber("1000022007180430042")
                .setDate(1L)
                .setMemberExpireTime("2024-07-19");
        memberInfoService.updateMemberExpireTime(updateMemberExpireTimeDto);
    }

    @Test
    public void testUpgradeMemberRank() {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        UpgradeMemberRankDto upgradeMemberRankDto = new UpgradeMemberRankDto();
        upgradeMemberRankDto.setMemberNumber("1000022007215654613")
                .setRankCode("2")
                .setRankName("金卡会员");
        memberInfoService.upgradeMemberRank(upgradeMemberRankDto);
    }

    @Test
    public void testGetMemberInfo() {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        String userId = "1248795148536647680";
        MemberCardInfoCenterDto memberCardInfoCenterDto = memberInfoService.getMemberInfo(userId);
        Assert.assertNotNull(memberCardInfoCenterDto);
        (memberCardInfoCenterDto);
    }

    @Test
    public void testGetTopMemberCardInfo() {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        MemberTopCardInfoDto memberTopCardInfoDto = memberInfoService.getTopMemberCardInfo();
        Assert.assertNotNull(memberTopCardInfoDto);
        (memberTopCardInfoDto);
    }

    @Test
    public void testGetMemberCardUpgrade() {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        String memberId = "1";
        MemberCardUpgradeVo memberCardUpgradeVo = memberInfoService.getMemberCardUpgrade(memberId);
        Assert.assertNotNull(memberCardUpgradeVo);
        (memberCardUpgradeVo);
    }

    @Test
    public void testGetMemberRecord() {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        String userId = "1248795148536647680";
        PageUtils<List<MemberInnerListVo>> iPage = memberInfoService.getMemberRecord(1, 20, userId, null, null, null, null, null);
        Assert.assertNotNull(iPage);
        (iPage);
    }

}
