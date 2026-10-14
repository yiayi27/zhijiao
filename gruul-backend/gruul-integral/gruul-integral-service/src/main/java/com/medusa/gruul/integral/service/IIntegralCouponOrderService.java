package com.medusa.gruul.integral.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.integral.api.entity.IntegralCouponOrder;
import com.medusa.gruul.integral.api.model.IntegralCouponDto;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralCouponOrderDto;
import com.medusa.gruul.integral.web.model.param.manager.IntegralCouponOrderParam;

import java.util.List;


/**
 * <p>
 * 积分优惠券订单表 服务类
 * </p>
 *
 * @author xiaoq
 * @since 2024-1-9
 */
public interface IIntegralCouponOrderService extends IService<IntegralCouponOrder> {

	/**
	 * 按条件获取积分优惠券订单
	 *
	 * @param integralCouponOrderParam
	 * @return 分页数据
	 */
	IPage<IntegralCouponOrderDto> searchOrders(IntegralCouponOrderParam integralCouponOrderParam);

	/**
	 * 积分优惠券兑换
	 *
	 * @param integralCouponDto
	 * @return
	 */
	Result<Integer> addIntegralCouponOrder(IntegralCouponDto integralCouponDto);


	/**
	 * 对积分优惠券订单 进行备注
	 *
	 * @param ids  ids
	 * @param backRemark 备注内容
	 * @param isCoverage 是否覆盖
	 */
	void remarkIntegralCouponOrder(List<Long> ids, String backRemark, Boolean isCoverage);
}
