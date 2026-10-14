package com.medusa.gruul.integral.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.integral.api.entity.IntegralOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.integral.api.model.IntegralOrderVo;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralCouponOrderDto;
import com.medusa.gruul.integral.web.model.param.manager.IntegralCouponOrderParam;
import com.medusa.gruul.integral.web.model.param.manager.IntegralDeliveryFiltrateParam;
import com.medusa.gruul.integral.web.model.param.manager.IntegralGoodsOrderParam;
import com.medusa.gruul.integral.web.model.vo.IntegralGoodsOrderVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 积分订单表 Mapper 接口
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Repository
public interface IntegralOrderMapper extends BaseMapper<IntegralOrder> {

	/**
	 * 获取积分商品订单信息List
	 * @param page 分页对象
	 * @param integralGoodsOrderParam
	 * @return
	 */
	List<IntegralGoodsOrderVo> getGoodsOrderList(@Param("page") IPage<IntegralGoodsOrderVo> page,
			@Param("integralGoodsOrderParam") IntegralGoodsOrderParam integralGoodsOrderParam);


	/**
	 * 根据订单id获取订单信息
	 * @param orderId 订单id
	 * @return
	 */
	IntegralGoodsOrderVo getGoodsOrderInfo(@Param("orderId") Long orderId);

	/**
	 * 获取积分优惠券订单信息List
	 * @param page 分页对象
	 * @param integralCouponOrderParam
	 * @return
	 */
	List<IntegralCouponOrderDto> getIntegralCouponOrderList(
			@Param("page") IPage<IntegralCouponOrderDto> page, @Param("integralCouponOrderParam") IntegralCouponOrderParam integralCouponOrderParam);


	/**
	 * 获取符合条件 的批量发货订单信息List
	 *
	 * @param integralDeliveryFiltrateParam 查询条件
	 * @return
	 */
	List<IntegralGoodsOrderVo> getFiltrateGoodsOrderList(
			@Param("integralDeliveryFiltrateParam") IntegralDeliveryFiltrateParam integralDeliveryFiltrateParam);

	/**
	 * 根据积分订单id获取 积分订单vo信息
	 *
	 * @param integralOrderId
	 * @return IntegralOrderVo
	 */
	IntegralOrderVo selectIntegralOrderVoById(@Param("integralOrderId") Long integralOrderId);

	/**
	 * 获取全部待发货订单信息
	 *
	 * @param status 待发货 状态
	 * @return
	 */
	List<IntegralGoodsOrderVo> selectAllWaitForSendIntegralOrder(@Param("status") int status);
}
