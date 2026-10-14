package com.medusa.gruul.account.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.constant.AccountQueueNameConstant;
import com.medusa.gruul.account.api.entity.*;
import com.medusa.gruul.account.constant.MemberStatus;
import com.medusa.gruul.account.mapper.*;
import com.medusa.gruul.account.model.dto.UpdateApiMemberExpireTimeDto;
import com.medusa.gruul.account.model.dto.UpdateApiMemberExpireTimePageDto;
import com.medusa.gruul.account.model.dto.UpgradeApiMemberRankDto;
import com.medusa.gruul.account.model.dto.UpgradeApiMemberRankPageDto;
import com.medusa.gruul.account.model.vo.*;
import com.medusa.gruul.account.service.IApiMemberInfoService;
import com.medusa.gruul.account.service.IMemberCardInfoService;
import com.medusa.gruul.account.service.IMemberSwitchService;
import com.medusa.gruul.account.service.IMiniAccountExtendsService;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.IPUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.goods.api.entity.RebateSet;
import com.medusa.gruul.goods.api.feign.RemoteGoodsService;
import com.medusa.gruul.integral.api.feign.RemoteIntegralService;
import com.medusa.gruul.payment.api.feign.RemotePaymentService;
import com.medusa.gruul.payment.api.model.dto.PayRequestDto;
import com.medusa.gruul.payment.api.model.dto.PayResultDto;
import com.medusa.gruul.payment.api.model.pay.PayParam;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * @Description: 会员信息实现类 小程序端
 * @Author: zhaokw29701
 * @Date: 2024/5/30 22:31
 **/
@Slf4j
@Service
public class ApiMemberInfoServiceImpl extends ServiceImpl<MemberInfoMapper, MemberInfo> implements IApiMemberInfoService {

    @Autowired
    private MemberInfoMapper memberInfoMapper;

    @Autowired
    private MiniAccountMapper miniAccountMapper;

    @Autowired
    private MemberCardInfoMapper memberCardInfoMapper;
    @Autowired
    private IMemberCardInfoService memberCardInfoService;
    @Autowired
    private MemberCardPrivilegeMapper memberCardPrivilegeMapper;

    @Autowired
    private MemberCardPrivilegeGroupMapper memberCardPrivilegeGroupMapper;

    @Autowired
    private RemotePaymentService remotePaymentService;

    @Autowired
    private RemoteIntegralService remoteIntegralService;

    @Autowired
    private IMiniAccountExtendsService miniAccountExtendsService;

    @Autowired
    private MemberBalanceRecordMapper memberBalanceRecordMapper;

    @Autowired
    private IMemberSwitchService memberSwitchService;

    @Autowired
    private RemoteGoodsService remoteGoodsService;

