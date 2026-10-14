package com.medusa.gruul.discount.service.manager.impl;

import com.medusa.gruul.discount.api.entity.CouponProduct;
import com.medusa.gruul.discount.mapper.manager.CouponProductMapper;
import com.medusa.gruul.discount.service.manager.ICouponProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 优惠劵关联商品表 服务实现类
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Service
public class CouponProductServiceImpl extends ServiceImpl<CouponProductMapper, CouponProduct> implements ICouponProductService {

}
