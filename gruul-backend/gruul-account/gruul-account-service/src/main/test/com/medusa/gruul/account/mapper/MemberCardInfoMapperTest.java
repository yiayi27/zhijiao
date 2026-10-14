package com.medusa.gruul.account.mapper;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.medusa.gruul.account.api.entity.MemberCardInfo;
import com.medusa.gruul.account.api.model.MemberCardInfoListDto;
import com.medusa.gruul.account.model.dto.SetMemberDto;
import com.medusa.gruul.account.model.vo.MemberCardInfoRankStatusVo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 会员卡信息表 Mapper 接口
 * </p>
 *
 * @author zhaokw
 * @since 2024-07-04
 */
@RunWith(SpringRunner.class)
@MybatisPlusTest
@ActiveProfiles("unit-test")
public class MemberCardInfoMapperTest {

    @Resource
    private MemberCardInfoMapper memberCardInfoMapper;

    @Test
    public void testSelectMemCardInfo() {
        SetMemberDto setMemberDto = new SetMemberDto();
        setMemberDto.setUserId("1")
                .setCode("176472")
                .setPhone("12354546746")
                .setRankCode("1");
        //MemberCardInfo memberCardInfo = memberCardInfoMapper.selectMemCardInfo(setMemberDto);
        //Assert.assertNotNull(memberCardInfo);
        //(memberCardInfo);
    }

    @Test
    public void testGetMemberCardRank() {
        List<MemberCardInfo> memberCardInfos = memberCardInfoMapper.getMemberCardRank();
        Assert.assertNotNull(memberCardInfos);
        (memberCardInfos);
    }

    @Test
    public void testGetMemberCardInfo() {
        List<MemberCardInfo> memberCardInfos = memberCardInfoMapper.getMemberCardInfo();
        Assert.assertNotNull(memberCardInfos);
        (memberCardInfos);
    }

    @Test
    public void testUpdateMemberCardInfoStatus() {
        MemberCardInfoRankStatusVo memberCardInfoRankStatusVo = new MemberCardInfoRankStatusVo();
        memberCardInfoRankStatusVo.setMemberCardInfoSwitch(1)
                .setRankCode("1")
        int update = memberCardInfoMapper.updateMemberCardInfoStatus(memberCardInfoRankStatusVo);
        Assert.assertNotNull(update);
        (update);
    }


    @Test
    public void testGetMemberCardInfoList() {
        MemberCardInfoListDto memberCardInfoListDto = new MemberCardInfoListDto();
        memberCardInfoListDto.setOpenType(1);
        List<MemberCardInfo> memberCardInfoList = memberCardInfoMapper.getMemberCardInfoList(memberCardInfoListDto);
        Assert.assertNotNull(memberCardInfoList);
        (memberCardInfoList);
    }

    @Test
    public void testGetApiMemberCardInfo() {
        MemberCardInfo memberCardInfo = memberCardInfoMapper.getApiMemberCardInfo(1L);
        Assert.assertNotNull(memberCardInfo);
        (memberCardInfo);
    }


}
