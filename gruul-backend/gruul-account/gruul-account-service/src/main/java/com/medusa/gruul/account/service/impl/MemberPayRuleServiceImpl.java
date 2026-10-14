package com.medusa.gruul.account.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.constant.AccountQueueNameConstant;
import com.medusa.gruul.account.api.entity.MemberCardInfo;
import com.medusa.gruul.account.api.entity.MemberInfo;
import com.medusa.gruul.account.api.entity.MemberPayRule;
import com.medusa.gruul.account.mapper.MemberCardInfoMapper;
import com.medusa.gruul.account.mapper.MemberInfoMapper;
import com.medusa.gruul.account.mapper.MemberPayRuleMapper;
import com.medusa.gruul.account.model.dto.MemberPayDto;
import com.medusa.gruul.account.model.dto.RechargeDto;
import com.medusa.gruul.account.model.vo.MemberCompletedVo;
import com.medusa.gruul.account.service.IMemberPayRuleService;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.core.util.IPUtils;
import com.medusa.gruul.common.core.util.SystemCode;


import com.medusa.gruul.payment.api.feign.RemotePaymentService;
import com.medusa.gruul.payment.api.model.dto.PayRequestDto;
import com.medusa.gruul.payment.api.model.dto.PayResultDto;
import com.medusa.gruul.payment.api.model.pay.PayParam;
import com.medusa.gruul.platform.api.feign.RemoteMiniInfoService;
import com.medusa.gruul.platform.api.model.dto.ShopConfigDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;

/**
 * @Description: 会员信息实现类
 * @Author: zhaokw29701
 * @Date: 2024/6/12 22:31
 **/
@Slf4j
@Service
public class MemberPayRuleServiceImpl extends ServiceImpl<MemberPayRuleMapper, MemberPayRule> implements IMemberPayRuleService {

    @Autowired
    private MemberPayRuleMapper memberPayRuleMapper;

    @Autowired
    private MemberInfoMapper memberInfoMapper;

    @Autowired
    private MemberCardInfoMapper memberCardInfoMapper;

    @Autowired
    private RemotePaymentService remotePaymentService;

    /**
     * 生成订单编号，格式：店铺id+yyMMdd+7位随机数
     *
     * @param range
     * @return
     */
    @SneakyThrows
    public static String generateOrderRandomNo(String shopId, int range) {
        return MemberInfoServiceImpl.generateRandomNo(shopId, range);
    }

    /**
     * 会员储值规则信息
     *
     * @return 会员卡储值信息表
     */
    @Override
    public List<MemberPayRule> getMemberPayRule() {
        return memberPayRuleMapper.getMemberPayRule();
    }

    /**
     * 后台保存储值规则
     *
     * @param memberPayRuleList 会员卡储值信息表
     */
    @Override
    public void saveMemberPayRule(List<MemberPayRule> memberPayRuleList) {
        List<MemberPayRule> memberPayRules = memberPayRuleMapper.getMemberPayRule();
        for (MemberPayRule memberPayRule : memberPayRules) {
            memberPayRuleMapper.deleteById(memberPayRule.getId());
        }
        for (MemberPayRule memberPayRule : memberPayRuleList) {
            MemberPayRule memberPayRuleTemp = new MemberPayRule();
            memberPayRuleTemp.setState(memberPayRule.getState())
                    .setPayAmount(memberPayRule.getPayAmount())
                    .setDiscountAmount(memberPayRule.getDiscountAmount());
            memberPayRuleMapper.insert(memberPayRuleTemp);
        }
    }

