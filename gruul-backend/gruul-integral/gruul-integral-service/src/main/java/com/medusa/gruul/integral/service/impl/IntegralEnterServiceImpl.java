package com.medusa.gruul.integral.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.account.api.model.IntegraChangelDto;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.integral.api.constant.IntegralConstant;
import com.medusa.gruul.integral.api.entity.*;
import com.medusa.gruul.integral.mapper.*;
import com.medusa.gruul.integral.mq.Sender;
import com.medusa.gruul.integral.service.IIntegralEnterService;
import com.medusa.gruul.integral.web.enums.IntegralGainTypeEnum;
import com.medusa.gruul.integral.web.enums.IntegralOperationTypeEnum;
import com.medusa.gruul.integral.web.enums.IntegralRuleGainTypeEnum;
import com.medusa.gruul.integral.web.enums.IntegralRuleParametersEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: xiaoq
 * @Date: 2023/12/31 10:17
 */
@Service
public class IntegralEnterServiceImpl extends ServiceImpl<IntegralEnterMapper, IntegralLogIn>
        implements IIntegralEnterService {

    @Autowired
    private IntegralEnterMapper integralEnterMapper;

    @Autowired
    private IntegralShopMapper integralShopMapper;

    @Autowired
    private IntegralRuleMapper integralRuleMapper;

    @Autowired
    private IntegralRuleParameterMapper integralRuleParameterMapper;

    @Autowired
    private IntegralDetailMapper integralDetailMapper;

    @Autowired
    private Sender sender;

    @Autowired
    private IntegralDetailServiceImpl integralDetailService;

    /**
     * 用户登入获取积分
     *
     * @param userId 用户id
     * @return 获取的积分
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal userLogin(String userId) {
        String shopId = ISecurity.userMust().getShopId();
        
        IntegralShop integralShop = integralShopMapper.selectByShopInfo(shopId);
        BigDecimal integral = null;
        IntegralDetail integralDetail = new IntegralDetail();
        //积分商城是否开启
        if (!integralShop.getType()) {
            throw new ServiceException("规则参数信息不存在", SystemCode.DATA_NOT_EXIST.getCode());

        }
        //2.根据积分商城表id 得到积分规则表类型为登入的 数据
        IntegralRule integralRule = integralRuleMapper
                .getRuleStatus(integralShop.getId(), IntegralRuleGainTypeEnum.LONG_IN.getStatus());
        //根据规则id 获取规则参数信息
        List<IntegralRuleParameter> integralList = integralRuleParameterMapper
                .getIntegralList(integralRule.getIntegralRuleId());
        if (CollectionUtil.isNotEmpty(integralList)) {
            //获取每日登入信息
            IntegralRuleParameter enterIntegral = integralRuleParameterMapper
                    .getSingInIntegral(integralRule.getIntegralRuleId(),
                            IntegralRuleParametersEnum.DAY_LONGIN.getStatus());
            //获取用户会员信息
            BigDecimal integralMultiples = integralDetailService.memberCardIntegralPrivilegeInfo(userId);
            if (integralMultiples.compareTo(BigDecimal.ONE) > 0) {
                //拥有会员权益 积分加倍时 新增字段 会员权益
                integralDetail
                        .setDetailName(integralMultiples.doubleValue() + IntegralConstant.INTEGRAL_PRIVILEGE_NAME);
            }
            integralDetail.setIntegral(enterIntegral.getValue().multiply(integralMultiples));

            IntegralLogIn integralLogIn = null;
            //查看商家是否开启登入功能
            if (integralRule.getOpen()) {

                integralLogIn = integralEnterMapper.selectByUserId(userId);
                if (integralLogIn == null) {
                    integralLogIn = new IntegralLogIn();
                    integralLogIn.setLogInState(false);
                    integralLogIn.setRunningDays(CommonConstants.LONG_ONE);
                    integralLogIn.setUserId(userId);
                    integralEnterMapper.insert(integralLogIn);
                }
            }
            assert integralLogIn != null;
            if (!integralLogIn.getLogInState()) {
                //修改用户状态
                Long runningDays = integralLogIn.getRunningDays();
                integralLogIn.setRunningDays(++runningDays);
                integralLogIn.setLogInState(true);
                integralEnterMapper.updateById(integralLogIn);
                List<String> statusList = new ArrayList<>(CommonConstants.NUMBER_TWO);
                statusList.add(IntegralRuleParametersEnum.DAYS.getStatus());
                statusList.add(IntegralRuleParametersEnum.DAYSS.getStatus());
                for (IntegralRuleParameter integralRuleParameter : integralList) {
                    if (integralRuleParameter.getValue().intValue() == (integralLogIn.getRunningDays() - 1)
                            && statusList.contains(integralRuleParameter.getKey())) {
                        //得到key进行天数比较
                        String key = integralRuleParameter.getKey();
                        String status = key.equals(IntegralRuleParametersEnum.DAYS.getStatus()) ? IntegralRuleParametersEnum.DAYS_LONGIN
                                .getStatus() : IntegralRuleParametersEnum.DAYSS_LONGIN.getStatus();
                        IntegralRuleParameter singInCondition = integralRuleParameterMapper.getSingInIntegral(integralRule.getIntegralRuleId(), status);
                        //计算本次获得的积分值
                        integralDetail.setIntegral((enterIntegral.getValue().add(singInCondition.getValue()).multiply(integralMultiples)));
                    }
                }
                integralDetail.setUserId(userId);
                integralDetail.setGainIntegralType(IntegralGainTypeEnum.DAY_LOGIN.getStatus());
                //计算用户现在拥有的积分值(未加当前获取的积分)
                BigDecimal bigDecimal = integralDetailMapper.selectNowIntegral(shopId, integralLogIn.getUserId());
                bigDecimal = bigDecimal == null ? BigDecimal.ZERO : bigDecimal;
                integral = integralDetail.getIntegral();
                integralDetail.setNowTheIntegral(bigDecimal.add(integral));
                integralDetailMapper.insert(integralDetail);
                //给用户发送mq 增加积分
                IntegraChangelDto integraChangelDto = new IntegraChangelDto();
                integraChangelDto.setOption(IntegralOperationTypeEnum.INCREASE.getStatus());
                integraChangelDto.setUserId(integralLogIn.getUserId());
                integraChangelDto.setValue(integral);
                integraChangelDto.setShopId(shopId);
                sender.sendIntegralDeductionMessage(integraChangelDto);
            }
        }
        return integral;
    }

    /**
     * 获取所有登入状态为false的数据
     *
     * @return
     */
    @Override
    public List<IntegralLogIn> selectStatusAll() {
        return integralEnterMapper.getDidnotEnter();
    }

    @Override
    public List<IntegralLogIn> selectAll() {
        return integralEnterMapper.getAllInfo();
    }

    /**
     * 定时任务 修改状态为未登入的用户连续签到前天数1L
     *
     * @param integralLogIn
     */
    @Override
    public void updateEnterDays(IntegralLogIn integralLogIn) {
        this.baseMapper.updateEnterDays(integralLogIn);

    }

    /**
     * 定时任务 修改登入状态为未登入
     *
     * @param integralLogIn
     */
    @Override
    public void updateEnterStatus(IntegralLogIn integralLogIn) {
        this.baseMapper.updateEnterStatus(integralLogIn);
    }

    /**
     * @param shopId
     * @return
     */
    @Override
    public List<IntegralLogIn> selectShopAll(String shopId) {
        return integralEnterMapper.selectShopUserInfo(shopId);
    }
}
