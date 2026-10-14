package com.medusa.gruul.integral.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.integral.api.entity.IntegralCouponOrder;
import com.medusa.gruul.integral.api.entity.IntegralDetail;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralCouponOrderDto;
import com.medusa.gruul.integral.web.model.param.manager.IntegralCouponOrderParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description: 积分优惠券订单表 Mapper 接口
 * @Author: xiaoq
 * @Date: 2024/1/9 10:33
 */
@Repository
public interface IntegralCouponOrderMapper extends BaseMapper<IntegralCouponOrder> {


	/**
	 * 获取优惠卷订单
	 * @param page 分页对象
	 * @param integralCouponOrderParam 查询条件
	 * @return
	 */
	List<IntegralCouponOrderDto> getIntegralCouponOrderList(@Param("page") IPage<IntegralCouponOrderDto> page,
			@Param("integralCouponOrderParam") IntegralCouponOrderParam integralCouponOrderParam);
}
