package com.medusa.gruul.integral.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.integral.api.entity.IntegralRule;
import com.medusa.gruul.integral.api.entity.IntegralRuleParameter;
import com.medusa.gruul.integral.api.entity.IntegralShop;
import com.medusa.gruul.integral.mapper.IntegralRuleMapper;
import com.medusa.gruul.integral.mapper.IntegralRuleParameterMapper;
import com.medusa.gruul.integral.mapper.IntegralShopMapper;
import com.medusa.gruul.integral.service.IIntegralRuleService;
import com.medusa.gruul.integral.web.enums.IntegralRuleGainTypeEnum;
import com.medusa.gruul.integral.web.enums.IntegralRuleParametersEnum;
import com.medusa.gruul.integral.web.model.dto.manager.ApiIntegralRuleDto;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralRuleDto;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralRuleParameterDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 积分规则表 服务实现类
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Service
@Slf4j
public class IntegralRuleServiceImpl extends ServiceImpl<IntegralRuleMapper, IntegralRule> implements IIntegralRuleService {

    @Autowired
    private IntegralRuleMapper integralRuleMapper;

    @Autowired
    private IntegralRuleParameterMapper integralRuleParameterMapper;

    @Autowired
    private IntegralShopMapper integralShopMapper;


    /**
     * 根据积分商城id 获取积分使用规则信息 在获取到积分参数信息
     *
     * @param shopId 积分商城id
     */
    @Override
    public List<IntegralRuleDto> getIntegralShopRule(String shopId) {
        //根据积分商城shopId 获取到 店铺id
        Long integralShopId = integralShopMapper.selectByShopId(shopId);
        List<IntegralRuleDto> shopRuleInfo = integralRuleMapper.getShopRuleInfo(integralShopId);
        if (CollectionUtil.isEmpty(shopRuleInfo)) {
            return List.of();
        }
        return shopRuleInfo;
    }


