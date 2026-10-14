package com.medusa.gruul.integral.service;

import com.medusa.gruul.integral.api.entity.IntegralRule;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.integral.api.entity.IntegralShop;
import com.medusa.gruul.integral.web.model.dto.manager.ApiIntegralRuleDto;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralRuleDto;

import java.util.List;

/**
 * <p>
 * 积分规则表 服务类
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
public interface IIntegralRuleService extends IService<IntegralRule> {

	/**
	 * 获取 积分规则
	 *
	 * @param shopId 店铺id
     * @return
	 */
	List<IntegralRuleDto> getIntegralShopRule(String shopId);

	/**
	 * 修改积分规则
	 *
	 * @param integralRuleDto
     *
	 */
	void updateIntegralRule(List<IntegralRuleDto> integralRuleDto);


	/**
	 * 默认积分规则新增
	 *
	 * @param integralRuleDto
	 */
	void saveIntegralRule(List<IntegralRuleDto> integralRuleDto);

    /**
     * 获取积分规则
     *
     * @return
     */
	List<ApiIntegralRuleDto> getApiIntegralRule();


    /**
     * 获取规则信息
     * @param integralShop 商城信息
     * @param status
     * @return
     */
	int getShopInfo(IntegralShop integralShop, int status);


	/**
	 * 生成积分规则默认值
	 * @param id 积分id
	 * @param shopId 店铺id
	 * @return
	 */
	Boolean saveIntegralDefaultRule(Long id,String shopId);
}
