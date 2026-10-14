package com.medusa.gruul.discount.web.controller.api;


import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.discount.api.model.vo.api.ApiMemberCouponVo;
import com.medusa.gruul.discount.service.api.IApiMemberCouponService;
import com.medusa.gruul.goods.api.model.param.api.ApiProductParam;
import com.medusa.gruul.goods.api.model.vo.api.ApiAliveProductVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 小程序客户优惠劵 前端控制器
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
@RestController
@RequestMapping("/api/member/coupon")
public class ApiMemberCouponController {

    @Autowired
    private IApiMemberCouponService apiMemberCouponService;

    /**
     * 获取用户优惠劵信息
     */
    @GetMapping("/getByUserId")
    @Operation(summary = "用户优惠劵信息列表")
    public Result<List<ApiMemberCouponVo>> getMemberCouponListByUserId() {
        List<ApiMemberCouponVo> apiMemberCouponVos = apiMemberCouponService.getMemberCouponListByUserId();
        return Result.ok(apiMemberCouponVos);
    }

    /**
     * 优惠劵领取
     */
    @PostMapping("/receiveCoupon/{id}")
    @Operation(summary = "优惠劵领取")
    public Result<Void> receiveCoupon(@Parameter(description = "优惠劵id", required = true) @PathVariable(name = "id") Long couponId) {
        return apiMemberCouponService.receiveCoupon(couponId);
    }

    /**
     * 一键领取优惠劵
     */
    @PostMapping("/receiveCoupon/some/{ids}")
    @Operation(summary = "一键领取优惠劵")
    public Result<Void> receiveSomeCoupon(@Parameter(description = "优惠劵ids", required = true) @PathVariable(name = "ids") Long[] ids) {
        apiMemberCouponService.receiveSomeCoupon(ids);
        return Result.ok();
    }

    /**
     * 积分优惠劵兑换
     */
    @PostMapping("/exchangeCoupon/{id}")
    @Operation(summary = "积分优惠劵兑换")
    public Result<Void> exchangeCoupon(@Parameter(description = "优惠劵id", required = true) @PathVariable(name = "id") Long couponId) {
        apiMemberCouponService.exchangeCoupon(couponId);
        return Result.ok();
    }

    /**
     * 获取单张客户已领取优惠券可使用商品列表
     */
    @GetMapping("/alive/product/list/{id}")
    @Operation(summary = "获取单张客户已领取优惠券可使用商品列表")
    public Result<PageUtils<ApiAliveProductVo>> getProductPageBySingleCoupon(@Parameter(description = "客户优惠劵id", required = true) @PathVariable("id") Long id,
                                                                             ApiProductParam apiProductParam) {
        PageUtils<ApiAliveProductVo> pageUtils = new PageUtils<>(apiMemberCouponService.getProductPageBySingleCoupon(id, apiProductParam));
        return Result.ok(pageUtils);
    }

}