    /**
     * 小程序端会员充值
     *
     * @param memberPayDto 会员充值信息
     * @return PayResultDto
     */
    @Override
    public PayResultDto memberPay(MemberPayDto memberPayDto) {
        //微信支付
        if (memberPayDto.getPaymentType().equals(CommonConstants.NUMBER_ONE)) {
            PayRequestDto payRequestDto = new PayRequestDto();
            //支付渠道 1-微信支付 2-环迅 3-随行 4-盛付通
            Integer payChannel = 1;
            int tradeType;
            if (payChannel.equals(CommonConstants.NUMBER_TWO)) {
                tradeType = 101;
            } else if (payChannel.equals(CommonConstants.NUMBER_THREE)) {
                tradeType = 201;
            } else if (payChannel.equals(CommonConstants.NUMBER_FOUR)) {
                tradeType = 301;
            } else {
                tradeType = CommonConstants.NUMBER_ONE;
            }
            SecureUser secureUser = ISecurity.userMust();
            String outTradeNo = generateOrderRandomNo(secureUser.getShopId(), CommonConstants.NUMBER_SEVEN);
            String userId = secureUser.getId();
            String openId = secureUser.getOpenid();
            memberPayDto.setUserId(userId);
            List<MemberInfo> memberInfos = memberInfoMapper.getMemberInfoByUserId(userId);
            List<MemberCardInfo> memberCardInfoList = memberInfos.stream().map(item -> memberCardInfoMapper.getApiMemberCardInfo(item.getMemberCardInfoId())).toList();
            memberCardInfoList = memberCardInfoList.stream().sorted(Comparator.comparing(MemberCardInfo::getRankCode).reversed()).toList();
            memberPayDto.setMemberCardInfoId(memberCardInfoList.get(CommonConstants.NUMBER_ZERO).getId());
            String routekey = AccountQueueNameConstant.MEMBER_PAY_OK_QUEUE_CHANGE;
            BigDecimal totalFee = memberPayDto.getPayAmount();
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                throw new ServiceException("数据异常");
            }
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            String terminalIp = IPUtils.getIpAddr(request);
            String feeType = "CNY";
            String timeoutExpress = "15m";
            String body = "会员充值";
            MemberCompletedVo memberCompletedVo = new MemberCompletedVo();
            memberCompletedVo.setUserRank(memberPayDto.getUserId())
                    .setId(memberPayDto.getMemberCardInfoId())
                    .setDAmount(memberPayDto.getDiscountAmount())
                    .setPAmount(memberPayDto.getPayAmount())
                    .setType(CommonConstants.NUMBER_FOUR);
            String businessParams = Jackson.writeValueAsString(memberCompletedVo);
            payRequestDto.setPayChannel(payChannel);
            payRequestDto.setTradeType(tradeType);
            payRequestDto.setOutTradeNo(outTradeNo);
            payRequestDto.setRouteKey(routekey);
            payRequestDto.setOpenId(openId);
            payRequestDto.setTotalFee(totalFee);
            payRequestDto.setTerminalIp(terminalIp);
            payRequestDto.setFeeType(feeType);
            payRequestDto.setTimeoutExpress(timeoutExpress);
            payRequestDto.setBody(body);
            payRequestDto.setBusinessParams(businessParams);
            payRequestDto.setShopId(ISecurity.userMust().getShopId());
            PayResultDto payResultDto = remotePaymentService.payRequest(payRequestDto);
            log.debug("__:{}", payResultDto);
            return payResultDto;
        } else {
            return new PayResultDto();
        }
    }

    /**
     * 會員充值页面
     *
     * @param recharge 充值信息
     */
    @Override
    public Object memberPayPage(RechargeDto recharge) {
        if (recharge.getRuleId() == null) {
            throw new ServiceException(SystemCode.PARAM_BIND_ERROR);
        }
        List<MemberInfo> memberInfos = memberInfoMapper.getMemberInfoByUserId(ISecurity.userMust().getId());
        if (CollUtil.isEmpty(memberInfos)) {
            throw new ServiceException("非会员无法使用此规则");
        }
        List<MemberCardInfo> memberCardInfos =
                memberInfos.stream().map(
                        item -> memberCardInfoMapper.getApiMemberCardInfo(item.getMemberCardInfoId())
                ).sorted(
                        Comparator.comparing(MemberCardInfo::getRankCode).reversed()
                ).toList();
        if (CollUtil.isEmpty(memberCardInfos)) {
            throw new ServiceException("暂无可用会员卡");
        }
        MemberPayRule rule = this.getById(recharge.getRuleId());
        PayParam payParam = new PayParam()
                .setSubject("会员充值")
                .setBody("会员充值")
                .setOrderId(generateOrderRandomNo(ISecurity.userMust().getShopId(), CommonConstants.NUMBER_SEVEN))
                .setPayType(recharge.getPayType())
                .setRouteKey(AccountQueueNameConstant.MEMBER_PAY_OK_QUEUE_CHANGE)
                .setPrice(rule.getPayAmount())
                .setSeconds(15 * 60L)
                .setAttach(
                        Jackson.writeValueAsString(
                                new MemberCompletedVo()
                                        .setId(memberCardInfos.get(CommonConstants.NUMBER_ZERO).getId())
                                        .setUserRank(ISecurity.userMust().getId())
                                        .setDAmount(rule.getDiscountAmount())
                                        .setPAmount(rule.getPayAmount())
                                        .setType(CommonConstants.NUMBER_FOUR)
                        )
                );

        return remotePaymentService.toPayPage(payParam).successData().getData();
    }

    /**
     * 小程序端用户充值
     *
     * @param memberPayDto 会员充值信息
     * @return PayResultDto
     */
    @Override
    public PayResultDto userPay(MemberPayDto memberPayDto) {
        
        //微信支付
        if (memberPayDto.getPaymentType().equals(CommonConstants.NUMBER_ONE)) {
            PayRequestDto payRequestDto = new PayRequestDto();
            //支付渠道 1-微信支付 2-环迅 3-随行 4-盛付通
            Integer payChannel = CommonConstants.NUMBER_ONE;
            int tradeType;
            if (payChannel.equals(CommonConstants.NUMBER_TWO)) {
                tradeType = 101;
            } else if (payChannel.equals(CommonConstants.NUMBER_THREE)) {
                tradeType = 201;
            } else if (payChannel.equals(CommonConstants.NUMBER_FOUR)) {
                tradeType = 301;
            } else {
                tradeType = CommonConstants.NUMBER_ONE;
            }
            String outTradeNo = generateOrderRandomNo(ISecurity.userMust().getShopId(), CommonConstants.NUMBER_SEVEN);
            SecureUser secureUser = ISecurity.userMust();
            String userId = secureUser.getId();
            String openId = secureUser.getOpenid();
            memberPayDto.setUserId(userId);
            String routekey = AccountQueueNameConstant.MEMBER_PAY_OK_QUEUE_CHANGE;
            BigDecimal totalFee = memberPayDto.getPayAmount();
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                throw new ServiceException("数据异常");
            }
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            String terminalIp = IPUtils.getIpAddr(request);
            String feeType = "CNY";
            String timeoutExpress = "15m";
            String body = "用户充值";
            MemberCompletedVo memberCompletedVo = new MemberCompletedVo();
            memberCompletedVo.setUserRank(memberPayDto.getUserId())
                    .setId(memberPayDto.getMemberCardInfoId())
                    .setDAmount(memberPayDto.getDiscountAmount())
                    .setPAmount(memberPayDto.getPayAmount())
                    .setType(CommonConstants.NUMBER_SIX);
            String businessParams = Jackson.writeValueAsString(memberCompletedVo);
            payRequestDto.setPayChannel(payChannel);
            payRequestDto.setTradeType(tradeType);
            payRequestDto.setOutTradeNo(outTradeNo);
            payRequestDto.setRouteKey(routekey);
            payRequestDto.setOpenId(openId);
            payRequestDto.setTotalFee(totalFee);
            payRequestDto.setTerminalIp(terminalIp);
            payRequestDto.setFeeType(feeType);
            payRequestDto.setTimeoutExpress(timeoutExpress);
            payRequestDto.setBody(body);
            payRequestDto.setBusinessParams(businessParams);
            return remotePaymentService.payRequest(payRequestDto);
        } else {
            return new PayResultDto();
        }
    }

    /**
     * 用户充值页面
     * @param recharge 充值信息
     * @return Object
     */
    @Override
    public Object userPayPage(RechargeDto recharge) {
        PayParam payParam = new PayParam()
                .setPayType(recharge.getPayType())
                .setOrderId(generateOrderRandomNo(ISecurity.userMust().getShopId(), CommonConstants.NUMBER_SEVEN))
                .setRouteKey(AccountQueueNameConstant.MEMBER_PAY_OK_QUEUE_CHANGE)
                .setSubject("余额充值")
                .setBody("余额充值")
                .setPrice(recharge.getPayAmount())
                .setSeconds(15 * 60L)
                .setAttach(
                        Jackson.writeValueAsString(
                                new MemberCompletedVo()
                                        .setId(null)
                                        .setUserRank(ISecurity.userMust().getId())
                                        .setDAmount(BigDecimal.ZERO)
                                        .setPAmount(recharge.getPayAmount())
                                        .setType(6)
                        )
                );
        return remotePaymentService.toPayPage(payParam).successData().getData();
    }
}