    /**
     * 小程序端会员信息列表
     *
     * @return List<ApiMemberCardInfoVo> 会员卡列表信息
     */
    @Override
    public List<ApiMemberCardInfoVo> getMemberInfo() {
        String userId = ISecurity.userOpt()
                .map(SecureUser::getId)
                .orElse("");
        List<ApiMemberCardInfoVo> apiMemberCardInfoVos = new ArrayList<>();
        MemberSwitch memberSwitch = new MemberSwitch();
        memberSwitch.setMemberModel(0);
        MemberSwitch memberSwitches =
                memberSwitchService.getByMemberModelMemberSwitch(memberSwitch.getMemberModel());
        if (memberSwitches == null || memberSwitches.getState().equals(CommonConstants.NUMBER_ZERO)) {
            //获取会员信息
            List<MemberInfo> memberInfos = memberInfoMapper.getMemberInfoByUserId(userId);
            if (CollectionUtil.isNotEmpty(memberInfos)) {
                //是会员
                for (MemberInfo memberInfo : memberInfos) {
                    ApiMemberCardInfoVo apiMemberCardInfoVo = new ApiMemberCardInfoVo();
                    //PC端关闭会员，当前用户是会员
                    apiMemberCardInfoVo.setStatus(MemberStatus.THREE);
                    apiMemberCardInfoVo.setState(memberInfo.getState())
                            .setId(memberInfo.getId())
                            .setMemberCardInfoId(memberInfo.getMemberCardInfoId())
                            .setMemberExpireTime(memberInfo.getMemberExpireTime());
                    MemberCardInfo memberCardInfoTemp = memberCardInfoMapper.getApiMemberCardInfo(memberInfo.getMemberCardInfoId());
                    apiMemberCardInfoVo.setRankCode(memberCardInfoTemp.getRankCode())
                            .setRankName(memberCardInfoTemp.getRankName());
                    //获取用户信息
                    MiniAccountExtends accountExtends = miniAccountExtendsService.findByShopUserId(userId);
                    apiMemberCardInfoVo.setUserId(userId)
                            .setSupplyBonus(accountExtends.getSupplyBonus())
                            .setRebateBonus(accountExtends.getRebateBonus())
                            .setSupplyAndRebateBonus(accountExtends.getSupplyBonus().add(accountExtends.getRebateBonus()));
                    apiMemberCardInfoVos.add(apiMemberCardInfoVo);
                }
                apiMemberCardInfoVos = apiMemberCardInfoVos.stream().sorted(Comparator.comparing(ApiMemberCardInfoVo::getRankCode).reversed()).toList();
            } else {
                ApiMemberCardInfoVo apiMemberCardInfoVo = new ApiMemberCardInfoVo();
                //PC端关闭会员，不是会员
                apiMemberCardInfoVo.setStatus(MemberStatus.FOUR);
                MiniAccountExtends accountExtends = miniAccountExtendsService.findByShopUserId(userId);
                apiMemberCardInfoVo.setUserId(userId)
                        .setSupplyBonus(accountExtends.getSupplyBonus())
                        .setRebateBonus(accountExtends.getRebateBonus())
                        .setSupplyAndRebateBonus(accountExtends.getSupplyBonus().add(accountExtends.getRebateBonus()))
                        .setState(0);
                apiMemberCardInfoVos.add(apiMemberCardInfoVo);
            }
        } else {
            //获取会员信息
            List<MemberInfo> memberInfos = memberInfoMapper.getMemberInfoByUserId(userId);
            //是会员
            if (CollectionUtil.isNotEmpty(memberInfos)) {
                for (MemberInfo memberInfo : memberInfos) {
                    ApiMemberCardInfoVo apiMemberCardInfoVo = new ApiMemberCardInfoVo();
                    apiMemberCardInfoVo
                            .setStatus(CommonConstants.NUMBER_ONE)
                            .setState(memberInfo.getState())
                            .setId(memberInfo.getId())
                            .setMemberCardInfoId(memberInfo.getMemberCardInfoId())
                            .setMemberExpireTime(memberInfo.getMemberExpireTime());
                    MemberCardInfo memberCardInfoTemp = memberCardInfoMapper.getApiMemberCardInfo(memberInfo.getMemberCardInfoId());
                    apiMemberCardInfoVo
                            .setRankCode(memberCardInfoTemp.getRankCode())
                            .setRankName(memberCardInfoTemp.getRankName());
                    //获取用户信息
                    MiniAccountExtends accountExtends = miniAccountExtendsService.findByShopUserId(userId);
                    apiMemberCardInfoVo.setUserId(userId)
                            .setSupplyBonus(accountExtends.getSupplyBonus())
                            .setRebateBonus(accountExtends.getRebateBonus())
                            .setSupplyAndRebateBonus(accountExtends.getSupplyBonus().add(accountExtends.getRebateBonus()));
                    apiMemberCardInfoVos.add(apiMemberCardInfoVo);
                }
                apiMemberCardInfoVos = apiMemberCardInfoVos.stream().sorted(Comparator.comparing(ApiMemberCardInfoVo::getRankCode).reversed()).toList();
            } else {
                ApiMemberCardInfoVo apiMemberCardInfoVo = new ApiMemberCardInfoVo();
                //不是会员
                apiMemberCardInfoVo.setStatus(MemberStatus.TWO);
                MiniAccountExtends accountExtends = miniAccountExtendsService.findByShopUserId(userId);
                apiMemberCardInfoVo.setUserId(userId)
                        .setSupplyBonus(accountExtends.getSupplyBonus())
                        .setRebateBonus(accountExtends.getRebateBonus())
                        .setSupplyAndRebateBonus(accountExtends.getSupplyBonus().add(accountExtends.getRebateBonus()))
                        .setState(0);
                apiMemberCardInfoVos.add(apiMemberCardInfoVo);
            }
        }
        return apiMemberCardInfoVos;
    }

