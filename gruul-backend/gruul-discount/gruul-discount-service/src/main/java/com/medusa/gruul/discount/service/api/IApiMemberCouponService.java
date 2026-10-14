package com.medusa.gruul.discount.service.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.discount.api.entity.MemberCoupon;
import com.medusa.gruul.discount.api.model.vo.api.ApiMemberCouponVo;
import com.medusa.gruul.goods.api.model.param.api.ApiProductParam;
import com.medusa.gruul.goods.api.model.vo.api.ApiAliveProductVo;

import java.util.List;

/**
 * <p>
 * 客户优惠劵 服务类
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
public interface IApiMemberCouponService extends IService<MemberCoupon> {

    /**
     * 查询用户购物车商品详情
     * @return 购物车list信息
     */
    List<ApiMemberCouponVo> getMemberCouponListByUserId();

    /**
     * 领取优惠劵
     * @param couponId 优惠劵id
     * @return Result
     */
    Result<Void> receiveCoupon(Long couponId);

    /**
     * 一键领取优惠劵
     * @param ids 优惠券id数组
     */
    void receiveSomeCoupon(Long[] ids);

    /**
     * 积分优惠劵兑换
     * @param couponId 优惠劵id
     */
    void exchangeCoupon(Long couponId);

    /**
     * 获取单张客户已领取优惠券可使用商品列表
     *
     * @param id 客户优惠券id
     * @param apiProductParam 商品查询信息
     * @return PageUtils
     *
     */
    IPage<ApiAliveProductVo> getProductPageBySingleCoupon(Long id, ApiProductParam apiProductParam);

}
