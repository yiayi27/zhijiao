package com.medusa.gruul.integral.service;

import com.medusa.gruul.integral.api.entity.IntegralShop;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.integral.api.model.DiscountInfoDto;
import com.medusa.gruul.integral.api.model.ShopFunctionInfoVo;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralShopDto;
import com.medusa.gruul.integral.web.model.vo.IntegralShopVo;

import java.util.List;

/**
 * <p>
 * 积分商城 服务类
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
public interface IIntegralShopService extends IService<IntegralShop> {


	/**
	 * 修改积分商城开启关闭状态
	 *
	 * @param shopId 店铺id
	 * @param status 状态值
	 */
	void updateIntegralShopStatus(String shopId,Boolean status);

	/**
	 * 获取积分商城信息
	 *
	 * @param shopId 店铺id
	 * @return
	 */
	IntegralShopVo getIntegralShopInfo(String shopId);

	/**
	 * 新增商家默认值 积分折扣比
	 *
	 * @param integralShopDto
     * @return
	 */
	Boolean addShopInfo(IntegralShopDto integralShopDto);

	/**
	 * 获取商家积分抵扣规则
	 *
	 * @param shopId
	 * @return
	 */
	DiscountInfoDto getDiscountInfo(String shopId);

	/**
	 * 获取商城信息 积分商城状态 签到状态 步数状态
	 *
	 * @param shopId 商铺id
	 * @return  功能开启状态
	 */
	ShopFunctionInfoVo getShopFunctionInfo(String shopId);

	/**
	 * 获取所有商城信息
	 *
	 * @return
	 */
	List<IntegralShop> getAllShopInfo();


}