    /**
     * 小程序端更新会员的到期日，会员续费
     *
     * @param updateApiMemberExpireTimeDto  小程序更新会员的到期日
     * @return PayResultDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayResultDto updateMemberExpireTime(UpdateApiMemberExpireTimeDto updateApiMemberExpireTimeDto) {

        DateTime t1 = DateUtil.parseDateTime(updateApiMemberExpireTimeDto.getMemberExpireTime());
        DateTime t2 = DateTime.now();
        if (t1.compareTo(t2) < 0) {
            throw new ServiceException("小程序端会员续费失败，会员卡已到期", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
        //付费购买
        if (updateApiMemberExpireTimeDto.getOpenType().equals(CommonConstants.NUMBER_ONE)) {
            Balance balance = new Balance(updateApiMemberExpireTimeDto).invoke();
            if (balance.is()) {
                return balance.getPayResultDto();
            }
        } else if (updateApiMemberExpireTimeDto.getOpenType().equals(CommonConstants.NUMBER_TWO)) {
            integral(updateApiMemberExpireTimeDto);

        } else {

        }
        return new PayResultDto();
    }

    /**
     * 付费购买会员卡续费 会员到期信息
     * @param memberExpire 会员到期信息
     * @return Object
     */
    @Override
    public Object updateMemberExpireTimePage(UpdateApiMemberExpireTimePageDto memberExpire) {
        if (DateUtil.parseDateTime(memberExpire.getMemberExpireTime()).compareTo(DateTime.now()) < 0) {
            throw new ServiceException("会员续费失败，会员卡已到期", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
        PayParam payParam = new PayParam()
                .setSubject("会员续费")
                .setBody("会员续费")
                .setPayType(memberExpire.getPayType())
                .setOrderId(memberExpire.getMemberNumber())
                .setRouteKey(AccountQueueNameConstant.MEMBER_PAY_OK_QUEUE_CHANGE)
                .setPrice(memberExpire.getPayAmount())
                .setSeconds(15 * 60L)
                .setAttach(
                        Jackson.writeValueAsString(
                                new MemberCompletedVo()
                                        .setUserRank(ISecurity.userMust().getId())
                                        .setMNumber(memberExpire.getMemberNumber())
                                        .setType(2)
                                        .setDate(memberExpire.getDate())
                        )
                );
        return remotePaymentService.toPayPage(payParam).successData().getData();
    }

    /**
     * 更新到期时间，积分兑换
     *
     * @param updateApiMemberExpireTimeDto com.medusa.gruul.account.model.dto.UpdateApiMemberExpireTimeDto
     */
    private void integral(UpdateApiMemberExpireTimeDto updateApiMemberExpireTimeDto) {
        String shopUserId = ISecurity.userMust().getId();
        BigDecimal useAmount = updateApiMemberExpireTimeDto.getAvailableAmount();
        Result result = remoteIntegralService.conversionClubCard(shopUserId, useAmount);
        if (result.getCode() == CommonConstants.SUCCESS) {
            //会员续费
            //取时间
            Date date = DateUtil.parseDateTime(updateApiMemberExpireTimeDto.getMemberExpireTime());
            Calendar calendar = Calendar.getInstance();
            //需要将date数据转移到Calender对象中操作
            calendar.setTime(date);
            //把日期往后增加n天.正数往后推,负数往前移动
            calendar.add(Calendar.DATE, updateApiMemberExpireTimeDto.getDate().intValue());
            //这个时间就是日期往后推n天的结果
            date = calendar.getTime();
            updateApiMemberExpireTimeDto.setMemberExpireTime(DateUtil.formatDateTime(date));
            int update = memberInfoMapper.updateApiMemberExpireTime(updateApiMemberExpireTimeDto);
            if (update < 1) {
                throw new ServiceException("小程序端会员续费失败", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        } else {
            throw new ServiceException(result.getMsg(), SystemCode.DATA_UPDATE_FAILED.getCode());
        }
    }

    /**
     * 小程序端升级会员界面
     *
     * @param memberId 会员id
     * @return com.medusa.gruul.account.model.vo.MemberCardUpgradeVo
     */
    @Override
    public MemberCardUpgradeVo getMemberCardUpgrade(String memberId) {
        MemberCardUpgradeVo memberCardUpgradeVo = new MemberCardUpgradeVo();
        Optional<SecureUser<Object>> userOpt = ISecurity.userOpt();
        if (userOpt.isEmpty()) {
            return memberCardUpgradeVo;
        }
        SecureUser<Object> secureUser = userOpt.get();
        String userId = secureUser.getId();

        MiniAccount miniAccount = miniAccountMapper.getMemberInfo(userId);
        MiniAccountExtends accountExtends = miniAccountExtendsService.findByShopUserId(userId);
        //获取返利设置里会员返利金额是否可用,可用：返利+用户余额  不可用：用户余额
        String shopId = ISecurity.userMust().getShopId();
        RebateSet rebateSet = remoteGoodsService.getRebateSet(shopId);
        List<MemberInfo> memberInfoList = memberInfoMapper.selectList(new LambdaQueryWrapper<MemberInfo>().eq(MemberInfo::getState, 1).eq(MemberInfo::getUserId, userId));
        //是会员
        if (CollectionUtil.isNotEmpty(memberInfoList)) {
            if (rebateSet != null && rebateSet.getBalanceUseCondition() != null && rebateSet.getBalanceUseCondition().contains(CommonConstants.NUMBER_ONE.toString())) {
                memberCardUpgradeVo.setAccountBalance(accountExtends.getSupplyBonus().add(accountExtends.getRebateBonus()));
            } else {
                memberCardUpgradeVo.setAccountBalance(accountExtends.getSupplyBonus());
            }
        } else {
            //不是会员,是普通用户
            if (rebateSet != null && rebateSet.getBalanceUseCondition() != null && rebateSet.getBalanceUseCondition().contains(CommonConstants.NUMBER_ZERO.toString())) {
                memberCardUpgradeVo.setAccountBalance(accountExtends.getSupplyBonus().add(accountExtends.getRebateBonus()));
            } else {
                memberCardUpgradeVo.setAccountBalance(accountExtends.getSupplyBonus());
            }
        }
        memberCardUpgradeVo.setAvatarUrl(miniAccount.getAvatarUrl())
                .setNikeName(miniAccount.getNikeName())
                .setUserId(userId);
        BigDecimal integral = miniAccountMapper.getIntegralByShopUserId(secureUser.getId(), secureUser.getShopId());
        //账户可用积分
        memberCardUpgradeVo.setUseAmount(integral);
        MemberInfo memberInfoTemp = memberInfoMapper.selectById(memberId);
        MemberCardInfo memberCardInfoTemp = memberCardInfoMapper.getApiMemberCardInfo(memberInfoTemp.getMemberCardInfoId());
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = memberInfoTemp.getMemberExpireTime().atZone(zoneId);
        Date date = Date.from(zdt.toInstant());
        Date date2 = new Date();
        long day = DateUtil.betweenDay(date2, date, false);
        if (day < 0) {
            day = 0;
        }
        memberCardUpgradeVo.setRemainDate(String.valueOf(day))
                .setRankCode(memberCardInfoTemp.getRankCode())
                .setRankName(memberCardInfoTemp.getRankName())
                .setMemberNumber(memberInfoTemp.getMemberNumber());
        List<MemberCardInfoCenterVo> memberCardInfoCenterVos = new ArrayList<>();
        List<MemberCardInfo> memberCardInfoList = memberCardInfoService.getCurrentMemberCardInfo(secureUser.getShopId());
        for (MemberCardInfo memberCardInfo : memberCardInfoList) {
            MemberCardInfoCenterVo memberCardInfoCenterVo = getMemberCardInfoCenterVo(memberCardUpgradeVo, memberCardInfo);
            memberCardInfoCenterVos.add(memberCardInfoCenterVo);
            if (memberCardInfo.getRankCode().equals(memberCardUpgradeVo.getRankCode())) {
                memberCardUpgradeVo.setOpenValue(memberCardInfo.getOpenValue())
                        .setUseInDate(memberCardInfo.getUseInDate());
            }
        }
        memberCardUpgradeVo.setMemberCardInfoCenterVos(memberCardInfoCenterVos);
        return memberCardUpgradeVo;
    }

    /**
     * 获取指定会员卡相关信息
     *
     * @param memberCardUpgradeVo com.medusa.gruul.account.model.vo.MemberCardUpgradeVo
     * @param memberCardInfo      会员卡相关信息
     * @return com.medusa.gruul.account.model.vo.MemberCardInfoCenterVo
     */
    private MemberCardInfoCenterVo getMemberCardInfoCenterVo(MemberCardUpgradeVo memberCardUpgradeVo, MemberCardInfo memberCardInfo) {
        MemberCardInfoCenterVo memberCardInfoCenterVo = new MemberCardInfoCenterVo();
        memberCardInfoCenterVo.setMemberCardInfoId(memberCardInfo.getId());
        memberCardInfoCenterVo.setTitle(memberCardInfo.getTitle())
                .setRankCode(memberCardInfo.getRankCode())
                .setRankName(memberCardInfo.getRankName())
                .setOpenType(memberCardInfo.getOpenType())
                .setOpenValue(memberCardInfo.getOpenValue())
                .setUseInDate(memberCardInfo.getUseInDate())
                .setBackGroundPicUrl(memberCardInfo.getBackGroundPicUrl())
                .setIsOpen(memberCardInfo.getIsOpen());
        //实际支付金额（积分）：要升级套餐价格*剩余时长数/有效天数
        BigDecimal actualOpenValue = memberCardInfoCenterVo.getOpenValue()
                .multiply(new BigDecimal(memberCardUpgradeVo.getRemainDate()))
                .divide(new BigDecimal(memberCardInfoCenterVo.getUseInDate()), 2, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);

        if (CommonConstants.STRING_ZERO.equals(memberCardUpgradeVo.getRemainDate())) {
            actualOpenValue = memberCardInfoCenterVo.getOpenValue();
        }
        memberCardInfoCenterVo.setActualOpenValue(actualOpenValue);
        //获取店铺下特权开关为开启状态下的会员卡权益列表
        List<MemberPrivilegeVo> memberPrivilegeVos = memberCardPrivilegeMapper.selectMemberCardPrivilege(ISecurity.userMust().getShopId());
        for (MemberPrivilegeVo memberPrivilegeVo : memberPrivilegeVos) {
            MemberCardPrivilegeGroup memberCardPrivilegeGroup = memberCardPrivilegeGroupMapper.selectMemberCardPrivilegeGroup(ISecurity.userMust().getShopId(), memberCardInfo.getId(), memberPrivilegeVo.getId());
            if (memberCardPrivilegeGroup != null) {
                memberPrivilegeVo.setIsSelected(1);
                memberPrivilegeVo.setValue(memberCardPrivilegeGroup.getValue());
            } else {
                memberPrivilegeVo.setIsSelected(0);
            }
        }

        memberCardInfoCenterVo.setMemberCardPrivileges(memberPrivilegeVos);
        return memberCardInfoCenterVo;
    }

    /**
     * 小程序端升级会员等级
     *
     * @param upgradeApiMemberRankDto com.medusa.gruul.account.model.dto.UpgradeApiMemberRankDto
     * @return com.medusa.gruul.payment.api.model.dto.PayResultDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayResultDto upgradeMemberRank(UpgradeApiMemberRankDto upgradeApiMemberRankDto) {
        //付费购买
        if (upgradeApiMemberRankDto.getOpenType().equals(CommonConstants.NUMBER_ONE)) {
            //微信支付
            if (upgradeApiMemberRankDto.getPaymentType().equals(CommonConstants.NUMBER_ONE)) {
                return upgradeApiMemberWechatPay(upgradeApiMemberRankDto);

                //余额支付
            } else if (upgradeApiMemberRankDto.getPaymentType().equals(CommonConstants.NUMBER_TWO)) {
                upgradeApiMemberBalancePay(upgradeApiMemberRankDto);

            }
            //积分兑换
        } else if (upgradeApiMemberRankDto.getOpenType().equals(CommonConstants.NUMBER_TWO)) {
            SecureUser secureUser = ISecurity.userMust();
            String shopUserId = secureUser.getId();
            BigDecimal useAmount = upgradeApiMemberRankDto.getAvailableAmount();
            Result result = remoteIntegralService.conversionClubCard(shopUserId, useAmount);
            if (result.getCode() == CommonConstants.SUCCESS) {
                //会员升级
                upgradeApiMemberRankDto.setUserId(shopUserId);
                int update = memberInfoMapper.upgradeApiMemberRank(upgradeApiMemberRankDto);
                if (update < CommonConstants.NUMBER_ONE) {
                    throw new ServiceException("小程序端会员升级失败", SystemCode.DATA_UPDATE_FAILED.getCode());
                }
            } else {
                throw new ServiceException(result.getMsg(), SystemCode.DATA_UPDATE_FAILED.getCode());
            }

        }
        return new PayResultDto();

    }

    /**
     * 升級會員等級頁面
     * @param updateMemberRankPage 会员卡支付信息
     * @return Object
     */
    @Override
    public Object upgradeMemberRankPage(UpgradeApiMemberRankPageDto updateMemberRankPage) {
        PayParam payParam = new PayParam()
                .setSubject("会员升级")
                .setBody("会员升级")
                .setOrderId(updateMemberRankPage.getMemberNumber())
                .setPayType(updateMemberRankPage.getPayType())
                .setPrice(updateMemberRankPage.getPayAmount())
                .setRouteKey(AccountQueueNameConstant.MEMBER_PAY_OK_QUEUE_CHANGE)
                .setSeconds(15 * 60L)
                .setAttach(
                        Jackson.writeValueAsString(
                                new MemberCompletedVo()
                                        .setUserRank(updateMemberRankPage.getRankCode())
                                        .setMNumber(updateMemberRankPage.getMemberNumber())
                                        .setType(3)
                        )
                );
        return remotePaymentService.toPayPage(payParam).successData().getData();
    }

    /**
     * 微信支付
     *
     * @param upgradeApiMemberRankDto com.medusa.gruul.account.model.dto.UpgradeApiMemberRankDto
     * @return com.medusa.gruul.payment.api.model.dto.PayResultDto
     */
    private PayResultDto upgradeApiMemberWechatPay(UpgradeApiMemberRankDto upgradeApiMemberRankDto) {
        PayRequestDto payRequestDto = new PayRequestDto();

        //支付渠道 1-微信支付
        Integer payChannel = 1;
        int tradeType = 1;
        String outTradeNo = upgradeApiMemberRankDto.getMemberNumber();
        SecureUser secureUser = ISecurity.userMust();
        String userId = "";
        String openId = "";
        if (secureUser.getId() != null) {
            userId = secureUser.getId();
            openId = secureUser.getOpenid();
        }
        upgradeApiMemberRankDto.setUserId(userId);
        String routeKey = AccountQueueNameConstant.MEMBER_PAY_OK_QUEUE_CHANGE;
        BigDecimal totalFee = upgradeApiMemberRankDto.getAvailableAmount();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new ServiceException("数据异常");
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String teminalIp = IPUtils.getIpAddr(request);
        String feeType = "CNY";
        String timeoutExpress = "15m";
        String body = "会员升级";
        MemberCompletedVo memberCompletedVo = new MemberCompletedVo();
        memberCompletedVo.setUserRank(upgradeApiMemberRankDto.getRankCode())
                .setMNumber(upgradeApiMemberRankDto.getMemberNumber())
                .setType(3);
        String businessParams = Jackson.writeValueAsString(memberCompletedVo);
        payRequestDto.setPayChannel(payChannel);
        payRequestDto.setTradeType(tradeType);
        payRequestDto.setOutTradeNo(outTradeNo);
        payRequestDto.setRouteKey(routeKey);
        payRequestDto.setOpenId(openId);
        payRequestDto.setTotalFee(totalFee);
        payRequestDto.setTerminalIp(teminalIp);
        payRequestDto.setFeeType(feeType);
        payRequestDto.setTimeoutExpress(timeoutExpress);
        payRequestDto.setBody(body);
        payRequestDto.setBusinessParams(businessParams);
        PayResultDto payResultDto = remotePaymentService.payRequest(payRequestDto);
        log.debug("---:{}", payResultDto);
        return payResultDto;
    }

    /**
     * 余额支付升级会员
     *
     * @param upgradeApiMemberRankDto com.medusa.gruul.account.model.dto.UpgradeApiMemberRankDto
     */
    private void upgradeApiMemberBalancePay(UpgradeApiMemberRankDto upgradeApiMemberRankDto) {
        //会员升级
        int update = memberInfoMapper.upgradeApiMemberRank(upgradeApiMemberRankDto);
        if (update < 1) {
            throw new ServiceException("小程序端会员升级失败", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
        SecureUser secureUser = ISecurity.userMust();
        String userId = "";
        if (secureUser.getId() != null) {
            userId = secureUser.getId();
        }
        BigDecimal amount1 = upgradeApiMemberRankDto.getUseAmount().subtract(upgradeApiMemberRankDto.getAvailableAmount());

        //增加返利
        MiniAccountExtends miniAccountExtends = miniAccountExtendsService.findByShopUserId(userId);
        String shopId = ISecurity.userMust().getShopId();

        //划款金额小于等于可用余额
        if (upgradeApiMemberRankDto.getAvailableAmount().compareTo(miniAccountExtends.getSupplyBonus()) <= 0) {
            BigDecimal amount = miniAccountExtends.getSupplyBonus().subtract(upgradeApiMemberRankDto.getAvailableAmount());
            int updateBonus = miniAccountMapper.updateBonus(userId, amount);
            if (updateBonus < 1) {
                throw new ServiceException("小程序端更新会员余额失败", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        } else {//划款金额大于可用余额
            int updateBonus = miniAccountMapper.updateBonus(userId, new BigDecimal(0));
            if (updateBonus < 1) {
                throw new ServiceException("小程序端更新会员余额失败", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
            //获取返利设置，获取会员的返利设置
            RebateSet rebateSet = remoteGoodsService.getRebateSet(shopId);
            List<MemberInfo> memberInfoList = memberInfoMapper.selectList(new LambdaQueryWrapper<MemberInfo>().eq(MemberInfo::getState, CommonConstants.NUMBER_ONE).eq(MemberInfo::getUserId, userId));
            BigDecimal amount;
            //是会员
            if (memberInfoList != null && memberInfoList.size() > CommonConstants.NUMBER_ZERO) {
                if (rebateSet != null && rebateSet.getBalanceUseCondition() != null && rebateSet.getBalanceUseCondition().contains(CommonConstants.NUMBER_ONE.toString())) {
                    amount = miniAccountExtends.getRebateBonus().
                            subtract(upgradeApiMemberRankDto
                                    .getAvailableAmount()
                                    .subtract(miniAccountExtends.getSupplyBonus()));
                } else {
                    throw new ServiceException("小程序端更新会员余额失败,划款金额大于用户可用余额", SystemCode.DATA_UPDATE_FAILED.getCode());
                }
            } else {//不是会员,是普通用户
                if (rebateSet != null && rebateSet.getBalanceUseCondition() != null && rebateSet.getBalanceUseCondition().contains(CommonConstants.NUMBER_ZERO.toString())) {
                    amount = miniAccountExtends.getRebateBonus().subtract(upgradeApiMemberRankDto.getAvailableAmount().subtract(miniAccountExtends.getSupplyBonus()));
                } else {
                    throw new ServiceException("小程序端更新会员余额失败,划款金额大于用户可用余额", SystemCode.DATA_UPDATE_FAILED.getCode());
                }
            }

            int updateReBonus = miniAccountMapper.updateRebateBonus(userId, amount);
            if (updateReBonus < CommonConstants.NUMBER_ONE) {
                throw new ServiceException("小程序端更新会员余额失败", SystemCode.DATA_UPDATE_FAILED.getCode());
            }

        }

        //调取一下余额明细记录
        MemberInfo memberInfo = memberInfoMapper.selectOne(new LambdaQueryWrapper<MemberInfo>().eq(MemberInfo::getMemberNumber, upgradeApiMemberRankDto.getMemberNumber()));
        MemberBalanceRecord memberBalanceRecord = new MemberBalanceRecord();
        memberBalanceRecord.setChangeType(CommonConstants.NUMBER_ONE)
                .setDealType(CommonConstants.NUMBER_ZERO)
                .setDetailName("会员升级")
                .setMemberInfoId(memberInfo.getId())
                .setShopUserId(Long.valueOf(miniAccountExtends.getUserId()))
                .setOrderId(Long.parseLong(MemberBalanceRecordServiceImpl.generateOrderRandomNo(shopId, 7)))
                .setRemainingBalance(amount1)
                .setUseBalance(upgradeApiMemberRankDto.getAvailableAmount())
                .setOutOrderId(Long.parseLong(MemberInfoServiceImpl.generateRandomNo(shopId, 7)))
                .setSource(CommonConstants.NUMBER_ONE);
        int inserts = memberBalanceRecordMapper.insert(memberBalanceRecord);
        if (inserts < CommonConstants.NUMBER_ONE) {
            throw new ServiceException(STR."插入余额明细数据失败,外部订单号：\{memberBalanceRecord.getOutOrderId()}", SystemCode.DATA_ADD_FAILED.getCode());
        }
    }

    /**
     * 更新到期时间，付费购买
     */
    private class Balance {
        private final UpdateApiMemberExpireTimeDto updateApiMemberExpireTimeDto;
        private boolean myResult;
        @Getter
        private PayResultDto payResultDto;

        public Balance(UpdateApiMemberExpireTimeDto updateApiMemberExpireTimeDto) {
            this.updateApiMemberExpireTimeDto = updateApiMemberExpireTimeDto;
        }

        boolean is() {
            return myResult;
        }

        public Balance invoke() {
            if (updateApiMemberExpireTimeDto.getPaymentType().equals(CommonConstants.NUMBER_ONE)) {
                //微信支付
                return wechatPay();
            } else if (updateApiMemberExpireTimeDto.getPaymentType().equals(CommonConstants.NUMBER_TWO)) {
                //余额支付
                balancePay();
            }
            myResult = false;
            return this;
        }

        /**
         * 微信支付
         *
         * @return com.medusa.gruul.account.service.impl.ApiMemberInfoServiceImpl.Balance
         */
        private Balance wechatPay() {
            PayRequestDto payRequestDto = new PayRequestDto();
            /*ShopConfigDto shopConfig = remoteMiniInfoService.getShopConfig(tenantId);
            if (shopConfig == null) {
                throw new ServiceException("商户配置不存在");
            }

            PayInfoVo payInfo = shopConfig.getPayInfo();
            if (payInfo == null) {
                throw new ServiceException("支付配置不存在");
            }*/
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
            String outTradeNo = updateApiMemberExpireTimeDto.getMemberNumber();
            SecureUser secureUser = ISecurity.userMust();
            String userId = "";
            String openId = "";
            if (secureUser.getId() != null) {
                userId = secureUser.getId();
                openId = secureUser.getOpenid();
            }
            updateApiMemberExpireTimeDto.setUserId(userId);
            String routekey = AccountQueueNameConstant.MEMBER_PAY_OK_QUEUE_CHANGE;
            BigDecimal totalFee = updateApiMemberExpireTimeDto.getAvailableAmount();
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                throw new ServiceException("服务异常");
            }
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            String terminalIp = IPUtils.getIpAddr(request);
            String feeType = "CNY";
            String timeoutExpress = "15m";
            String body = "会员续费";
            MemberCompletedVo memberCompletedVo = new MemberCompletedVo();
            memberCompletedVo.setUserRank(userId)
                    .setMNumber(updateApiMemberExpireTimeDto.getMemberNumber())
                    .setType(2)
                    .setDate(updateApiMemberExpireTimeDto.getDate());
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
            payResultDto = remotePaymentService.payRequest(payRequestDto);
            log.debug("----：{}", payResultDto);
            myResult = true;
            return this;
        }

        /**
         * 余额支付
         */
        private void balancePay() {
            //会员续费
            //取时间
            Date date = DateUtil.parseDateTime(updateApiMemberExpireTimeDto.getMemberExpireTime());
            Calendar calendar = Calendar.getInstance();
            //需要将date数据转移到Calender对象中操作
            calendar.setTime(date);
            //把日期往后增加n天.正数往后推,负数往前移动
            calendar.add(Calendar.DATE, updateApiMemberExpireTimeDto.getDate().intValue());
            //这个时间就是日期往后推n天的结果
            date = calendar.getTime();
            updateApiMemberExpireTimeDto.setMemberExpireTime(DateUtil.formatDateTime(date));
            int update = memberInfoMapper.updateApiMemberExpireTime(updateApiMemberExpireTimeDto);
            if (update < 1) {
                throw new ServiceException("小程序端会员续费失败", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
            SecureUser secureUser = ISecurity.userMust();
            String userId = "";
            if (secureUser.getId() != null) {
                userId = secureUser.getId();
            }
            BigDecimal amount1 = updateApiMemberExpireTimeDto.getUseAmount().subtract(updateApiMemberExpireTimeDto.getAvailableAmount());

            //增加返利
            MiniAccountExtends miniAccountExtends = miniAccountExtendsService.findByShopUserId(userId);
            if (updateApiMemberExpireTimeDto.getAvailableAmount().compareTo(miniAccountExtends.getSupplyBonus()) <= CommonConstants.NUMBER_ZERO) {
                //划款金额小于等于可用余额
                BigDecimal amount = miniAccountExtends.getSupplyBonus().subtract(updateApiMemberExpireTimeDto.getAvailableAmount());
                int updateBonus = miniAccountMapper.updateBonus(userId, amount);
                if (updateBonus < CommonConstants.NUMBER_ONE) {
                    throw new ServiceException("小程序端更新会员余额失败", SystemCode.DATA_UPDATE_FAILED.getCode());
                }
            } else {
                //划款金额大于可用余额
                int updateBonus = miniAccountMapper.updateBonus(userId, new BigDecimal(CommonConstants.NUMBER_ZERO));
                if (updateBonus < CommonConstants.NUMBER_ONE) {
                    throw new ServiceException("小程序端更新会员余额失败", SystemCode.DATA_UPDATE_FAILED.getCode());
                }
                //获取返利设置，获取会员的返利设置
                String shopId = ISecurity.userMust().getShopId();
                RebateSet rebateSet = remoteGoodsService.getRebateSet(shopId);
                List<MemberInfo> memberInfoList = memberInfoMapper.selectList(new LambdaQueryWrapper<MemberInfo>().eq(MemberInfo::getState, CommonConstants.NUMBER_ONE).eq(MemberInfo::getUserId, userId));
                BigDecimal amount;
                //是会员
                if (CollectionUtil.isNotEmpty(memberInfoList)) {
                    if (rebateSet != null && rebateSet.getBalanceUseCondition() != null && rebateSet.getBalanceUseCondition().contains(CommonConstants.NUMBER_ONE.toString())) {
                        amount = miniAccountExtends.getRebateBonus().subtract(updateApiMemberExpireTimeDto.getAvailableAmount().subtract(miniAccountExtends.getSupplyBonus()));
                    } else {
                        throw new ServiceException("小程序端更新会员余额失败,划款金额大于用户可用余额", SystemCode.DATA_UPDATE_FAILED.getCode());
                    }
                } else {//不是会员,是普通用户
                    if (rebateSet != null && rebateSet.getBalanceUseCondition() != null && rebateSet.getBalanceUseCondition().contains(CommonConstants.NUMBER_ZERO.toString())) {
                        amount = miniAccountExtends.getRebateBonus().subtract(updateApiMemberExpireTimeDto.getAvailableAmount().subtract(miniAccountExtends.getSupplyBonus()));
                    } else {
                        throw new ServiceException("小程序端更新会员余额失败,划款金额大于用户可用余额", SystemCode.DATA_UPDATE_FAILED.getCode());
                    }
                }

                int updateReBonus = miniAccountMapper.updateRebateBonus(userId, amount);
                if (updateReBonus < 1) {
                    throw new ServiceException("小程序端更新会员余额失败", SystemCode.DATA_UPDATE_FAILED.getCode());
                }
            }

            MemberInfo memberInfo = memberInfoMapper.selectOne(new QueryWrapper<MemberInfo>().eq("member_number", updateApiMemberExpireTimeDto.getMemberNumber()));
            String shopId = ISecurity.userMust().getShopId();
            //调取一下余额明细记录
            MemberBalanceRecord memberBalanceRecord = new MemberBalanceRecord();
            memberBalanceRecord.setChangeType(CommonConstants.NUMBER_ONE)
                    .setDealType(CommonConstants.NUMBER_ZERO)
                    .setDetailName("会员续费")
                    .setMemberInfoId(memberInfo.getId())
                    .setShopUserId(Long.valueOf(miniAccountExtends.getUserId()))
                    .setOrderId(Long.parseLong(MemberBalanceRecordServiceImpl.generateOrderRandomNo(shopId, 7)))
                    .setRemainingBalance(amount1)
                    .setUseBalance(updateApiMemberExpireTimeDto.getAvailableAmount())
                    .setOutOrderId(Long.parseLong(MemberInfoServiceImpl.generateRandomNo(shopId, 7)))
                    .setSource(1);
            int inserts = memberBalanceRecordMapper.insert(memberBalanceRecord);
            if (inserts < 1) {
                throw new ServiceException(STR."插入余额明细数据失败,外部订单号：\{memberBalanceRecord.getOutOrderId()}", SystemCode.DATA_ADD_FAILED.getCode());
            }
        }
    }
}
