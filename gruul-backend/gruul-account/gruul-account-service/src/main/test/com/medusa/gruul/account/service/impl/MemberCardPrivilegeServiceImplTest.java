package com.medusa.gruul.account.service.impl;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.medusa.gruul.account.api.entity.MemberCardPrivilege;
import com.medusa.gruul.account.model.dto.DeleteMemberCardPrivilegeDto;
import com.medusa.gruul.account.model.dto.InsertMemberCardPrivilegeDto;
import com.medusa.gruul.account.model.dto.MemberCardPrivilegeStatusDto;
import com.medusa.gruul.account.model.dto.UpdateMemberCardPrivilegeDto;
import com.medusa.gruul.account.service.IMemberCardPrivilegeService;


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
@Import({MemberCardPrivilegeServiceImpl.class})
public class MemberCardPrivilegeServiceImplTest {
    @Resource
    private IMemberCardPrivilegeService memberCardPrivilegeService;

    @Test
    public void testGetMemberCardPrivilege() {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        List<MemberCardPrivilege> result = memberCardPrivilegeService.getMemberCardPrivilege();
        Assert.assertNotNull(result);
        (result);
    }

    @Test
    public void testUpdateMemberCardPrivilegeDto() {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        UpdateMemberCardPrivilegeDto updateMemberCardPrivilegeDto = new UpdateMemberCardPrivilegeDto();
        updateMemberCardPrivilegeDto.setId(7L)
                .setIllustrate("申请维权时，系统自动同意维权申请，无需等待人工审核")
                .setIcon("https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200710/798a80e89e61489c944fcfe663aba04a.png")
                .setOpen(1)
                .setAllowDel(0);
        memberCardPrivilegeService.updateMemberCardPrivilege(updateMemberCardPrivilegeDto);
    }

    @Test
    public void testUpdateMemberCardPrivilegeStatus() {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        MemberCardPrivilegeStatusDto memberCardPrivilegeStatusDto = new MemberCardPrivilegeStatusDto();
        memberCardPrivilegeStatusDto.setId(7L)
                .setOpen(1);
        memberCardPrivilegeService.updateMemberCardPrivilegeStatus(memberCardPrivilegeStatusDto);
    }

    @Test
    public void testDeleteMemberCardPrivilege() {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        DeleteMemberCardPrivilegeDto deleteMemberCardPrivilegeDto = new DeleteMemberCardPrivilegeDto();
        deleteMemberCardPrivilegeDto.setId(7L)
                .setIsDeleted(1);
        memberCardPrivilegeService.deleteMemberCardPrivilege(deleteMemberCardPrivilegeDto);
    }

    @Test
    public void testInsertMemberCardPrivilege() {
        ISecurity.userMust().setShopId(GoodsConstant.TEST_SHOP_ID);
        
        InsertMemberCardPrivilegeDto insertMemberCardPrivilegeDto = new InsertMemberCardPrivilegeDto();
        insertMemberCardPrivilegeDto.setIllustrate("自定义权益")
                .setIcon("https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200710/798a80e89e61489c944fcfe663aba04a.png")
                .setOpen(1)
                .setAllowDel(0)
                .setPType("0")
                .setPrivilegeName("自定义权益");
        memberCardPrivilegeService.insertMemberCardPrivilege(insertMemberCardPrivilegeDto);
    }

}
