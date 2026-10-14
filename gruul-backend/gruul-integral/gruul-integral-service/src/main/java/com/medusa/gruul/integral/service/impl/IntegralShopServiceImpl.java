package com.medusa.gruul.integral.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.integral.api.entity.IntegralRule;
import com.medusa.gruul.integral.api.entity.IntegralShop;
import com.medusa.gruul.integral.api.model.DiscountInfoDto;
import com.medusa.gruul.integral.api.model.ShopFunctionInfoVo;
import com.medusa.gruul.integral.mapper.IntegralShopMapper;
import com.medusa.gruul.integral.service.IIntegralRuleService;
import com.medusa.gruul.integral.service.IIntegralShopService;
import com.medusa.gruul.integral.web.enums.IntegralRuleGainTypeEnum;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralShopDto;
import com.medusa.gruul.integral.web.model.vo.IntegralShopVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 积分商城 服务实现类
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Service
public class IntegralShopServiceImpl extends ServiceImpl<IntegralShopMapper, IntegralShop> implements IIntegralShopService {
    @Autowired
    private IntegralShopMapper integralShopMapper;

    @Autowired
    private IIntegralRuleService integralRuleService;


    /**
     * 修改积分商城开启关闭状态
     *
     * @param shopId 店铺id
     * @param status 状态值
     */
    @Override
    public void updateIntegralShopStatus(String shopId, Boolean status) {
        Integer number = integralShopMapper.updateShopStatus(shopId, status);
        if (number != 1) {
            throw new ServiceException("数据错误", SystemCode.FAILURE.getCode());
        }
    }


    /**
     * 获取积分商城信息
     *
     * @param shopId 店铺id
     * @return IntegralShopVo
     */
    @Override
    public IntegralShopVo getIntegralShopInfo(String shopId) {
        return integralShopMapper.getShopInfo(shopId);
    }

    /**
     * @param integralShopDto 积分商城信息
     */
    @Override
    public Boolean addShopInfo(IntegralShopDto integralShopDto) {
        IntegralShop integralShop = integralShopDto.coverIntegralShop();
        String shopId = ISystem.shopId();
        //默认关闭积分商城
        integralShop.setType(false);
        Long aLong = integralShopMapper.selectByShopId(shopId);
        if (aLong != null) {
            throw new ServiceException("数据错误", SystemCode.FAILURE.getCode());
        }
        int insert = integralShopMapper.insert(integralShop);
        if (insert == 0) {
            throw new ServiceException("默认积分商城信息插入失败", SystemCode.DATA_ADD_FAILED.getCode());
        }
        return true;
    }

    /**
     * 商家积分抵扣规则
     *
     * @param shopId 店铺id
     * @return DiscountInfoDto
     */
    @Override
    public DiscountInfoDto getDiscountInfo(String shopId) {
        return integralShopMapper.getDiscountInfo(shopId);
    }


    /**
     * 获取所有商城信息
     *
     * @return 查询所有积分商城
     */
    @Override
    public List<IntegralShop> getAllShopInfo() {
        return integralShopMapper.selectAllShopInfo();
    }




    /**
     * 获取商城功能信息  商城信息 步数信息 签到信息
     *
     * @param shopId 商铺id
     * @return ShopFunctionInfoVo
     */
    @Override
    public ShopFunctionInfoVo getShopFunctionInfo(String shopId) {
        //商城信息
        IntegralShopVo shopInfo = integralShopMapper.getShopInfo(shopId);
        if (null == shopInfo) {
            throw new ServiceException("商城信息不存在", SystemCode.DATA_NOT_EXIST.getCode());
        }
        Map<Integer, Boolean> signStepOpenMap = integralRuleService.lambdaQuery()
                .select(IntegralRule::getRuleType, IntegralRule::getOpen)
                .eq(IntegralRule::getIntegralShopId, shopInfo.getId())
                .in(IntegralRule::getRuleType, IntegralRuleGainTypeEnum.SING_IN.getStatus(), IntegralRuleGainTypeEnum.STEP_NUMBER.getStatus())
                .list()
                .stream()
                .collect(
                        Collectors.toMap(
                                IntegralRule::getRuleType,
                                IntegralRule::getOpen
                        )
                );
        //封装
        ShopFunctionInfoVo vo = new ShopFunctionInfoVo();
        vo.setShop(shopInfo.getType());
        vo.setSingIn(signStepOpenMap.getOrDefault(IntegralRuleGainTypeEnum.SING_IN.getStatus(), false));
        vo.setStepNumber(signStepOpenMap.getOrDefault(IntegralRuleGainTypeEnum.STEP_NUMBER.getStatus(), false));
        return vo;
    }
}
