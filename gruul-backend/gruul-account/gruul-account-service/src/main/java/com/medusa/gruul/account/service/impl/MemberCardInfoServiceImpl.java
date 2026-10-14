package com.medusa.gruul.account.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.constant.AccountQueueNameConstant;
import com.medusa.gruul.account.api.entity.*;
import com.medusa.gruul.account.api.model.MemberCardInfoListDto;
import com.medusa.gruul.account.api.model.MemberCardInfoListResultDto;
import com.medusa.gruul.account.constant.AccountConstant;
import com.medusa.gruul.account.mapper.*;
import com.medusa.gruul.account.model.dto.MemberCardInfoRankStatusDto;
import com.medusa.gruul.account.model.dto.MemberCardInfoStatusDto;
import com.medusa.gruul.account.model.dto.UpdateApiMemberExpireTimeDto;
import com.medusa.gruul.account.model.dto.UpgradeApiMemberRankDto;
import com.medusa.gruul.account.model.vo.*;
import com.medusa.gruul.account.service.*;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.IPUtils;
import com.medusa.gruul.common.core.util.LocalDateTimeUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.goods.api.entity.RebateSet;
import com.medusa.gruul.goods.api.feign.RemoteGoodsService;
import com.medusa.gruul.integral.api.feign.RemoteIntegralService;
import com.medusa.gruul.payment.api.feign.RemotePaymentService;
import com.medusa.gruul.payment.api.model.dto.PayRequestDto;
import com.medusa.gruul.payment.api.model.dto.PayResultDto;
import com.medusa.gruul.payment.api.model.dto.WxPayNotifyResultDto;
import com.medusa.gruul.payment.api.model.pay.PayParam;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 会员卡信息 服务类
 * @Author zhaokw
 * @Date 23:26 2024\5\13 0013
 **/
@Service
public class MemberCardInfoServiceImpl extends ServiceImpl<MemberCardInfoMapper, MemberCardInfo> implements IMemberCardInfoService {

    @Autowired
    private MemberCardInfoMapper memberCardInfoMapper;

    @Autowired
    private MemberCardPrivilegeMapper memberCardPrivilegeMapper;

    @Autowired
    private IMemberCardPrivilegeGroupService memberCardPrivilegeGroupService;

    @Autowired
    private MemberCardPrivilegeGroupMapper memberCardPrivilegeGroupMapper;

    @Autowired
    private MemberCardGoodsMapper memberCardGoodsMapper;

    @Autowired
    private MemberRegisterFormMapper memberRegisterFormMapper;

    @Autowired
    private IMemberRegisterFormService memberRegisterFormService;

    @Autowired
    private MiniAccountMapper miniAccountMapper;

    @Autowired
    private RemotePaymentService remotePaymentService;

    @Autowired
    private MemberInfoMapper memberInfoMapper;

    @Autowired
    private MemberCardBaseInfoMapper memberCardBaseInfoMapper;

    @Autowired
    private MemberOrderInfoMapper memberOrderInfoMapper;

    @Autowired
    private RemoteIntegralService remoteIntegralService;

    @Autowired
    private RemoteGoodsService remoteGoodsService;

    @Autowired
    private IMiniAccountExtendsService miniAccountExtendsService;

    @Autowired
    private MemberBalanceRecordMapper memberBalanceRecordMapper;

    @Autowired
    private MemberSwitchMapper memberSwitchMapper;

    @Autowired
    private IMemberSwitchService memberSwitchService;

    /**
     * 后台关闭打开会员卡功能
     *
     * @param memberCardInfoStatusDto 会员卡功能关闭启用请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setMemberCardInfoStatus(MemberCardInfoStatusDto memberCardInfoStatusDto) {

        MemberSwitch memberSwitch = memberSwitchService.getByMemberModelMemberSwitch(memberCardInfoStatusDto.getMemberModel());
        if (memberSwitch != null) {
            memberSwitch.setState(memberCardInfoStatusDto.getMemberCardInfoSwitch());
            memberSwitchService.updateById(memberSwitch);
        } else {
            memberSwitch = new MemberSwitch();
            memberSwitch
                    .setMemberModel(memberCardInfoStatusDto.getMemberModel())
                    .setState(memberCardInfoStatusDto.getMemberCardInfoSwitch());
            memberSwitchService.save(memberSwitch);
        }
    }

    /**
     * 获取指定店铺下的会员卡信息
     *
     * @return MemberCardInfoListVo 会员卡信息
     */
    @Override
    public MemberCardInfoListVo getMemberCardInfo() {

        //获取指定店铺会员卡
        List<MemberCardInfo> memberCardInfoList = memberCardInfoMapper.getMemberCardInfoByShopId(ISecurity.userMust().getShopId());
        MemberCardInfoListVo memberCardInfoListVo = new MemberCardInfoListVo();
        if (CollectionUtil.isEmpty(memberCardInfoList)) {
            return memberCardInfoListVo;
        }
        //封装固定的3张会员卡，没有的进行插入
        for (int i = memberCardInfoList.size() + 1; i < CommonConstants.NUMBER_THREE; i++) {
            MemberCardInfo memberCardInfo = new MemberCardInfo();
            memberCardInfo.setIsOpen(1)
                    .setRankCode(String.valueOf(i))
                    .setStyle(1)
                    .setId(-1L);
            memberCardInfoList.add(memberCardInfo);
        }
        //封装会员卡基本共性信息
        MemberCardBaseInfo memberCardBaseInfo = this.getByMemberBaseInfo(ISecurity.userMust().getShopId());
        memberCardInfoListVo
                .setTitle(memberCardBaseInfo.getTitle())
                .setOpenType(memberCardBaseInfo.getCurrentOpenType())
                .setUseInDate(memberCardBaseInfo.getUseInDate());
        //获取店铺配置的会员卡权益
        List<MemberPrivilegeVo> memberPrivilegeVos = memberCardPrivilegeMapper.selectMemberCardPrivilege(ISecurity.userMust().getShopId());
        //封装每种类型开卡方式的会员卡
        List<MemberCardOpenInfoVo> memberCardOpenInfoVos = memberCardInfoList.parallelStream().map(memberCardInfo ->
                encapsulationMemberCardOpenVo(memberCardInfo, memberPrivilegeVos)).toList();
        //0-自动发卡 1-付费购买 2-积分兑换
        Map<Integer, List<MemberCardOpenInfoVo>> memberCardOpenInfoVoGroup = memberCardOpenInfoVos.stream()
                .collect(Collectors.groupingBy(MemberCardOpenInfoVo::getOpenType));
        List<MemberCardOpenInfoVo> paymentVos = memberCardOpenInfoVoGroup.get(CommonConstants.NUMBER_ONE);
        List<MemberCardOpenInfoVo> integralVos = memberCardOpenInfoVoGroup.get(CommonConstants.NUMBER_TWO);
        if (CollectionUtil.isNotEmpty(paymentVos)) {
            memberCardInfoListVo.setPaymentVos(sortByRankCodeMenberCardInfo(paymentVos));
        }
        if (CollectionUtil.isNotEmpty(integralVos)) {
            memberCardInfoListVo.setIntegralVos(sortByRankCodeMenberCardInfo(integralVos));
        }
        //获取会员卡注册填写表单
        List<MemberRegisterFormVo> memberRegisterFormVos = memberRegisterFormMapper.selectMemberRegisterForm(ISecurity.userMust().getShopId());
        if (CollectionUtil.isNotEmpty(memberRegisterFormVos)) {
            //按照注册表单id排序(升序)查询
            Collections.sort(memberRegisterFormVos,
                    Comparator.comparing(MemberRegisterFormVo::getId));
            memberCardInfoListVo.setMemberRegisterFormVos(memberRegisterFormVos);
        }
        SecureUser secureUser = ISecurity.userMust();
        String shopUserId = secureUser.getId();
        MiniAccount miniAccount = miniAccountMapper.selectByShopUserId(shopUserId);
        if (BeanUtil.isNotEmpty(miniAccount)) {
            memberCardInfoListVo.setUserId(shopUserId)
                    .setNikeName(miniAccount.getNikeName())
                    .setAvatarUrl(miniAccount.getAvatarUrl());
            //付费，获取可用余额
            if (memberCardInfoListVo.getOpenType().equals(CommonConstants.NUMBER_ONE)) {
                MiniAccountExtends accountExtends = miniAccountExtendsService.findByShopUserId(shopUserId);
                //获取返利设置里会员返利金额是否可用,可用：返利+用户余额  不可用：用户余额
                String shopId = ISecurity.userMust().getShopId();
                RebateSet rebateSet = remoteGoodsService.getRebateSet(shopId);
                List<MemberInfo> memberInfoList = memberInfoMapper.selectList(new LambdaQueryWrapper<MemberInfo>().eq(MemberInfo::getState, 1).eq(MemberInfo::getUserId, shopUserId));
                //是会员
                if (CollectionUtil.isNotEmpty(memberInfoList)) {

                    if (rebateSet != null && rebateSet.getBalanceUseCondition() != null && rebateSet.getBalanceUseCondition().contains(CommonConstants.NUMBER_ONE.toString())) {
                        memberCardInfoListVo.setUseAmount(accountExtends.getSupplyBonus().add(accountExtends.getRebateBonus()));
                    } else {
                        memberCardInfoListVo.setUseAmount(accountExtends.getSupplyBonus());
                    }
                    //不是会员,是普通用户
                } else {
                    if (rebateSet != null && rebateSet.getBalanceUseCondition() != null && rebateSet.getBalanceUseCondition().contains(CommonConstants.NUMBER_ZERO.toString())) {
                        memberCardInfoListVo.setUseAmount(accountExtends.getSupplyBonus().add(accountExtends.getRebateBonus()));
                    } else {
                        memberCardInfoListVo.setUseAmount(accountExtends.getSupplyBonus());
                    }
                }
                //积分，获取可用积分

            } else if (memberCardInfoListVo.getOpenType().equals(CommonConstants.NUMBER_TWO)) {
                BigDecimal integral = miniAccountMapper.getIntegralByShopUserId(secureUser.getId(), secureUser.getShopId());
                memberCardInfoListVo.setUseAmount(integral);
            }
        }

        return memberCardInfoListVo;
    }

