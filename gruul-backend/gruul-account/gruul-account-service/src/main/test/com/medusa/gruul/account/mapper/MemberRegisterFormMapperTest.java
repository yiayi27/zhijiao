package com.medusa.gruul.account.mapper;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.medusa.gruul.account.api.entity.MemberRegisterForm;
import com.medusa.gruul.account.model.vo.MemberRegisterFormVo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;
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
public class MemberRegisterFormMapperTest {

    @Resource
    private MemberRegisterFormMapper memberRegisterFormMapper;

    @Test
    public void testSelectMemberRegisterForm() {
        List<MemberRegisterFormVo> memberRegisterFormVos = memberRegisterFormMapper.selectMemberRegisterForm();
        Assert.assertNotNull(memberRegisterFormVos);
        (memberRegisterFormVos);
    }

    @Test
    public void testQryFormById() {
        String formId = "1";
        MemberRegisterForm memberRegisterForm = memberRegisterFormMapper.qryFormById(formId);
        Assert.assertNotNull(memberRegisterForm);
        (memberRegisterForm);
    }


}
