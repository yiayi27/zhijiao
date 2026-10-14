package com.medusa.gruul.integral.service.impl;

import cn.binarywang.wx.miniapp.util.crypt.WxMaCryptUtils;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.medusa.gruul.integral.service.IStepNumberService;
import com.medusa.gruul.integral.web.enums.IntegralGainTypeEnum;
import com.medusa.gruul.integral.web.enums.IntegralOperationTypeEnum;
import com.medusa.gruul.integral.web.enums.IntegralRuleGainTypeEnum;
import com.medusa.gruul.integral.web.enums.IntegralRuleParametersEnum;
import com.medusa.gruul.integral.web.model.dto.manager.DecryptRunData;
import com.medusa.gruul.integral.web.model.dto.manager.UserStepNumberDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 步数表 服务实现类
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Service
public class StepNumberServiceImpl extends ServiceImpl<StepNumberMapper, StepNumber> implements IStepNumberService {

    @Autowired
    private StepNumberMapper stepNumberMapper;

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
     * 解密获取微信步数 需要注意 用户微信步数拿到后 用户已兑换积分部分进行扣减的操作
     *
     * @param decryptRunData 解密
     * @return 用户实时步数
     */
    @Override
    public String getUserStepNumber(DecryptRunData decryptRunData) {
        //获取用户扩展数据信息
        SecureUser feignCurUser = ISecurity.userMust();
        String shopId = ISecurity.userMust().getShopId();
        String userId = feignCurUser.getId();
        if (StrUtil.isEmpty(userId)) {
            throw new ServiceException("数据错误", SystemCode.DATA_NOT_EXIST.getCode());
        }
        StepNumber stepNumber = stepNumberMapper.seleteByUserId(userId);
        if (null == stepNumber) {
            stepNumber = new StepNumber();
            //创建用户步数数据
            Long id = integralShopMapper.selectByShopId(shopId);
            Long integralRuleId = integralRuleMapper
                    .getIntegralRule(id, IntegralRuleGainTypeEnum.STEP_NUMBER.getStatus());
            stepNumber.setHasChange(CommonConstants.NUMBER_ZERO);
            stepNumber.setIntegralRuleId(integralRuleId);
            stepNumber.setUserId(userId);
            stepNumberMapper.insert(stepNumber);
        }

        //进行解密得到数据
        String json = WxMaCryptUtils
                .decrypt(decryptRunData.getSessionKey(), decryptRunData.getEncryptedData(), decryptRunData.getIvStr());
        JSONObject jsonObject = JSONUtil.parseObj(json);
        //微信运动key stepInfoList
        String stepInfoList = jsonObject.getStr("stepInfoList");
        //得到当天的微信运动数据
        String movementData = stepInfoList.substring(stepInfoList.lastIndexOf("{"));
        String[] split = movementData.split(",");
        String theCurrentStep = split[CommonConstants.NUMBER_ZERO];
        String[] split1 = theCurrentStep.split(":");
        theCurrentStep = split1[CommonConstants.NUMBER_ONE];
        if (theCurrentStep == null) {
            throw new ServiceException("步数获取失败");
        }
        int stopNumbers = Integer.parseInt(theCurrentStep);
        //获取用户剩余步数
        stopNumbers = stopNumbers - stepNumber.getHasChange();
        return Integer.toString(stopNumbers);
    }