    /**
     * 排序会员卡信息(升序)
     *
     * @return 排序好的会员卡
     */
    protected List<MemberCardOpenInfoVo> sortByRankCodeMenberCardInfo(List<MemberCardOpenInfoVo> openInfoVos) {
        return openInfoVos.stream().sorted(Comparator.comparing(MemberCardOpenInfoVo::getRankCode)).toList();
    }

    /**
     * 封装开卡方式会员卡数据
     *
     * @return com.medusa.gruul.account.model.vo.MemberCardOpenInfoVo
     */
    protected MemberCardOpenInfoVo encapsulationMemberCardOpenVo(MemberCardInfo memberCardInfo, List<MemberPrivilegeVo> memberPrivilegeVos) {
        //memberPrivilegeVos 转换成新的地址对象，防止操作值时操作相同的数据导致数据覆盖
        List<MemberPrivilegeVo> vos = memberPrivilegeVos.parallelStream().map(obj -> BeanUtil.toBean(obj, MemberPrivilegeVo.class)).toList();
        MemberCardOpenInfoVo openInfoVo = new MemberCardOpenInfoVo();
        openInfoVo.setId(memberCardInfo.getId())
                .setOpenType(memberCardInfo.getOpenType())
                .setRankCode(memberCardInfo.getRankCode())
                .setRankName(memberCardInfo.getRankName())
                .setPayAmount(memberCardInfo.getOpenValue())
                .setStyle(memberCardInfo.getStyle())
                .setBackGroundPicUrl(memberCardInfo.getBackGroundPicUrl())
                .setIsOpen(memberCardInfo.getIsOpen());
        //积分兑换方式封装积分所需值
        if (memberCardInfo.getOpenType().equals(CommonConstants.NUMBER_TWO)) {
            openInfoVo.setIntegralCount(memberCardInfo.getOpenValue());
        }
        //获取当前会员卡的权益
        vos.forEach(memberPrivilegeVo -> {
            MemberCardPrivilegeGroup memberCardPrivilegeGroup = memberCardPrivilegeGroupMapper
                    .selectMemberCardPrivilegeGroup(memberCardInfo.getShopId(), memberCardInfo.getId(), memberPrivilegeVo.getId());
            if (memberCardPrivilegeGroup != null) {
                memberPrivilegeVo.setIsSelected(1);
                memberPrivilegeVo.setValue(memberCardPrivilegeGroup.getValue());
            } else {
                memberPrivilegeVo.setIsSelected(0);
            }
        });
        openInfoVo.setMemberPrivilegeVos(vos);
        return openInfoVo;
    }

    /**
     * 后台停用开启会员卡各个等级
     *
     * @param memberCardInfoRankStatusDtoList 会员卡各个等级关闭启用请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMemberCardInfoStatus(List<MemberCardInfoRankStatusDto> memberCardInfoRankStatusDtoList) {

        int count = 0;
        for (MemberCardInfoRankStatusDto memberCardInfoRankStatusDto : memberCardInfoRankStatusDtoList) {
            MemberCardInfoRankStatusVo memberCardInfoRankStatusVo = new MemberCardInfoRankStatusVo();
            memberCardInfoRankStatusVo
                    .setMemberCardInfoSwitch(memberCardInfoRankStatusDto.getMemberCardInfoSwitch())
                    .setRankCode(memberCardInfoRankStatusDto.getRankCode())
                    .setUpdateTime(new Date());
            count = memberInfoMapper.getCountByRankCode(ISecurity.userMust().getShopId(), memberCardInfoRankStatusDto.getRankCode());
            if (count > 0 && CommonConstants.NUMBER_ZERO.equals(memberCardInfoRankStatusDto.getMemberCardInfoSwitch())) {
                break;
            }
            int update = memberCardInfoMapper.updateMemberCardInfoStatus(memberCardInfoRankStatusVo);
            if (update < 1) {
                throw new ServiceException("PC端关闭开启会员卡等级功能失败", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        }
        if (memberCardInfoRankStatusDtoList.get(0).getMemberCardInfoSwitch().equals(CommonConstants.NUMBER_ONE)) {
            return;
        }
        if (count > 0) {
            throw new ServiceException("当前存在该等级会员，无法停用！", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
    }

    /**
     * 后台保存会员卡信息
     *
     * @param memberCardInfoVo 会员卡信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMemberCardInfo(MemberCardInfoListVo memberCardInfoVo) {
        //组装插入会员表，如果根据店铺和会员卡等级和特权开关存在则更新，不存在插入
        List<MemberCardOpenInfoVo> memberCardOpenInfoVos;
        switch (memberCardInfoVo.getOpenType()) {
            case 1:
                memberCardOpenInfoVos = memberCardInfoVo.getPaymentVos();
                break;
            case 2:
                memberCardOpenInfoVos = memberCardInfoVo.getIntegralVos();
                break;
            default:
                throw new ServiceException("非法数据");
        }
        MemberCardBaseInfo memberCardBaseInfo = this.getByMemberBaseInfo(ISecurity.userMust().getShopId());
        memberCardBaseInfo.setCurrentOpenType(memberCardInfoVo.getOpenType());
        memberCardBaseInfo.setTitle(memberCardInfoVo.getTitle());
        memberCardBaseInfo.setUseInDate(memberCardInfoVo.getUseInDate());
        memberCardBaseInfoMapper.updateById(memberCardBaseInfo);

        for (MemberCardOpenInfoVo cardOpenInfoVo : memberCardOpenInfoVos) {
            MemberCardInfo rankCodeCardInfo = this.getByRankCodeCardInfo(memberCardInfoVo.getOpenType(), cardOpenInfoVo.getRankCode());
            if (rankCodeCardInfo == null) {
                rankCodeCardInfo = new MemberCardInfo();
            }
            rankCodeCardInfo
                    .setRankCode(cardOpenInfoVo.getRankCode())
                    .setIsOpen(cardOpenInfoVo.getIsOpen())
                    .setConsumeCondition(new BigDecimal(0))
                    .setBackGroundPicUrl(cardOpenInfoVo.getBackGroundPicUrl())
                    .setBuyGoods(0)
                    .setIntroduce(cardOpenInfoVo.getIntroduce())
                    .setRankName(cardOpenInfoVo.getRankName())
                    .setStyle(cardOpenInfoVo.getStyle())
                    .setOpenValue(cardOpenInfoVo.getPayAmount())
                    .setOpenType(memberCardInfoVo.getOpenType())
                    .setTitle(memberCardInfoVo.getTitle())
                    .setUseInDate(memberCardInfoVo.getUseInDate())
            ;
            //0-自动发卡 1-付费购买 2-积分兑换
            if (memberCardInfoVo.getOpenType().equals(CommonConstants.NUMBER_ONE)) {
                rankCodeCardInfo.setOpenValue(cardOpenInfoVo.getPayAmount());
            }
            //积分兑换
            if (memberCardInfoVo.getOpenType().equals(CommonConstants.NUMBER_TWO)) {
                rankCodeCardInfo.setOpenValue(cardOpenInfoVo.getIntegralCount());
            }
            if (!this.saveOrUpdate(rankCodeCardInfo)) {
                throw new ServiceException("保存新增修改失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
            if (rankCodeCardInfo.getId() == null) {
                throw new ServiceException("数据变更异常");
            }
            //根据权益的选中状态分组
            Map<Integer, List<MemberPrivilegeVo>> selectedGroup = cardOpenInfoVo.getMemberPrivilegeVos().stream().collect(Collectors.groupingBy(MemberPrivilegeVo::getIsSelected));
            List<MemberPrivilegeVo> notSelectedPrivilege = selectedGroup.get(CommonConstants.NUMBER_ZERO);
            if (CollectionUtil.isNotEmpty(notSelectedPrivilege)) {
                memberCardPrivilegeGroupService.removeByMemberCardIdAndPrivilegeIds(rankCodeCardInfo.getId(), notSelectedPrivilege.stream().map(MemberPrivilegeVo::getId).toList());
            }
            List<MemberPrivilegeVo> selectedPrivilege = selectedGroup.get(CommonConstants.NUMBER_ONE);
            //选中的权益
            if (CollectionUtil.isNotEmpty(selectedPrivilege)) {
                for (MemberPrivilegeVo memberPrivilegeVo : selectedPrivilege) {
                    MemberCardPrivilegeGroup memberCardPrivilegeGroup = memberCardPrivilegeGroupService
                            .selectByMemberCardIdAndPrivilegeId(rankCodeCardInfo.getId(), memberPrivilegeVo.getId());
                    if (memberCardPrivilegeGroup == null) {
                        memberCardPrivilegeGroup = new MemberCardPrivilegeGroup();
                        memberCardPrivilegeGroup
                                .setMemberCardId(rankCodeCardInfo.getId())
                                .setPrivilegeId(memberPrivilegeVo.getId());
                    }
                    memberCardPrivilegeGroup.setValue(memberPrivilegeVo.getValue());
                    memberCardPrivilegeGroupService.saveOrUpdate(memberCardPrivilegeGroup);
                }
            }
        }
        //处理会员注册表单
        memberRegisterFormService.operationMemberRegisterForm(memberCardInfoVo.getMemberRegisterFormVos());

    }

    /**
     * 获取指定店铺会员卡基本信息
     *
     * @param shopId 店铺id
     * @return MemberCardBaseInfo 会员卡基本信息
     */
    @Override
    public MemberCardBaseInfo getByMemberBaseInfo(String shopId) {
        MemberCardBaseInfo memberCardBaseInfo = memberCardBaseInfoMapper.selectOne(new QueryWrapper<>());
        //兼容老班，不存在数据创建
        if (memberCardBaseInfo == null) {
            memberCardBaseInfo = new MemberCardBaseInfo();
            memberCardBaseInfo.setUseInDate(365L);
            memberCardBaseInfo.setCurrentOpenType(CommonConstants.NUMBER_ONE);
            memberCardBaseInfoMapper.insert(memberCardBaseInfo);
        }
        return memberCardBaseInfo;
    }

