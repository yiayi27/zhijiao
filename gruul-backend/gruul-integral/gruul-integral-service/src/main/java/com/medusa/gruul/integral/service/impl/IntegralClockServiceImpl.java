package com.medusa.gruul.integral.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.model.IntegraChangelDto;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.core.util.SystemCode;



import com.medusa.gruul.integral.api.constant.IntegralConstant;
import com.medusa.gruul.integral.api.entity.*;
import com.medusa.gruul.integral.mapper.*;
import com.medusa.gruul.integral.mq.Sender;
import com.medusa.gruul.integral.service.IIntegralClockService;
import com.medusa.gruul.integral.web.enums.IntegralGainTypeEnum;
import com.medusa.gruul.integral.web.enums.IntegralOperationTypeEnum;
import com.medusa.gruul.integral.web.enums.IntegralRuleGainTypeEnum;
import com.medusa.gruul.integral.web.enums.IntegralRuleParametersEnum;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralClockDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 积分签到表 服务实现类
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Service
public class IntegralClockServiceImpl extends ServiceImpl<IntegralClockMapper, IntegralClock>
        implements IIntegralClockService {

    @Autowired
    private IntegralClockMapper integralClockMapper;

    @Autowired
    private IntegralRuleMapper integralRuleMapper;

    @Autowired
    private IntegralRuleParameterMapper integralRuleParameterMapper;

    @Autowired
    private IntegralShopMapper integralShopMapper;

    @Autowired
    private IntegralDetailMapper integralDetailMapper;

    @Autowired
    private Sender sender;

    @Autowired
    private IntegralDetailServiceImpl integralDetailService;

    /**
     * 提供给前端 商家后台设置的签到天数 用于展示签到天数
     *
     * @param shopId 商铺id
     * @return HashMap<Integer, String>
     */
    @Override
    public HashMap<Integer, String> getSingInIntegralValue(String shopId) {
        //1.根据shopId  获取商城id
        Long integralShopId = integralShopMapper.selectByShopId(shopId);
        if (integralShopId == null) {
            throw new ServiceException("商铺信息不存在");
        }
        int status = IntegralRuleGainTypeEnum.SING_IN.getStatus();
        //2.根据商城id 签到值 获取 积分规则id
        Long integralRuleId = integralRuleMapper.getIntegralRule(integralShopId, status);
        if (integralRuleId == null) {
            throw new ServiceException("商铺签到信息不存在");
        }
        //获取商家设置的最大连续天数
        IntegralRuleParameter ruleParameterDayss = getRuleParameter(integralRuleId, IntegralRuleParametersEnum.DAYSS.getStatus());
        //获取商家设置的days连续天数
        IntegralRuleParameter ruleParameterDays = getRuleParameter(integralRuleId, IntegralRuleParametersEnum.DAYS.getStatus());
        //获取商家设置的days连续天数
        IntegralRuleParameter ruleParameter = getRuleParameter(integralRuleId, IntegralRuleParametersEnum.DAY_SING_IN.getStatus());
        // 3. 构建签到天数对应的积分值
        HashMap<Integer, String> map = new HashMap<>(ruleParameterDayss.getValue().intValue());
        for (int i = 1; i <= ruleParameterDayss.getValue().intValue(); i++) {
            map.put(i, ruleParameter.getValue().toString());
            if (i == ruleParameterDays.getValue().intValue()) {
                IntegralRuleParameter firstSingInCondition = integralRuleParameterMapper
                        .getSingInIntegral(integralRuleId, IntegralRuleParametersEnum.DAYS_SING_IN.getStatus());
                map.put(i, firstSingInCondition.getValue().add(ruleParameter.getValue()).toString());
            }
            if (i == ruleParameterDayss.getValue().intValue()) {
                IntegralRuleParameter firstSingInCondition = integralRuleParameterMapper
                        .getSingInIntegral(integralRuleId, IntegralRuleParametersEnum.DAYSS_SING_IN.getStatus());
                map.put(i, firstSingInCondition.getValue().add(ruleParameter.getValue()).toString());
            }
        }
        return map;
    }

    /**
     * 获取积分规则参数值
     *
     * @param integralRuleId  积分规则ID
     * @param parameterStatus 参数状态
     * @return IntegralRuleParameter
     */
    private IntegralRuleParameter getRuleParameter(Long integralRuleId, String parameterStatus) {
        IntegralRuleParameter ruleParameter = integralRuleParameterMapper
                .getRuleParameterMapper(integralRuleId, parameterStatus);
        if (ruleParameter == null) {
            throw new ServiceException("积分规则参数不存在", SystemCode.DATA_NOT_EXIST.getCode());
        }
        return ruleParameter;
    }

    /**
     * 获取用户签到信息
     *
     * @param userId 用户id
     * @return IntegralClockDto
     */
    @Override
    public IntegralClockDto userSingInInfo(String userId) {
        IntegralClockDto userSingInInfo = integralClockMapper.getUserSingInInfo(userId);
        if (userSingInInfo == null) {
            this.addSingInDefault();
            userSingInInfo = integralClockMapper.getUserSingInInfo(userId);
        }
        if (BeanUtil.isEmpty(userSingInInfo)) {
            throw new ServiceException("用户积分签到信息不存在", SystemCode.DATA_NOT_EXIST.getCode());
        }
        return userSingInInfo;
    }

    /**
     * 用户进行签到 生成积分明细
     *
     * @param userId 用户id
     * @return 用户签到获得的积分
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer userSingIn(String userId) {
        String shopId = ISecurity.userMust().getShopId();
        IntegralShop integralShop = integralShopMapper.selectByShopInfo(shopId);
        if (!integralShop.getType()) {
            throw new ServiceException("积分商城未开启");
        }
        IntegralRule ruleStatus = integralRuleMapper
                .getRuleStatus(integralShop.getId(), IntegralRuleGainTypeEnum.SING_IN.getStatus());
        if (!ruleStatus.getOpen()) {
            throw new ServiceException("签到功能未开启");
        }
        //获取用户签到信息 查看用户当日是否进行了签到
        IntegralClock integralClock = integralClockMapper.selectByUserId(userId);

        if (integralClock !=null && integralClock.getSignInState()) {
            throw new ServiceException("该用户当日已进行签到");
        }
        //1.用户签到 改变用户用户签到状态 及签到天数
        Long runningDays = integralClock.getRunningDays();
        integralClock.setRunningDays(++runningDays);
        integralClock.setSignInState(true);
        integralClockMapper.updateById(integralClock);
        //2.生成积分明细表 用户添加积分
        BigDecimal bigDecimal = saveIntegralDetail(integralClock);
        return bigDecimal.intValue();
    }

    /**
     * 生成签到明细记录
     *
     * @param integralClock 用户签到信息
     */
    private BigDecimal saveIntegralDetail(IntegralClock integralClock) {
        String shopId = ISecurity.userMust().getShopId();
        BigDecimal integral;
        
        IntegralDetail integralDetail = new IntegralDetail();
        //获得商城信息
        IntegralShop integralShop = integralShopMapper.selectByShopInfo(shopId);
        Long integralRuleId = integralRuleMapper
                .getIntegralRule(integralShop.getId(), IntegralRuleGainTypeEnum.SING_IN.getStatus());
        //根据规则id 获取规则参数信息
        List<IntegralRuleParameter> integralList = integralRuleParameterMapper.getIntegralList(integralRuleId);
        if (CollectionUtil.isNotEmpty(integralList)) {
            //获取每日签到信息
            IntegralRuleParameter singInIntegral = integralRuleParameterMapper
                    .getSingInIntegral(integralRuleId, IntegralRuleParametersEnum.DAY_SING_IN.getStatus());
            if (singInIntegral == null) {
                throw new ServiceException("积分签到规则参数信息不存在", SystemCode.DATA_NOT_EXIST.getCode());
            }
            BigDecimal integralMultiples = integralDetailService
                    .memberCardIntegralPrivilegeInfo(integralClock.getUserId());
            if (integralMultiples.compareTo(BigDecimal.ONE) == CommonConstants.NUMBER_ONE) {
                //拥有会员权益 积分加倍时 新增字段 会员权益
                integralDetail
                        .setDetailName(integralMultiples.doubleValue() + IntegralConstant.INTEGRAL_PRIVILEGE_NAME);
            }
            integralDetail.setIntegral(singInIntegral.getValue().multiply(integralMultiples));
            for (IntegralRuleParameter integralRuleParameter : integralList) {
                if ((integralRuleParameter.getValue().intValue() + "").equals((integralClock.getRunningDays() - 1) + "")
                        & (integralRuleParameter.getKey().equals(IntegralRuleParametersEnum.DAYS.getStatus())
                        || integralRuleParameter.getKey().equals(IntegralRuleParametersEnum.DAYSS.getStatus()))) {
                    //得到key进行天数比较
                    String key = integralRuleParameter.getKey();
                    if (key.equals(IntegralRuleParametersEnum.DAYS.getStatus())) {
                        //key 等于第一个条件
                        IntegralRuleParameter firstSingInCondition = integralRuleParameterMapper
                                .getSingInIntegral(integralRuleId, IntegralRuleParametersEnum.DAYS_SING_IN.getStatus());
                        //计算本次获得的积分值
                        integralDetail.setIntegral((singInIntegral.getValue().add(firstSingInCondition.getValue())
                                .multiply(integralMultiples)));
                    } else if (key.equals(IntegralRuleParametersEnum.DAYSS.getStatus())) {
                        // key 等于第二个条件
                        IntegralRuleParameter secondSingInCondition = integralRuleParameterMapper
                                .getSingInIntegral(integralRuleId,
                                        IntegralRuleParametersEnum.DAYSS_SING_IN.getStatus());
                        integralDetail.setIntegral((singInIntegral.getValue().add(secondSingInCondition.getValue()))
                                .multiply(integralMultiples));
                    }
                }
            }
            integralDetail.setUserId(integralClock.getUserId());
            integralDetail.setGainIntegralType(IntegralGainTypeEnum.SIGN_IN.getStatus());
            //计算用户现在拥有的积分值(未加当前获取的积分)
            BigDecimal bigDecimal = integralDetailMapper.selectNowIntegral(shopId, integralClock.getUserId());
            bigDecimal = bigDecimal == null ? BigDecimal.ZERO : bigDecimal;
            integral = integralDetail.getIntegral();
            //用户目前积分(现有积分+本次获得的积分)
            integralDetail.setNowTheIntegral(bigDecimal.add(integral));
            int insert = integralDetailMapper.insert(integralDetail);
            if (insert < 1) {
                throw new ServiceException("积分明细生成失败", SystemCode.DATA_ADD_FAILED.getCode());
            }
            //给用户发送mq 增加积分
            IntegraChangelDto integraChangelDto = new IntegraChangelDto();
            integraChangelDto.setOption(IntegralOperationTypeEnum.INCREASE.getStatus());
            integraChangelDto.setUserId(integralClock.getUserId());
            integraChangelDto.setValue(integral);
            integraChangelDto.setShopId(shopId);
            sender.sendIntegralDeductionMessage(integraChangelDto);
        } else {
            throw new ServiceException("规则参数信息不存在", SystemCode.DATA_NOT_EXIST.getCode());
        }
        return integral;
    }

    /**
     * 生成用户签到信息默认值
     */
    @Override
    public void addSingInDefault() {
        SecureUser feignCurUser = ISecurity.userMust();
        String userId = feignCurUser.getId();
        if (StrUtil.isEmpty(userId)) {
            throw new ServiceException("数据错误", SystemCode.DATA_NOT_EXIST.getCode());
        }
        IntegralClock clockInfo = integralClockMapper.findUserId(userId);
        if (clockInfo == null) {
            clockInfo = new IntegralClock();
            clockInfo.setUserId(userId);
            clockInfo.setSignInState(false);
            clockInfo.setRunningDays(CommonConstants.LONG_ONE);
            int insert = integralClockMapper.insert(clockInfo);
            if (insert < 1) {
                throw new ServiceException("用户签到默认值生成失败", SystemCode.DATA_ADD_FAILED.getCode());
            }
        }
    }

    /**
     * 获取所有当日签到信息为false的用户
     *
     * @return
     */
    @Override
    public List<IntegralClock> selectStatusAll() {
        return integralClockMapper.getDidnotSignIn();
    }

    /**
     * 查询所有用户
     *
     * @return
     */
    @Override
    public List<IntegralClock> selectAll() {
        return integralClockMapper.getAllUserSingInInfo();
    }

    /**
     * 修改为签到的用户 连续签到天数为1L
     *
     * @param integralClock
     */
    @Override
    public void updateRunningDays(IntegralClock integralClock) {
        this.baseMapper.updateRunningDays(integralClock);
    }

    /**
     * 定时任务 修改签到状态为未签到
     *
     * @param integralClock
     */
    @Override
    public void updateSignIn(IntegralClock integralClock) {
        this.baseMapper.updateSignIn(integralClock);
    }

    /**
     * 获取该商铺下所有的用户签到信息
     *
     * @param shopId
     */
    @Override
    public List<IntegralClock> selectShopAll(String shopId) {
        return integralClockMapper.selectShopUserInfo(shopId);
    }
}