    /**
     * 用户进行步数兑换
     *
     * @param todayStep  用户当前步数
     * @param targetStep 商家设置的目标步数
     */
    @Override
    public String userStepNumberConversion(Integer todayStep, Integer targetStep) {
        //获取用户当前信息
        SecureUser feignCurUser = ISecurity.userMust();
        String shopId = ISecurity.userMust().getShopId();
        String userId = feignCurUser.getId();
        
        if (StrUtil.isEmpty(userId)) {
            throw new ServiceException("数据错误", SystemCode.DATA_NOT_EXIST.getCode());
        }
        IntegralShop integralShop = integralShopMapper.selectByShopInfo(shopId);
        if (integralShop == null) {
            throw new ServiceException("商城信息不存在");
        }
        IntegralRule ruleStatus = integralRuleMapper
                .getRuleStatus(integralShop.getId(), IntegralRuleGainTypeEnum.STEP_NUMBER.getStatus());
        if (ruleStatus == null) {
            throw new ServiceException("商城规则信息不存在");
        } else {
            Boolean open = ruleStatus.getOpen();
            if (!open) {
                throw new ServiceException("步数兑换未开启");
            }
        }

        IntegralRuleParameter ruleParameter = integralRuleParameterMapper
                .getStepConversionIntegral(ruleStatus.getIntegralRuleId(),
                        IntegralRuleParametersEnum.STEP_NUMBER_INTEGRAL.getStatus());
        if (ruleParameter == null) {
            throw new ServiceException("目标步数信息不存在");
        }
        BigDecimal value = ruleParameter.getValue();
        //进行整除 获得百分比
        int number = todayStep / targetStep;
        StepNumber stepNumber = this.baseMapper.selectOne(new QueryWrapper<StepNumber>().eq("user_id", userId));
        if (stepNumber == null) {
            throw new ServiceException("用户有误，无法进行兑换", SystemCode.DATA_NOT_EXIST.getCode());
        }
        Integer hasChange = number * targetStep;
        stepNumber.setHasChange(hasChange + stepNumber.getHasChange());
        this.updateById(stepNumber);
        BigDecimal numbers = new BigDecimal(number);
        //获取用户会员信息
        BigDecimal integralMultiples = integralDetailService.memberCardIntegralPrivilegeInfo(userId);
        //计算本次兑换可获取积分值
        BigDecimal integral = (value.multiply(numbers)).multiply(integralMultiples);
        //生成明细
        IntegralDetail integralDetail = new IntegralDetail();
        if (integralMultiples.compareTo(BigDecimal.ONE) == 1) {
            //拥有会员权益 积分加倍时 新增字段 会员权益
            integralDetail.setDetailName(integralMultiples.doubleValue() + IntegralConstant.INTEGRAL_PRIVILEGE_NAME);
        }
        integralDetail.setUserId(userId);
        integralDetail.setGainIntegralType(IntegralGainTypeEnum.DAY_STEP_NUMBER_REACH.getStatus());
        integralDetail.setIntegral(integral);
        BigDecimal bigDecimal = integralDetailMapper.selectNowIntegral(shopId, userId);
        bigDecimal = bigDecimal == null ? BigDecimal.ZERO : bigDecimal;
        integral = integralDetail.getIntegral();
        //用户目前积分(现有积分+本次获得的积分)
        integralDetail.setNowTheIntegral(bigDecimal.add(integral));
        int insert = integralDetailMapper.insert(integralDetail);
        if (insert < 1) {
            throw new ServiceException("积分明细生成失败", SystemCode.DATA_ADD_FAILED.getCode());
        }
        //mq通知用户

        IntegraChangelDto integraChangelDto = new IntegraChangelDto();
        integraChangelDto.setValue(integral);
        integraChangelDto.setUserId(userId);
        integraChangelDto.setOption(IntegralOperationTypeEnum.INCREASE.getStatus());
        integraChangelDto.setShopId(feignCurUser.getShopId());
        sender.sendIntegralDeductionMessage(integraChangelDto);
        number = todayStep % targetStep;
        return String.valueOf(number);
    }


    /**
     * 用户步数兑换相关数据信息获取
     *
     * @param userId         用户id
     * @param theCurrentStep 当前步数
     */
    @Override
    public UserStepNumberDto getUserStepNumberInfo(String userId, Integer theCurrentStep) {
        String shopId = ISecurity.userMust().getShopId();
        Long id = integralShopMapper.selectByShopId(shopId);
        if (id == null) {
            throw new ServiceException("商城信息不存在");
        }

        Long integralRuleId = integralRuleMapper.getIntegralRule(id, IntegralRuleGainTypeEnum.STEP_NUMBER.getStatus());
        if (integralRuleId == null) {
            throw new ServiceException("商城步数规则信息不存在");
        }

        IntegralRuleParameter valueParameter = integralRuleParameterMapper
                .getStepConversionIntegral(integralRuleId, IntegralRuleParametersEnum.DAY_STEP_NUBER.getStatus());
        IntegralRuleParameter keyParameter = integralRuleParameterMapper
                .getStepConversionIntegral(integralRuleId, IntegralRuleParametersEnum.STEP_NUMBER_INTEGRAL.getStatus());
        UserStepNumberDto userStepNumberDto = new UserStepNumberDto();
        //获取目标步数
        Integer targetStepNumber = valueParameter.getValue().intValue();
        //达标步数可获得积分
        BigDecimal value = keyParameter.getValue();
        userStepNumberDto.setIntegral(value);
        userStepNumberDto.setTargetStep(targetStepNumber);
        userStepNumberDto.setTodayStep(theCurrentStep);
        userStepNumberDto.setCompletionDegree("100");
        if (theCurrentStep < targetStepNumber) {
            String completeness = (int) ((float) theCurrentStep / targetStepNumber * 100) + "";
            userStepNumberDto.setCompletionDegree(completeness);
        }
        return userStepNumberDto;
    }


    /**
     * 获取所有已兑换步数大于0的数据
     *
     * @return List<StepNumber>
     */
    @Override
    public List<StepNumber> selectByHasChange() {
        return stepNumberMapper.selectByHasChange();
    }

    /**
     * 定时任务 修改用户兑换步数为0
     *
     * @param sn
     */
    @Override
    public void updateStepNumber(StepNumber sn) {
        this.baseMapper.updateStepNumber(sn);
    }

}
