package com.medusa.gruul.account.mapper;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.medusa.gruul.account.api.entity.MemberPayRule;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 会员储值规则表 Mapper 接口
 * </p>
 *
 * @author zhaokw
 * @since 2024-07-04
 */
@RunWith(SpringRunner.class)
@MybatisPlusTest
@ActiveProfiles("unit-test")
public class MemberPayRuleMapperTest {

    @Resource
    private MemberPayRuleMapper memberPayRuleMapper;

    @Test
    public void testGetMemberPayRule() {
        List<MemberPayRule> memberPayRules = memberPayRuleMapper.getMemberPayRule();
        Assert.assertNotNull(memberPayRules);
        (memberPayRules);
    }

}