    /**
     * 获取指定开卡方式与指定等级代码的会员卡
     *
     * @param openType 开卡方式
     * @param rankCode 会员等级代码
     * @return com.medusa.gruul.account.api.entity.MemberCardInfo
     */
    private MemberCardInfo getByRankCodeCardInfo(Integer openType, String rankCode) {
        return this.baseMapper.selectOne(new QueryWrapper<MemberCardInfo>()
                .eq("rank_code", rankCode)
                .eq("open_type", openType));

    }

    /**
     * 小程序端购买弹框点击确认付款或确认兑换
     *
     * @param confirmPaymentVo
     * @return com.medusa.gruul.payment.api.model.dto.PayResultDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayResultDto confirmPayment(ConfirmPaymentVo confirmPaymentVo) {
        String shopId = ISecurity.userMust().getShopId();
        /*
         * 检查用户是否已有会员卡
         */
        this.userAlreadyExitsMemberCard(ISecurity.userMust().getId());
        //付费购买
        if (confirmPaymentVo.getOpenType().equals(CommonConstants.NUMBER_ONE)) {
            //微信支付
            if (confirmPaymentVo.getPaymentType().equals(CommonConstants.NUMBER_ONE)) {
                return wechatPay(confirmPaymentVo, shopId);
                //余额支付
            } else if (confirmPaymentVo.getPaymentType().equals(CommonConstants.NUMBER_TWO)) {
                balancePay(confirmPaymentVo, shopId);
            }
            //积分兑换
        } else if (confirmPaymentVo.getOpenType().equals(CommonConstants.NUMBER_TWO)) {
            pointsfor(confirmPaymentVo, shopId);
        }
        return new PayResultDto();
    }

    /**
     * 微信支付
     *
     * @param confirmPaymentVo com.medusa.gruul.account.model.vo.ConfirmPaymentVo
     * @return com.medusa.gruul.payment.api.model.dto.PayResultDto
     */
    private PayResultDto wechatPay(ConfirmPaymentVo confirmPaymentVo, String shopId) {
        PayRequestDto payRequestDto = new PayRequestDto();
        //支付渠道 1-微信支付 2-环迅 3-随行 4-盛付通
        Integer payChannel = 1;
        int tradeType = CommonConstants.NUMBER_ONE;
        if (payChannel.equals(CommonConstants.NUMBER_TWO)) {
            tradeType = 101;
        } else if (payChannel.equals(CommonConstants.NUMBER_THREE)) {
            tradeType = 201;
        } else if (payChannel.equals(CommonConstants.NUMBER_FOUR)) {
            tradeType = 301;
        }
        String outTradeNo = MemberInfoServiceImpl.generateRandomNo(shopId, 7);
        SecureUser secureUser = ISecurity.userMust();
        String userId = secureUser.getId();
        String openId = secureUser.getOpenid();
        confirmPaymentVo.setMemberNumber(outTradeNo)
                .setUserId(userId);
        BigDecimal totalFee = confirmPaymentVo.getAvailableAmount();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new ServiceException("数据异常");
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String teminalIp = IPUtils.getIpAddr(request);
        String feeType = "CNY";
        String timeoutExpress = "15m";
        String body = "会员付费购买会员卡";
        MemberCompletedVo memberCompletedVo = new MemberCompletedVo();
        memberCompletedVo.setUserRank(userId)
                .setId(confirmPaymentVo.getMemberCardInfoId())
                .setMNumber(confirmPaymentVo.getMemberNumber())
                .setType(1);
        String businessParams = Jackson.writeValueAsString(memberCompletedVo);
        payRequestDto.setPayChannel(payChannel);
        payRequestDto.setTradeType(tradeType);
        payRequestDto.setOutTradeNo(outTradeNo);
        payRequestDto.setRouteKey(AccountQueueNameConstant.MEMBER_PAY_OK_QUEUE_CHANGE);
        payRequestDto.setOpenId(openId);
        payRequestDto.setTotalFee(totalFee);
        payRequestDto.setTerminalIp(teminalIp);
        payRequestDto.setFeeType(feeType);
        payRequestDto.setTimeoutExpress(timeoutExpress);
        payRequestDto.setBody(body);
        payRequestDto.setBusinessParams(businessParams);
        PayResultDto payResultDto = remotePaymentService.payRequest(payRequestDto);
        log.debug(Jackson.writeValueAsString(payResultDto));
        return payResultDto;
    }

    /**
     *  会员卡支付确认信息
     * @param confirmPaymentPage 确认支付信息
     * @return Object
     */
    @Override
    public Object confirmPaymentPage(ConfirmPaymentPageVO confirmPaymentPage) {
        /*
         * 检查是否用户已存在会员卡
         */
        this.userAlreadyExitsMemberCard(ISecurity.userMust().getId());
        /*
         * 检查是否已开通会员卡
         */
        MemberCardInfo memberCard = memberCardInfoMapper.getApiMemberCardInfo(confirmPaymentPage.getMemberCardInfoId());
        if (memberCard == null) {
            throw new ServiceException("该会员卡已不存在.");
        }
        /*
         * 检查付款金额
         */
        BigDecimal amount = memberCard.getOpenValue();
        if (confirmPaymentPage.getPayAmount().compareTo(amount) != 0) {
            throw new ServiceException("付款金额不一致,没有通过检查");
        }
        if (BigDecimal.ZERO.compareTo(amount) > 0) {
            amount = new BigDecimal("0.01");
        }
        String memberCardNo = MemberInfoServiceImpl.generateRandomNo(ISecurity.userMust().getShopId(), 7);
        PayParam payParam = new PayParam()
                .setSubject("购买会员卡")
                .setBody("付费购买会员卡:" + memberCard.getTitle() + "|" + memberCard.getRankCode())
                .setPayType(confirmPaymentPage.getPayType())
                .setOrderId(memberCardNo)
                .setRouteKey(AccountQueueNameConstant.MEMBER_PAY_OK_QUEUE_CHANGE)
                .setPrice(amount)
                .setAttach(
                        Jackson.writeValueAsString(
                                new MemberCompletedVo()
                                        .setUserRank(ISecurity.userMust().getId())
                                        .setId(confirmPaymentPage.getMemberCardInfoId())
                                        .setMNumber(memberCardNo)
                                        .setType(1)
                        )
                )
                .setSeconds(15 * 60L);
        return remotePaymentService.toPayPage(payParam).successData().getData();
    }

    /**
     * 检查是否已开通会员
     * 用户已存在会员卡
     */
    public void userAlreadyExitsMemberCard(String userId) {
        Long count = memberInfoMapper.selectCount(
                new QueryWrapper<MemberInfo>()
                        .ge("member_expire_time", DateUtil.date().toString())
                        .eq("user_id", userId)
        );
        if (count > 0) {
            throw new ServiceException("您已开通会员卡,请勿重复开通！可选择续费或升级.", SystemCode.DATA_ADD_FAILED.getCode());
        }
    }

    /**
     * 余额支付
     *
     * @param confirmPaymentVo com.medusa.gruul.account.model.vo.ConfirmPaymentVo
     * @param shopId           店铺id
     */
    private void balancePay(ConfirmPaymentVo confirmPaymentVo, String shopId) {
        //保存开通会员
        Date memberRegisterTime = new Date();
        //计算会员到期日期
        DateTime memberExpireTime = DateUtil.offsetDay(memberRegisterTime, confirmPaymentVo.getUseInDate().intValue());
        SecureUser secureUser = ISecurity.userMust();
        String userId = "";
        if (secureUser != null && secureUser.getId() != null) {
            userId = secureUser.getId();
        }

        //增加返利
        MiniAccountExtends miniAccountExtends = miniAccountExtendsService.findByShopUserId(userId);
        //划款金额小于等于可用余额
        if (confirmPaymentVo.getAvailableAmount().compareTo(miniAccountExtends.getSupplyBonus()) <= 0) {
            BigDecimal amount = miniAccountExtends.getSupplyBonus().subtract(confirmPaymentVo.getAvailableAmount());
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
            RebateSet rebateSet = remoteGoodsService.getRebateSet(miniAccountExtends.getShopId());
            List<MemberInfo> memberInfoList = memberInfoMapper.selectList(new LambdaQueryWrapper<MemberInfo>().eq(MemberInfo::getState, 1).eq(MemberInfo::getUserId, userId));
            BigDecimal amount = new BigDecimal(0);
            //是会员
            if (memberInfoList != null && memberInfoList.size() > 0) {
                if (rebateSet != null && rebateSet.getBalanceUseCondition() != null && rebateSet.getBalanceUseCondition().contains(CommonConstants.NUMBER_ONE.toString())) {
                    amount = miniAccountExtends.getRebateBonus().subtract(confirmPaymentVo.getAvailableAmount().subtract(miniAccountExtends.getSupplyBonus()));
                } else {
                    throw new ServiceException("小程序端更新会员余额失败,划款金额大于用户可用余额", SystemCode.DATA_UPDATE_FAILED.getCode());
                }
            } else {//不是会员,是普通用户
                if (rebateSet != null && rebateSet.getBalanceUseCondition() != null && rebateSet.getBalanceUseCondition().contains(CommonConstants.NUMBER_ZERO.toString())) {
                    amount = miniAccountExtends.getRebateBonus().subtract(confirmPaymentVo.getAvailableAmount().subtract(miniAccountExtends.getSupplyBonus()));
                } else {
                    throw new ServiceException("小程序端更新会员余额失败,划款金额大于用户可用余额", SystemCode.DATA_UPDATE_FAILED.getCode());
                }
            }
            int updateReBonus = miniAccountMapper.updateRebateBonus(userId, amount);
            if (updateReBonus < 1) {
                throw new ServiceException("小程序端更新会员余额失败", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
        }

        BigDecimal amount1 = confirmPaymentVo.getUseAmount().subtract(confirmPaymentVo.getAvailableAmount());

        MemberInfo memberInfo = new MemberInfo();
        memberInfo
                .setMemberCardInfoId(confirmPaymentVo.getMemberCardInfoId())
                .setMemberNumber(MemberInfoServiceImpl.generateRandomNo(shopId, 7))
                .setMemberRegisterTime(DateUtil.toLocalDateTime(memberRegisterTime))
                .setMemberExpireTime(DateUtil.toLocalDateTime(memberExpireTime))
                .setMemberRegisterType(0)
                .setState(1)
                .setUserId(userId);
        int insert = memberInfoMapper.insert(memberInfo);
        if (insert < 1) {
            throw new ServiceException("小程序端余额购买会员失败", SystemCode.DATA_ADD_FAILED.getCode());
        }
        //调取一下余额明细记录
        MemberBalanceRecord memberBalanceRecord = new MemberBalanceRecord();
        memberBalanceRecord.setChangeType(1)
                .setDealType(0)
                .setDetailName("开通会员")
                .setMemberInfoId(memberInfo.getId())
                .setShopUserId(Long.valueOf(miniAccountExtends.getUserId()))
                .setOrderId(Long.parseLong(MemberBalanceRecordServiceImpl.generateOrderRandomNo(secureUser.getShopId(), 7)))
                .setRemainingBalance(amount1)
                .setUseBalance(confirmPaymentVo.getAvailableAmount())
                .setOutOrderId(Long.parseLong(MemberInfoServiceImpl.generateRandomNo(secureUser.getShopId(), 7)))
                .setSource(1);
        int inserts = memberBalanceRecordMapper.insert(memberBalanceRecord);
        if (inserts < 1) {
            throw new ServiceException("插入余额明细数据失败,外部订单号：" + memberBalanceRecord.getOutOrderId(), SystemCode.DATA_ADD_FAILED.getCode());
        }
    }

    /**
     * 积分兑换
     *
     * @param confirmPaymentVo com.medusa.gruul.account.model.vo.ConfirmPaymentVo
     * @param shopId           店铺id
     */
    private void pointsfor(ConfirmPaymentVo confirmPaymentVo, String shopId) {
        SecureUser secureUser = ISecurity.userMust();
        String shopUserId = secureUser.getId();
        BigDecimal useAmount = confirmPaymentVo.getAvailableAmount();
        Result result = remoteIntegralService.conversionClubCard(shopUserId, useAmount);
        if (result.getCode() == CommonConstants.SUCCESS) {
            //保存开通会员
            Date memberRegisterTime = new Date();
            //计算会员到期日期
            DateTime memberExpireTime = DateUtil.offsetDay(memberRegisterTime, confirmPaymentVo.getUseInDate().intValue());
            String userId = "";
            if (secureUser != null && secureUser.getId() != null) {
                userId = secureUser.getId();
            }
            MemberInfo memberInfo = new MemberInfo();
            memberInfo.setMemberCardInfoId(confirmPaymentVo.getMemberCardInfoId())
                    .setMemberNumber(MemberInfoServiceImpl.generateRandomNo(shopId, 7))
                    .setMemberRegisterTime(DateUtil.toLocalDateTime(memberRegisterTime))
                    .setMemberExpireTime(DateUtil.toLocalDateTime(memberExpireTime))
                    .setMemberRegisterType(1)
                    .setState(1)
                    .setUserId(userId);
            List<MemberInfo> memberInfos = memberInfoMapper.selectList(new LambdaQueryWrapper<MemberInfo>()
                    .eq(MemberInfo::getMemberCardInfoId, memberInfo.getMemberCardInfoId())
                    .ge(MemberInfo::getMemberExpireTime, DateUtil.date().toString())
                    .eq(MemberInfo::getUserId, memberInfo.getUserId())
                    .eq(MemberInfo::getShopId, shopId));
            if (CollectionUtil.isNotEmpty(memberInfos)) {
                throw new ServiceException("开通会员失败，已开通该类型会员请勿重复开通！", SystemCode.DATA_ADD_FAILED.getCode());
            } else {
                int insert = memberInfoMapper.insert(memberInfo);
                if (insert < 1) {
                    throw new ServiceException("小程序端积分兑换购买会员失败", SystemCode.DATA_ADD_FAILED.getCode());
                }
            }


        } else {
            throw new ServiceException(result.getMsg(), SystemCode.DATA_UPDATE_FAILED.getCode());
        }
    }

    /**
     * 会员开卡处理
     *
     * @param wxPayNotifyResultDto 微信支付信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void memberCompleted(WxPayNotifyResultDto wxPayNotifyResultDto) {
        if (wxPayNotifyResultDto == null) {
            throw new ServiceException("数据异常");
        }
        //转换交易参数对象
        MemberCompletedVo memberCompletedVo = Jackson.readValue(wxPayNotifyResultDto.getBusinessParams(), MemberCompletedVo.class);
        //开通会员
        if (memberCompletedVo.getType() == 1) {
            //保存开通会员
            MemberCardInfo memberCardInfo = memberCardInfoMapper.getApiMemberCardInfo(memberCompletedVo.getId());
            Date memberRegisterTime = new Date();
            //计算会员到期日期
            DateTime memberExpireTime = DateUtil.offsetDay(memberRegisterTime, memberCardInfo.getUseInDate().intValue());
            MemberInfo memberInfo = new MemberInfo();
            memberInfo.setMemberCardInfoId(memberCompletedVo.getId())
                    .setMemberExpireTime(DateUtil.toLocalDateTime(memberExpireTime))
                    .setMemberNumber(memberCompletedVo.getMNumber())
                    .setMemberRegisterTime(DateUtil.toLocalDateTime(memberRegisterTime))
                    .setMemberRegisterType(0)
                    .setState(1)
                    .setUserId(memberCompletedVo.getUserRank());
            List<MemberInfo> memberInfos = memberInfoMapper.selectList(new QueryWrapper<MemberInfo>()
                    .eq("member_card_info_id", memberInfo.getMemberCardInfoId())
                    .eq("user_id", memberInfo.getUserId())
                    .ge("member_expire_time", DateUtil.date().toString()));
            if (CollectionUtil.isNotEmpty(memberInfos)) {
                throw new ServiceException("开通会员失败，已开通该类型会员请勿重复开通！", SystemCode.DATA_ADD_FAILED.getCode());
            } else {
                int insert = memberInfoMapper.insert(memberInfo);
                if (insert < 1) {
                    throw new ServiceException("小程序端积分兑换购买会员失败", SystemCode.DATA_ADD_FAILED.getCode());
                }
            }
            //续费会员
        } else if (memberCompletedVo.getType().equals(CommonConstants.NUMBER_TWO)) {
            MemberInfo memberInfo = memberInfoMapper.selectOne(new QueryWrapper<MemberInfo>().eq("member_number", memberCompletedVo.getMNumber()));
            UpdateApiMemberExpireTimeDto updateApiMemberExpireTimeDto = new UpdateApiMemberExpireTimeDto();
            DateTime dateTime = DateUtil.offsetDay(LocalDateTimeUtils.convertLdtToDate(memberInfo.getMemberExpireTime()), memberCompletedVo.getDate().intValue());
            updateApiMemberExpireTimeDto
                    .setMemberNumber(memberCompletedVo.getMNumber())
                    .setMemberExpireTime(dateTime.toString());
            int update = memberInfoMapper.updateApiMemberExpireTime(updateApiMemberExpireTimeDto);
            if (update < 1) {
                throw new ServiceException("小程序端会员续费失败", SystemCode.DATA_UPDATE_FAILED.getCode());
            }

            //升级会员
        } else if (memberCompletedVo.getType().equals(CommonConstants.NUMBER_THREE)) {
            //会员升级
            MemberInfo memberInfo = memberInfoMapper.selectOne(new QueryWrapper<MemberInfo>().eq("member_number", memberCompletedVo.getMNumber()));
            UpgradeApiMemberRankDto upgradeApiMemberRankDto = new UpgradeApiMemberRankDto();
            upgradeApiMemberRankDto.setRankCode(memberCompletedVo.getUserRank());
            upgradeApiMemberRankDto.setMemberNumber(memberCompletedVo.getMNumber());
            int update = memberInfoMapper.upgradeApiMemberRank(upgradeApiMemberRankDto);
            if (update < 1) {
                throw new ServiceException("小程序端会员升级失败", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
            //会员充值
        } else if (memberCompletedVo.getType().equals(CommonConstants.NUMBER_FOUR)) {
            memberRecharge(wxPayNotifyResultDto, memberCompletedVo);
            //用户充值
        } else if (memberCompletedVo.getType().equals(CommonConstants.NUMBER_SIX)) {
            userRecharge(wxPayNotifyResultDto, memberCompletedVo);
        }

    }

    /**
     * 会员充值
     *
     * @param wxPayNotifyResultDto com.medusa.gruul.payment.api.model.dto.WxPayNotifyResultDto
     * @param memberCompletedVo    com.medusa.gruul.account.model.vo.MemberCompletedVo
     */
    private void memberRecharge(WxPayNotifyResultDto wxPayNotifyResultDto, MemberCompletedVo memberCompletedVo) {
        log.warn("打印memberCompletedVo ===================》");
        log.warn(memberCompletedVo.toString());
        String orderId = wxPayNotifyResultDto.getOutTradeNo();
        MemberOrderInfo memberOrderInfo = new MemberOrderInfo();
        MemberCardInfo memberCardInfo = memberCardInfoMapper.getApiMemberCardInfo(memberCompletedVo.getId());
        //An instantaneous point on the time-line.(时间线上的一个瞬时点。)
        Instant instant = new Date().toInstant();
        //A time-zone ID, such as {@code Europe/Paris}.(时区)
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        MiniAccountExtends accountExtends = miniAccountExtendsService.findByShopUserId(memberCompletedVo.getUserRank());
        //计算充值金额与赠送金额
        BigDecimal payAmount = memberCompletedVo.getPAmount().add(memberCompletedVo.getDAmount());
        //累计用户金额
        memberOrderInfo.setUsableAmount(accountExtends.getSupplyBonus().add(payAmount));
        memberOrderInfo.setMemberCardId(memberCompletedVo.getId())
                .setOrderId(Long.parseLong(orderId))
                .setPayAmount(payAmount)
                .setPayTime(localDateTime)
                .setUserId(memberCompletedVo.getUserRank());
        //会员充值，插入储值订单
        int insert = memberOrderInfoMapper.insert(memberOrderInfo);
        if (insert < 1) {
            throw new ServiceException("小程序端会员插入储值订单失败", SystemCode.DATA_ADD_FAILED.getCode());
        }
        //更新会员账户余额
        BigDecimal amount = memberOrderInfo.getUsableAmount();
        int updateBonus = miniAccountMapper.updateBonus(memberCompletedVo.getUserRank(), amount);
        if (updateBonus < 1) {
            throw new ServiceException("小程序端更新会员余额失败", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
        //调取一下余额明细记录
        MemberInfo memberInfo = memberInfoMapper.selectOne(new QueryWrapper<MemberInfo>()
                .eq("user_id", memberCompletedVo.getUserRank())
                .eq("member_card_info_id", memberCompletedVo.getId())
                .eq("is_deleted", Boolean.FALSE)
                .last(CommonConstants.SQL_LIMIT_1));
        MemberBalanceRecord memberBalanceRecord = new MemberBalanceRecord();
        memberBalanceRecord.setChangeType(0)
                .setDealType(1)
                .setDetailName("充值（参与充值活动充" + memberCompletedVo.getPAmount() + "送" + memberCompletedVo.getDAmount() + ")")
                .setMemberInfoId(memberInfo.getId())
                .setShopUserId(Long.valueOf(accountExtends.getUserId()))
                .setOrderId(Long.parseLong(MemberBalanceRecordServiceImpl.generateOrderRandomNo(memberCardInfo.getShopId(), 7)))
                .setRemainingBalance(amount)
                .setUseBalance(memberOrderInfo.getPayAmount())
                .setOutOrderId(Long.parseLong(orderId))
                .setSource(1);
        int inserts = memberBalanceRecordMapper.insert(memberBalanceRecord);
        if (inserts < 1) {
            throw new ServiceException("插入余额明细数据失败,外部订单号：" + memberBalanceRecord.getOutOrderId(), SystemCode.DATA_ADD_FAILED.getCode());
        }
    }

    /**
     * 用户充值
     *
     * @param wxPayNotifyResultDto com.medusa.gruul.payment.api.model.dto.WxPayNotifyResultDto
     * @param memberCompletedVo    com.medusa.gruul.account.model.vo.MemberCompletedVo
     */
    private void userRecharge(WxPayNotifyResultDto wxPayNotifyResultDto, MemberCompletedVo memberCompletedVo) {
        log.warn("打印memberCompletedVo ===================》");
        log.warn(memberCompletedVo.toString());
        String orderId = wxPayNotifyResultDto.getOutTradeNo();
        MiniAccountExtends accountExtends = miniAccountExtendsService.findByShopUserId(memberCompletedVo.getUserRank());
        //计算充值金额与赠送金额
        BigDecimal payAmount = memberCompletedVo.getPAmount().add(memberCompletedVo.getDAmount());
        //更新用户账户余额
        BigDecimal amount = accountExtends.getSupplyBonus().add(payAmount);
        int updateBonus = miniAccountMapper.updateBonus(memberCompletedVo.getUserRank(), amount);

        if (updateBonus < 1) {
            throw new ServiceException("小程序端更新用户余额失败", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
        MemberBalanceRecord memberBalanceRecord = new MemberBalanceRecord();
        memberBalanceRecord.setChangeType(0)
                .setDealType(6)
                .setDetailName("用户充值")
                .setShopUserId(Long.valueOf(accountExtends.getUserId()))
                .setOrderId(Long.parseLong(MemberBalanceRecordServiceImpl.generateOrderRandomNo(accountExtends.getShopId(), 7)))
                .setRemainingBalance(amount)
                .setUseBalance(memberCompletedVo.getPAmount())
                .setOutOrderId(Long.parseLong(orderId))
                .setSource(1);
        int inserts = memberBalanceRecordMapper.insert(memberBalanceRecord);
        if (inserts < 1) {
            throw new ServiceException("插入余额明细数据失败,外部订单号：" + memberBalanceRecord.getOutOrderId(), SystemCode.DATA_ADD_FAILED.getCode());
        }
    }

    /**
     * 小程序端购买会员卡填写信息
     *
     * @param registerDataVo com.medusa.gruul.account.model.vo.RegisterDataVo
     * @return com.medusa.gruul.account.model.vo.MemberCardInfoResVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MemberCardInfoResVo updateRegisterData(RegisterDataVo registerDataVo) {
        //校验验证码
        String code = RedisUtil.getCacheObject(RedisUtil.key(AccountConstant.ACCOUNT_KEY, AccountConstant.MEMBER_KEY.concat(registerDataVo.getPhone())));
        if (StrUtil.isEmpty(code)) {
            throw new ServiceException("无效验证码");
        }
        if (!registerDataVo.getCode().equals(code)) {
            throw new ServiceException("验证码输入有误，请重新输入！", SystemCode.FAILURE_CODE);
        }
        //获取表单校验规则
        List<MemberRegisterForm> memberRegisterForms = memberRegisterFormMapper.
                selectList(new QueryWrapper<MemberRegisterForm>().orderByAsc("form_id"));
        //过滤手机号校验规则,忽略手机表单校验
        memberRegisterForms = memberRegisterForms.stream().filter(obj -> !obj.getFormId().equals(CommonConstants.NUMBER_ONE.toString())).toList();

        Map<String, RegisterDataVo.FormExdDto> fromExdMap = registerDataVo.getFormExdDto().stream().collect(Collectors.toMap(RegisterDataVo.FormExdDto::getFormId, v -> v));
        for (MemberRegisterForm memberRegisterForm : memberRegisterForms) {
            RegisterDataVo.FormExdDto formExdDto = fromExdMap.get(memberRegisterForm.getFormId());
            if (formExdDto == null) {
                throw new ServiceException(SystemCode.DATA_NOT_EXIST);
            }
            boolean isRequired = memberRegisterForm.getIsRequired().equals(CommonConstants.NUMBER_ONE);
            //校验必填项不能为空
            if (isRequired && StrUtil.isEmpty(formExdDto.getValue())) {
                throw new ServiceException("更新表单信息失败，必填项不能为空！", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
            //如果是身份证
            if (memberRegisterForm.getFormPattern().equals(CommonConstants.NUMBER_ONE)) {
                //属于必填项,必须判断身份证是否正确,不是必填,只有在已填写的情况下才进行判断身份证是否正确
                if (isRequired) {
                    if (!IdcardUtil.isValidCard(formExdDto.getValue())) {
                        throw new ServiceException("更新表单信息失败,身份证格式不正确！", SystemCode.DATA_UPDATE_FAILED.getCode());
                    }
                } else {
                    if (StrUtil.isNotEmpty(formExdDto.getValue()) && !IdcardUtil.isValidCard(formExdDto.getValue())) {
                        throw new ServiceException("更新表单信息失败,身份证格式不正确！", SystemCode.DATA_UPDATE_FAILED.getCode());
                    }
                }
            }
        }
        SecureUser secureUser = ISecurity.userMust();
        String userId = "";
        if (secureUser != null && secureUser.getId() != null) {
            userId = secureUser.getId();
        }
        MemberInfo memberInfo = new MemberInfo();
        memberInfo.setUserId(userId)
                .setRegisterData(Jackson.writeValueAsString(registerDataVo.getFormExdDto()));
        int update = this.memberInfoMapper.updateRegisterData(memberInfo);
        if (update < 1) {
            throw new ServiceException("小程序端更新注册表单信息失败", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
        List<MemberInfo> memberInfos = memberInfoMapper.getMemberInfoByUserId(memberInfo.getUserId());
        memberInfo.setId(memberInfos.get(0).getId());
        if (memberInfo.getId() == null) {
            throw new ServiceException("小程序端更新注册表单信息失败", SystemCode.DATA_UPDATE_FAILED.getCode());
        }
        memberInfo = memberInfoMapper.selectById(memberInfo.getId());
        MemberCardInfo memberCardInfo = memberCardInfoMapper.getApiMemberCardInfo(memberInfo.getMemberCardInfoId());

        MemberCardInfoResVo memberCardInfoResVo = new MemberCardInfoResVo();
        memberCardInfoResVo.setMemberCardId(memberInfo.getMemberCardInfoId())
                .setMemberExpireTime(memberInfo.getMemberExpireTime())
                .setMemberId(memberInfo.getId())
                .setMemberNumber(memberInfo.getMemberNumber())
                .setMemberRegisterTime(memberInfo.getMemberRegisterTime())
                .setRankCode(memberCardInfo.getRankCode())
                .setRankName(memberCardInfo.getRankName())
                .setTitle(memberCardInfo.getTitle())
                .setUseInDate(memberCardInfo.getUseInDate());

        return memberCardInfoResVo;

    }

    /**
     * 小程序会员中心
     *
     * @return com.medusa.gruul.account.model.vo.MemberCenterVo
     */
    @Override
    public MemberCenterVo memberCenter() {
        MemberCenterVo memberCenterVo = new MemberCenterVo();

        String shopUserId = ISecurity.userMust().getId();
        if (shopUserId != null) {
            MiniAccountExtends accountExtends = miniAccountExtendsService.findByShopUserId(shopUserId);
            //账户总余额
            memberCenterVo.setAccountBalance(accountExtends.getSupplyBonus().add(accountExtends.getRebateBonus()));
            //获取返利设置里会员返利金额是否可用,可用：返利+用户余额  不可用：用户余额
            String shopId = ISecurity.userMust().getShopId();
            RebateSet rebateSet = remoteGoodsService.getRebateSet(shopId);
            List<MemberInfo> memberInfoList = memberInfoMapper.selectList(new LambdaQueryWrapper<MemberInfo>().eq(MemberInfo::getState, 1).eq(MemberInfo::getUserId, shopUserId));
            //是会员
            if (memberInfoList != null && memberInfoList.size() > 0) {
                if (rebateSet != null && rebateSet.getBalanceUseCondition() != null && rebateSet.getBalanceUseCondition().contains(CommonConstants.NUMBER_ONE.toString())) {
                    memberCenterVo.setAccountUseBalance(accountExtends.getSupplyBonus().add(accountExtends.getRebateBonus()));
                } else {
                    memberCenterVo.setAccountUseBalance(accountExtends.getSupplyBonus());
                }
            } else {
                //不是会员,是普通用户
                if (rebateSet != null && rebateSet.getBalanceUseCondition() != null && rebateSet.getBalanceUseCondition().contains(CommonConstants.NUMBER_ZERO.toString())) {
                    memberCenterVo.setAccountUseBalance(accountExtends.getSupplyBonus().add(accountExtends.getRebateBonus()));
                } else {
                    memberCenterVo.setAccountUseBalance(accountExtends.getSupplyBonus());
                }
            }
            //账户消费返利余额
            memberCenterVo.setRebateBonus(accountExtends.getRebateBonus());
            BigDecimal integral = miniAccountMapper.getIntegralByShopUserId(shopUserId, ISecurity.userMust().getShopId());
            //账户可用积分
            memberCenterVo.setUseAmount(integral);
            List<MemberCardInfoCenterVo> memberCardInfoCenterVos = new ArrayList<>();
            List<MemberInfo> memberInfos = memberInfoMapper.getMemberInfoByUserId(shopUserId);
            for (MemberInfo memberInfo : memberInfos) {
                MemberCardInfoCenterVo memberCardInfoCenterVo = getMemberCardInfoCenterVo(memberInfo);
                memberCardInfoCenterVos.add(memberCardInfoCenterVo);
            }
            List<MemberSwitch> memberSwitchList = memberSwitchMapper.selectList(new LambdaQueryWrapper<MemberSwitch>().eq(MemberSwitch::getMemberModel, 1));
            memberCenterVo.setState(memberSwitchList.get(0).getState());
            List<MemberCardInfo> memberCardInfoList = memberCardInfoMapper.getMemberCardInfoByShopId(ISecurity.userMust().getShopId());
            memberCardInfoList.stream().sorted(Comparator.comparing(MemberCardInfo::getRankCode)).toList();
            // 会员续费方式 要查询当前开卡信息方式
            MemberCardBaseInfo memberBaseInfo = this.getByMemberBaseInfo(ISecurity.userMust().getShopId());
            memberCenterVo.setOpenType(memberCardInfoCenterVos.get(0).getOpenType());
            if (BeanUtil.isNotEmpty(memberBaseInfo)) {
                memberCenterVo.setOpenType(memberBaseInfo.getCurrentOpenType());
                //获取用户当前会员卡开卡方式及会员等级
                MemberCardInfoCenterVo memberCardInfoCenterVo = memberCardInfoCenterVos.get(0);
                String rankCode = memberCardInfoCenterVo.getRankCode();
                Integer openType = memberCardInfoCenterVo.getOpenType();
                // 如果用户当前会员卡 获取方式不等于现有会员卡开卡方式 查看用户持有会员卡等级是否存在于当前开卡方式会员等级
                if (!memberBaseInfo.getCurrentOpenType().equals(openType)) {
                    if (openType.equals(1)) {
                        openType = 2;
                    }
                    if (openType.equals(2)) {
                        openType = 1;
                    }
                    MemberCardInfo byRankCodeCardInfo = this.getByRankCodeCardInfo(openType, rankCode);
                    if (BeanUtil.isEmpty(byRankCodeCardInfo)) {
                        throw new ServiceException("当前等级会员卡不存在，无法续费。可联系客服人员沟通续费情况");
                    }
                    memberCardInfoCenterVos.get(0).setOpenValue(byRankCodeCardInfo.getOpenValue());
                }
            }

            memberCenterVo.setMemberCardInfoCenterVos(memberCardInfoCenterVos);
        }
        return memberCenterVo;
    }

    /**
     * 获取封装会员卡相关信息
     *
     * @param memberInfo 会员信息
     * @return com.medusa.gruul.account.model.vo.MemberCardInfoCenterVo
     */
    private MemberCardInfoCenterVo getMemberCardInfoCenterVo(MemberInfo memberInfo) {
        MemberCardInfoCenterVo memberCardInfoCenterVo = new MemberCardInfoCenterVo();
        memberCardInfoCenterVo.setId(memberInfo.getId())
                .setMemberNumber(memberInfo.getMemberNumber())
                .setState(memberInfo.getState())
                .setMemberCardInfoId(memberInfo.getMemberCardInfoId())
                .setMemberRegisterTime(memberInfo.getMemberRegisterTime())
                .setMemberExpireTime(memberInfo.getMemberExpireTime());
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = memberCardInfoCenterVo.getMemberExpireTime().atZone(zoneId);
        Date date = Date.from(zdt.toInstant());
        Date date2 = new Date();
        long day = DateUtil.betweenDay(date2, date, false);
        if (day < 0) {
            day = 0;
        }
        memberCardInfoCenterVo.setRemainDate(String.valueOf(day));
        String shopId = ISecurity.userMust().getShopId();
        List<MemberCardInfo> memberCardInfoList = memberCardInfoMapper.getMemberCardInfoByShopId(shopId);
        memberCardInfoList = memberCardInfoList.stream().filter(item -> (item.getRankName() != null && !"".equals(item.getRankName()) && item.getIsOpen() == 1)).sorted(Comparator.comparing(MemberCardInfo::getRankCode).reversed()).toList();
        MemberCardInfo memberCardInfo = memberCardInfoMapper.getApiMemberCardInfo(memberInfo.getMemberCardInfoId());
        for (MemberCardInfo memberCardInfoTemp : memberCardInfoList) {
            if (memberCardInfoTemp.getRankCode().equals(memberCardInfo.getRankCode()) && memberCardInfoTemp.getOpenType().equals(memberCardInfo.getOpenType())) {
                memberCardInfoCenterVo.setTitle(memberCardInfoTemp.getTitle())
                        .setRankCode(memberCardInfoTemp.getRankCode())
                        .setRankName(memberCardInfoTemp.getRankName())
                        .setOpenType(memberCardInfoTemp.getOpenType())
                        .setOpenValue(memberCardInfoTemp.getOpenValue())
                        .setUseInDate(memberCardInfoTemp.getUseInDate())
                        .setBackGroundPicUrl(memberCardInfoTemp.getBackGroundPicUrl())
                        .setIsOpen(memberCardInfoTemp.getIsOpen());
                //获取店铺下特权开关为开启状态下的会员卡权益列表
                List<MemberPrivilegeVo> memberPrivilegeVos = memberCardPrivilegeMapper.selectMemberCardPrivilege(shopId);
                for (MemberPrivilegeVo memberPrivilegeVo : memberPrivilegeVos) {
                    MemberCardPrivilegeGroup memberCardPrivilegeGroup = memberCardPrivilegeGroupMapper.selectMemberCardPrivilegeGroup(shopId, memberCardInfoTemp.getId(), memberPrivilegeVo.getId());
                    if (memberCardPrivilegeGroup != null) {
                        memberPrivilegeVo.setIsSelected(1);
                        memberPrivilegeVo.setValue(memberCardPrivilegeGroup.getValue());
                    } else {
                        memberPrivilegeVo.setIsSelected(0);
                    }
                }
                memberCardInfoCenterVo.setMemberCardPrivileges(memberPrivilegeVos);
                break;
            }
        }

        String rankCode = memberCardInfoList.get(0).getRankCode();
        if (Integer.parseInt(memberCardInfoCenterVo.getRankCode()) >= Integer.parseInt(rankCode)) {
            memberCardInfoCenterVo.setMostRankCode(1);
        } else {
            memberCardInfoCenterVo.setMostRankCode(2);

        }
        return memberCardInfoCenterVo;
    }

    /**
     * 小程序端会员卡记录
     *
     * @return com.medusa.gruul.account.model.vo.MemberCardRecordVo
     */
    @Override
    public List<MemberCardRecordVo> getMemberCardRecord() {
        List<MemberCardRecordVo> memberCardRecordVos = new ArrayList<>();

        SecureUser secureUser = ISecurity.userMust();
        String userId = "";
        if (secureUser != null && secureUser.getId() != null) {
            userId = secureUser.getId();
            if (userId != null) {
                List<MemberInfo> memberInfos = memberInfoMapper.getMemberInfoByUserId(userId);
                for (MemberInfo memberInfo : memberInfos) {
                    MemberCardRecordVo memberCardRecordVo = new MemberCardRecordVo();
                    MemberCardInfo memberCardInfo = memberCardInfoMapper.getApiMemberCardInfo(memberInfo.getMemberCardInfoId());
                    memberCardRecordVo.setId(memberInfo.getId())
                            .setMemberCardInfoId(memberInfo.getMemberCardInfoId())
                            .setMemberExpireTime(memberInfo.getMemberExpireTime())
                            .setMemberNumber(memberInfo.getMemberNumber())
                            .setMemberRegisterTime(memberInfo.getMemberRegisterTime())
                            .setOpenType(memberCardInfo.getOpenType())
                            .setRankCode(memberCardInfo.getRankCode())
                            .setRankName(memberCardInfo.getRankName())
                            .setState(memberInfo.getState())
                            .setTitle(memberCardInfo.getTitle());
                    //付费和积分兑换
                    if (memberCardInfo.getOpenType().equals(CommonConstants.NUMBER_ONE) || memberCardInfo.getOpenType().equals(CommonConstants.NUMBER_TWO)) {
                        memberCardRecordVo.setOpenValue(memberCardInfo.getOpenValue());
                    }
                    memberCardRecordVos.add(memberCardRecordVo);
                }
            }
        }
        return memberCardRecordVos;
    }

    /**
     * 获取会员开关功能
     *
     * @return com.medusa.gruul.account.api.entity.MemberSwitch
     */
    @Override
    public List<MemberSwitch> getMemberCardInfoSwitch() {

        List<MemberSwitch> memberSwitchList = memberSwitchMapper.
                selectList(new QueryWrapper<>());
        return memberSwitchList;
    }

    /**
     * 获取指定表单列表
     *
     * @return 会员表单列表
     */
    @Override
    public List<MemberRegisterFormVo> getRegisterForm() {

        return memberRegisterFormMapper.selectMemberRegisterForm(ISecurity.userMust().getShopId());
    }

    /**
     * 获取指定店铺的指定开卡方式的会员卡 (小程序端)
     *
     * @param memberCardInfoListDto com.medusa.gruul.account.api.model.MemberCardInfoListDto
     * @return com.medusa.gruul.account.api.model.MemberCardInfoListResultDto
     */
    @Override
    public List<MemberCardInfoListResultDto> getMemberCardInfoList(MemberCardInfoListDto memberCardInfoListDto) {
        if (memberCardInfoListDto.getOpenType() == null) {
            throw new ServiceException("无效获取类型");
        }
        //会员卡开卡方式:0-自动发卡 1-付费购买 2-积分兑换
        List<MemberCardInfo> memberCardInfoList = memberCardInfoMapper.getMemberCardInfoList(memberCardInfoListDto);
        List<MemberCardInfoListResultDto> memberCardInfoListResultDtos = new ArrayList<>();
        for (MemberCardInfo memberCardInfo : memberCardInfoList) {
            MemberCardInfoListResultDto memberCardInfoListResultDto = new MemberCardInfoListResultDto();
            BeanUtils.copyProperties(memberCardInfo, memberCardInfoListResultDto);
            //如果是获取开发方式为购买指定商品,方式则需要获取商品id数组
            if (memberCardInfoListDto.getOpenType().equals(CommonConstants.NUMBER_FOUR)) {
                MemberCardGoodsVo memberCardGoodsVo = memberCardGoodsMapper.selectMemberCardGoods(memberCardInfo.getShopId(), memberCardInfo.getId());
                memberCardInfoListResultDto.setGoodsId(memberCardGoodsVo.getGoodsIds());
            }
            memberCardInfoListResultDtos.add(memberCardInfoListResultDto);
        }
        return memberCardInfoListResultDtos;
    }

    /**
     * 获取会员卡开启状态
     *
     * @param shopId 店铺id
     * @return java.lang.Integer
     */
    @Override
    public Integer memberSwitch(String shopId) {
        MemberSwitch memberSwitch = memberSwitchMapper.selectOne(new LambdaQueryWrapper<MemberSwitch>().eq(MemberSwitch::getShopId, shopId).eq(MemberSwitch::getMemberModel, 0));
        return memberSwitch == null ? 0 : memberSwitch.getState();
    }

    /**
     * 生成初始化sql
     *
     * @param shopId 店铺id
     */
    @Override
    public void memberInit(String shopId) {
        //需要初始化的sql
        ISystem.switchTenant(
                shopId,
                () -> {
                    MemberCardBaseInfo memberCardBaseInfo = new MemberCardBaseInfo();
                    memberCardBaseInfo.setUseInDate(365L);
                    memberCardBaseInfo.setTitle("plus会员");
                    memberCardBaseInfo.setCurrentOpenType(CommonConstants.NUMBER_ONE);
                    memberCardBaseInfoMapper.insert(memberCardBaseInfo);
                    //初始化t_member_switch
                    MemberSwitch memberSwitch = new MemberSwitch();
                    memberSwitch.setState(1)
                            .setMemberModel(0);
                    memberSwitchMapper.insert(memberSwitch);
                    memberSwitch = new MemberSwitch();
                    memberSwitch.setState(1)
                            .setMemberModel(1);
                    memberSwitchMapper.insert(memberSwitch);
                    initMemberCardPrivilege();
                    //付费购买
                    initMemberCardInfo(CommonConstants.NUMBER_ONE);
                    //积分兑换
                    initMemberCardInfo(CommonConstants.NUMBER_TWO);

                    // 初始化表单数据
                    MemberRegisterForm memberRegisterForm = new MemberRegisterForm();
                    memberRegisterForm.setFormId("1")
                            .setFormName("手机号")
                            .setFormPattern(3)
                            .setTips("请输入手机号")
                            .setIsRequired(1);
                    memberRegisterFormMapper.insert(memberRegisterForm);
                }
        );

    }

    /**
     * 获取当前使用的会员卡
     *
     * @param shopId 店铺id
     * @return com.medusa.gruul.account.api.entity.MemberCardInfo
     */
    @Override
    public List<MemberCardInfo> getCurrentMemberCardInfo(String shopId) {
        MemberCardBaseInfo baseInfo = getByMemberBaseInfo(shopId);
        return this.baseMapper.selectList(new QueryWrapper<MemberCardInfo>()
                .eq("open_type", baseInfo.getCurrentOpenType())
                .eq("is_open", CommonConstants.NUMBER_ONE)
                .orderByAsc("rank_code")
        );
    }

    /**
     * 初始化会员卡信息
     */
    private void initMemberCardInfo(Integer openType) {
        //获取商品权益
        MemberCardPrivilege memberCardPrivilege = memberCardPrivilegeMapper.selectOne(new QueryWrapper<MemberCardPrivilege>().eq("p_type", CommonConstants.NUMBER_ONE));
        MemberCardPrivilegeGroup memberCardPrivilegeGroup = new MemberCardPrivilegeGroup();
        memberCardPrivilegeGroup.setPrivilegeId(memberCardPrivilege.getId());
        memberCardPrivilegeGroup.setValue(new BigDecimal("90.00"));
        MemberCardInfo memberCardInfo = new MemberCardInfo()
                .setRankCode("1")
                .setIsOpen(1)
                .setOpenType(openType)
                .setBackGroundPicUrl("https://oss-tencent.bgniao.cn/api/silver_card.png")
                .setOpenValue(new BigDecimal(39))
                .setRankName("普通会员")
                .setTitle("Plus会员")
                .setUseInDate(365L);
        memberCardInfoMapper.insert(memberCardInfo);
        memberCardPrivilegeGroup.setMemberCardId(memberCardInfo.getId());
        memberCardPrivilegeGroupMapper.insert(memberCardPrivilegeGroup);

        memberCardInfo = new MemberCardInfo();
        memberCardInfo.setRankCode("2")
                .setIsOpen(1)
                .setBackGroundPicUrl("https://oss-tencent.bgniao.cn/api/general_card.png")
                .setOpenType(openType)
                .setOpenValue(new BigDecimal(199))
                .setRankName("黄金会员")
                .setTitle("Plus会员")
                .setUseInDate(365L);
        memberCardInfoMapper.insert(memberCardInfo);
        memberCardPrivilegeGroup = new MemberCardPrivilegeGroup();
        memberCardPrivilegeGroup.setPrivilegeId(memberCardPrivilege.getId());
        memberCardPrivilegeGroup.setValue(new BigDecimal("90.00"));
        memberCardPrivilegeGroup.setMemberCardId(memberCardInfo.getId());
        memberCardPrivilegeGroupMapper.insert(memberCardPrivilegeGroup);

        memberCardInfo = new MemberCardInfo();
        memberCardInfo.setRankCode("3")
                .setIsOpen(1)
                .setBackGroundPicUrl("https://oss-tencent.bgniao.cn/api/platinum_card.png")
                .setOpenType(openType)
                .setOpenValue(new BigDecimal(399))
                .setRankName("钻石会员")
                .setTitle("Plus会员")
                .setUseInDate(365L);
        memberCardInfoMapper.insert(memberCardInfo);
        MemberCardInfo memberCardInfoTemp = new MemberCardInfo();
        memberCardInfoTemp.setRankCode("4")
                .setIsOpen(0)
                .setOpenType(openType)
                .setTitle("Plus会员")
                .setUseInDate(365L);
        memberCardInfoMapper.insert(memberCardInfoTemp);

        memberCardInfoTemp = new MemberCardInfo();
        memberCardInfoTemp.setRankCode("5")
                .setIsOpen(0)
                .setOpenType(openType)
                .setTitle("Plus会员")
                .setUseInDate(365L);
        memberCardInfoMapper.insert(memberCardInfoTemp);
    }

    /**
     * 初始化会员卡权益
     */
    private void initMemberCardPrivilege() {
        MemberCardPrivilege memberCardPrivilege = new MemberCardPrivilege();
        memberCardPrivilege.setPrivilegeName("商品折扣")
                .setIllustrate("商品折扣")
                .setPType("1")
                .setAllowDel(0)
                .setOpen(1);
        memberCardPrivilegeMapper.insert(memberCardPrivilege);
        memberCardPrivilege = new MemberCardPrivilege();
        memberCardPrivilege.setPrivilegeName("积分加倍")
                .setIllustrate("购买商品获得的积分加倍")
                .setPType("3")
                .setAllowDel(0)
                .setOpen(1);
        memberCardPrivilegeMapper.insert(memberCardPrivilege);
        memberCardPrivilege = new MemberCardPrivilege();
        memberCardPrivilege.setPrivilegeName("物流优惠")
                .setIllustrate("物流配送方式订单享受包邮免配送费")
                .setPType("2")
                .setAllowDel(0)
                .setOpen(1);
        memberCardPrivilegeMapper.insert(memberCardPrivilege);
        memberCardPrivilege = new MemberCardPrivilege();
        memberCardPrivilege.setPrivilegeName("优先发货")
                .setIllustrate("订单优先发货")
                .setPType("5")
                .setAllowDel(0)
                .setOpen(1);
        memberCardPrivilegeMapper.insert(memberCardPrivilege);
        memberCardPrivilege = new MemberCardPrivilege();
        memberCardPrivilege.setPrivilegeName("急速售后")
                .setIllustrate("申请维权时，系统自动同意维权申请，无需等待人工审核")
                .setPType("6")
                .setAllowDel(0)
                .setOpen(1);
        memberCardPrivilegeMapper.insert(memberCardPrivilege);
        memberCardPrivilege = new MemberCardPrivilege();
        memberCardPrivilege.setPrivilegeName("专属客服")
                .setIllustrate("享受专属客服1对1接待")
                .setPType("4")
                .setAllowDel(0)
                .setOpen(1);
        memberCardPrivilegeMapper.insert(memberCardPrivilege);
    }
}