    /**
     * 修改积分规则 积分使用规则做修改 积分获取规则做删除新增操作
     *
     * @param integralRuleDto 积分规则dto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateIntegralRule(List<IntegralRuleDto> integralRuleDto) {
        for (IntegralRuleDto ruleDto : integralRuleDto) {
            IntegralRule integralRule = ruleDto.coverIntegralRule();
            int update = integralRuleMapper.updateById(integralRule);
            if (update == 0) {
                throw new ServiceException("修改积分规则失败 !", SystemCode.DATA_UPDATE_FAILED.getCode());
            }
            List<IntegralRuleParameterDto> integralRuleParameterDtos = ruleDto.getIntegralRuleParameterDtos();
            if (CollectionUtil.isNotEmpty(integralRuleParameterDtos)) {
                //因数据量大 该处进行物理删除
                Integer deleteRuleParameter = integralRuleParameterMapper.physicsDelete(ruleDto.getIntegralRuleId());
                if (deleteRuleParameter == 0) {
                    throw new ServiceException("积分规则参数删除失败 !", SystemCode.DATA_DELETE_FAILED.getCode());
                }
                //新增积分商品属性信息
                addRuleParameterList(integralRuleParameterDtos, ruleDto.getIntegralRuleId());
            }
        }
    }


    /**
     * 默认积分规则新增
     *
     * @param integralRuleDto 积分规则信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveIntegralRule(List<IntegralRuleDto> integralRuleDto) {
        String shoId = ISecurity.userMust().getShopId();
        Long id = integralShopMapper.selectByShopId(shoId);
        if (id == null) {
            throw new ServiceException("积分商城信息不存在 !", SystemCode.DATA_NOT_EXIST.getCode());
        }
        for (IntegralRuleDto ruleDto : integralRuleDto) {
            IntegralRule integralRule = ruleDto.coverIntegralRule();
            integralRule.setIntegralShopId(id);
            int insert = integralRuleMapper.insert(integralRule);
            if (insert == 0) {
                throw new ServiceException("积分规则新增失败 !", SystemCode.DATA_ADD_FAILED.getCode());
            }
            IntegralRule rule = integralRuleMapper.selectRule(ruleDto.getRuleType());
            List<IntegralRuleParameterDto> integralRuleParameterDtos = ruleDto.getIntegralRuleParameterDtos();
            if (CollectionUtil.isNotEmpty(integralRuleParameterDtos)) {
                //积分规则参数新增
                addRuleParameterList(integralRuleParameterDtos, rule.getIntegralRuleId());
            }
        }
    }

    /**
     * 生成积分规则默认值
     *
     * @param id 商铺id
     * @return Boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveIntegralDefaultRule(Long id, String shopId) {
        int ruleDefaultNum = 4;
        IntegralRule integralRule = new IntegralRule();
        for (int i = 0; i < ruleDefaultNum; i++) {
            integralRule.setIntegralShopId(id);
            integralRule.setIntegralValidity(12);
            integralRule.setUseRuleIntroduce(
                    "（1）积分使用过程中不找零、不兑现、不开发票，不可转移至其他账户。\n" + "\n" + "（2）使用积分进行兑换，兑换申请一经提交, 一律不能退货\n" + "\n"
                            + "（3）如因积分商品缺货等原因导致的退货退款，积分会原路返还\n" + "\n" + "（4）兑换礼品涉及运费和配送费由用户自行承担。\n" + "\n"
                            + "（5）笔歌拼团保留最终解释权。");
            integralRule.setIntegralRuleInfo("月度滚动过期制。 每个自然月的第1天00：00分自动清零 已满一年的积分。 举例：2018年8月1日开始清除2017年7月31日");
            integralRule.setRuleType(i);
            integralRule.setShopId(shopId);
            integralRule.setOpen(false);
            integralRule.setDeleted(false);
            integralRule.setCreateTime(LocalDateTime.now());
            integralRule.setUpdateTime(LocalDateTime.now());
            int insert = integralRuleMapper.insertRule(integralRule);
            if (insert == 0) {
                throw new ServiceException("积分规则新增失败 !", SystemCode.DATA_ADD_FAILED.getCode());
            }
            //新增积分规则参数信息
            switch (i) {
                case 0:
                    saveIntegralShare(integralRule.getIntegralRuleId(), shopId);
                    break;
                case 1:
                    saveIntegralEnter(integralRule.getIntegralRuleId(), shopId);
                    break;
                case 2:
                    saveIntegralSignIn(integralRule.getIntegralRuleId(), shopId);
                    break;
                case 3:
                    saveIntegralStepNumber(integralRule.getIntegralRuleId(), shopId);
                default:
            }
        }
        return Boolean.TRUE;
    }

    /**
     * 步数默认值新增
     *
     * @param integralRuleId 积分规则表id
     */
    private void saveIntegralStepNumber(Long integralRuleId, String shopId) {
        IntegralRuleParameter integralRuleParameter = new IntegralRuleParameter();
        integralRuleParameter.setCreateTime(LocalDateTime.now());
        integralRuleParameter.setUpdateTime(LocalDateTime.now());
        integralRuleParameter.setIntegralRuleId(integralRuleId);
        integralRuleParameter.setShopId(shopId);
        integralRuleParameter.setDeleted(false);
        Integer integer = integralRuleParameterMapper.insertStepNumberInfo(integralRuleParameter);
        if (integer < 1) {
            throw new ServiceException("积分步数规则参数插入失败");
        }
    }


    /**
     * 签到默认值新增
     *
     * @param integralRuleId 积分规则表id
     */
    private void saveIntegralSignIn(Long integralRuleId, String shopId) {
        IntegralRuleParameter integralRuleParameter = new IntegralRuleParameter();
        integralRuleParameter.setCreateTime(LocalDateTime.now());
        integralRuleParameter.setUpdateTime(LocalDateTime.now());
        integralRuleParameter.setIntegralRuleId(integralRuleId);
        integralRuleParameter.setShopId(shopId);
        integralRuleParameter.setDeleted(false);
        Integer integer = integralRuleParameterMapper.insertSingInInfo(integralRuleParameter);
        if (integer < 1) {
            throw new ServiceException("积分签到规则参数插入失败");
        }
    }

