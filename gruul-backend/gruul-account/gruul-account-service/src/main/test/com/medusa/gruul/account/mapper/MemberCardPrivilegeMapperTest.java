package com.medusa.gruul.account.mapper;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.medusa.gruul.account.api.entity.MemberCardPrivilege;
import com.medusa.gruul.account.model.vo.MemberPrivilegeVo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 会员卡权益表 Mapper 接口
 * </p>
 *
 * @author zhaokw
 * @since 2024-07-04
 */
@RunWith(SpringRunner.class)
@MybatisPlusTest
@ActiveProfiles("unit-test")
public class MemberCardPrivilegeMapperTest {

    @Resource
    private MemberCardPrivilegeMapper memberCardPrivilegeMapper;

    @Test
    public void testSelectMemberCardPrivilege() {
        List<MemberPrivilegeVo> memberPrivilegeVos = memberCardPrivilegeMapper.selectMemberCardPrivilege();
        Assert.assertNotNull(memberPrivilegeVos);
        (memberPrivilegeVos);
    }

    @Test
    public void testGetMemberCardPrivilege() {
        List<MemberCardPrivilege> memberCardPrivileges = memberCardPrivilegeMapper.getMemberCardPrivilege();
        Assert.assertNotNull(memberCardPrivileges);
        (memberCardPrivileges);
    }

}
