package com.medusa.gruul.discount.mapper.api;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.discount.api.entity.CouponProduct;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 优惠劵关联商品表 Mapper 接口
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Component
public interface ApiCouponProductMapper extends BaseMapper<CouponProduct> {

}
