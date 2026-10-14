package com.medusa.gruul.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.account.api.entity.MemberPayRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员信息表 Mapper 接口
 *
 * @author zhaokw
 * @since 2024-06-13
 */
public interface MemberPayRuleMapper extends BaseMapper<MemberPayRule> {


    /**
     * 获取储值规则
     *
     * @return com.medusa.gruul.account.api.entity.MemberPayRule
     */
    List<MemberPayRule> getMemberPayRule();

}
