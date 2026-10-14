package com.medusa.gruul.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.account.api.entity.MemberPayRule;
import com.medusa.gruul.account.model.dto.MemberPayDto;
import com.medusa.gruul.account.model.dto.RechargeDto;
import com.medusa.gruul.payment.api.model.dto.PayResultDto;

import java.util.List;

/**
 * @Description 会员卡储值 服务类
 * @Author zhaokw
 * @Date 23:26 2024\6\13 0013
 **/
public interface IMemberPayRuleService extends IService<MemberPayRule> {

    /**
     * 会员储值规则信息
     *
     * @return 会员卡储值信息表
     */
    List<MemberPayRule> getMemberPayRule();

    /**
     * 后台保存储值规则
     *
     * @param memberPayRuleList 会员卡储值信息表
     */
    void saveMemberPayRule(List<MemberPayRule> memberPayRuleList);

    /**
     * 小程序端会员充值
     *
     * @param memberPayDto 会员充值信息
     * @return PayResultDto
     */
    PayResultDto memberPay(MemberPayDto memberPayDto);

    /**
     * 會員充值页面
     *
     * @param recharge 充值信息
     * @return Object
     */
    Object memberPayPage(RechargeDto recharge);

    /**
     * 小程序端用户充值
     *
     * @param memberPayDto 会员充值信息
     * @return PayResultDto
     */
    PayResultDto userPay(MemberPayDto memberPayDto);

    /**
     * 用户充值页面
     * @param recharge 充值信息
     * @return Object
     */
    Object userPayPage(RechargeDto recharge);
}
