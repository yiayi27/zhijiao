package com.medusa.gruul.discount.service.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.discount.api.entity.Coupon;
import com.medusa.gruul.discount.api.model.vo.api.ApiCouponVo;

import java.util.List;

/**
 * <p>
 * 优惠劵 服务类
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
public interface IApiCouponService extends IService<Coupon> {

    /**
     * 查询列表
     *
     * @return 优惠劵list
     *
     */
    List<ApiCouponVo> getApiCouponList();

    /**
     * 优惠劵组件信息列表
     *
     * @return 优惠劵list
     *
     */
    List<ApiCouponVo> getApiCouponComponentList();

    /**
     * 查询购物车优惠劵列表
     *
     * @return 优惠劵list
     *
     */
    List<ApiCouponVo> getApiShoppingCartCouponList();

    /**
     * 查询单个商品可使用优惠劵信息
     *
     * @param userId
     * @param productId
     * @return List<CouponVo>
     */
    List<ApiCouponVo> findCouponListForProduct(String userId, Long productId);

    /**
     * 查询积分优惠劵列表
     *
     * @return 积分优惠劵list
     *
     */
    List<ApiCouponVo> getApiIntegralCouponList();

}
