package com.medusa.gruul.discount.mapper.api;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.discount.api.entity.Coupon;
import com.medusa.gruul.discount.api.model.vo.api.ApiCouponVo;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * <p>
 * 优惠劵 Mapper 接口
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Component
public interface ApiCouponMapper extends BaseMapper<Coupon> {

    /**
     *  获取优惠劵信息list
     * @param userId
     * @return 优惠劵list信息
     */
    List<ApiCouponVo> queryApiCouponList(String userId);

    /**
     *  获取积分优惠劵信息list
     * @param userId
     * @return 积分优惠劵list信息
     */
    List<ApiCouponVo> queryApiIntegralCouponList(String userId);

    /**
     * 获取优惠券信息列表.用户用户未登入是展示
     * @return
     */
    List<ApiCouponVo> queryApiCouponListNoLogin();
}