    /**
     * 登入默认值新增
     *
     * @param integralRuleId 积分规则规则id
     */
    private void saveIntegralEnter(Long integralRuleId, String shopId) {
        IntegralRuleParameter integralRuleParameter = new IntegralRuleParameter();
        integralRuleParameter.setCreateTime(LocalDateTime.now());
        integralRuleParameter.setUpdateTime(LocalDateTime.now());
        integralRuleParameter.setIntegralRuleId(integralRuleId);
        integralRuleParameter.setShopId(shopId);
        integralRuleParameter.setDeleted(false);
        Integer integer = integralRuleParameterMapper.insertEnterInfo(integralRuleParameter);
        if (integer < 1) {
            throw new ServiceException("积分登入规则参数插入失败");
        }
    }

    /**
     * 积分规则分享参数默认值
     *
     * @param integralRuleId 积分规则id
     */
    private void saveIntegralShare(Long integralRuleId, String shopId) {
        BigDecimal bigDecimal = new BigDecimal(100);
        IntegralRuleParameter integralRuleParameter = new IntegralRuleParameter();
        integralRuleParameter.setIntegralRuleId(integralRuleId);
        integralRuleParameter.setKey("day_share");
        integralRuleParameter.setValue(bigDecimal);
        integralRuleParameter.setBelongType(CommonConstants.NUMBER_ZERO);
        integralRuleParameter.setShopId(shopId);
        integralRuleParameter.setDeleted(false);
        integralRuleParameter.setCreateTime(LocalDateTime.now());
        integralRuleParameter.setUpdateTime(LocalDateTime.now());
        Integer integer = integralRuleParameterMapper.insertInfo(integralRuleParameter);
        if (integer < 1) {
            throw new ServiceException("积分分享规则参数插入失败");
        }
    }

    /**
     * 积分规则参数新增
     *
     * @param integralRuleParameterDtos 积分规则参数信息
     * @param integralRuleId            积分规则表id
     */
    private void addRuleParameterList(List<IntegralRuleParameterDto> integralRuleParameterDtos, Long integralRuleId) {
        String shopId = ISecurity.userMust().getShopId();

        integralRuleParameterDtos.forEach(bean -> {
            IntegralRuleParameter integralRuleParameter = bean.coverIntegralRuleParameter();
            integralRuleParameter.setIntegralRuleId(integralRuleId);
            //添加状态为未删除
            integralRuleParameter.setDeleted(false);
            integralRuleParameter.setCreateTime(LocalDateTime.now());
            integralRuleParameter.setUpdateTime(LocalDateTime.now());
            integralRuleParameter.setShopId(shopId);
            int insertRuleParameter = integralRuleParameterMapper.insertInfo(integralRuleParameter);
            if (insertRuleParameter == 0) {
                throw new ServiceException("积分规则参数新增失败！", SystemCode.DATA_ADD_FAILED.getCode());
            }
        });
    }

    /**
     * 获取积分规则
     *
     * @return List<ApiIntegralRuleDto>
     */
    @Override
    public List<ApiIntegralRuleDto> getApiIntegralRule() {
        String shopId = ISecurity.userMust().getShopId();
        return integralRuleMapper.getRule(shopId, IntegralRuleGainTypeEnum.SING_IN.getStatus(),
                IntegralRuleGainTypeEnum.STEP_NUMBER.getStatus());
    }

    /**
     * 获取 商家设置最大签到天数
     *
     * @param integralShop 商城信息
     * @param status       状态
     */
    @Override
    public int getShopInfo(IntegralShop integralShop, int status) {
        Long integralRuleId = integralRuleMapper.getIntegralRuleTask(integralShop.getId(), status);
        if (integralRuleId == null) {
            throw new ServiceException("积分规则不存在", SystemCode.DATA_NOT_EXIST.getCode());
        }
        IntegralRuleParameter ruleParameterMapper = integralRuleParameterMapper
                .getRuleParameterTask(integralRuleId, IntegralRuleParametersEnum.DAYSS.getStatus());
        BigDecimal value = ruleParameterMapper.getValue();
        return value.intValue();
    }

}
