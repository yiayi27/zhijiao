package com.medusa.gruul.discount.web.controller.api;



import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.discount.api.model.vo.api.ApiCouponVo;
import com.medusa.gruul.discount.service.api.IApiCouponService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 小程序优惠劵 前端控制器
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
@RestController
@RequestMapping("/api/coupon")
public class ApiCouponController {

    @Autowired
    private IApiCouponService apiCouponService;

    /**
     * 优惠劵信息列表
     */
    @GetMapping("/list")
    @PreAuthorize("permitAll()")
    @Operation(summary = "优惠劵信息列表")
    public Result<List<ApiCouponVo>> getApiCouponList() {
        List<ApiCouponVo> apiCouponVos = apiCouponService.getApiCouponList();
        return Result.ok(apiCouponVos);
    }

    /**
     * 优惠劵组件信息列表
     */
    @GetMapping("/component/list")
    @PreAuthorize("permitAll()")
    @Operation(summary = "优惠劵组件信息列表")
    public Result<List<ApiCouponVo>> getApiCouponComponentList() {
        List<ApiCouponVo> apiCouponVos = apiCouponService.getApiCouponComponentList();
        return Result.ok(apiCouponVos);
    }

    /**
     * 购物车优惠劵信息列表
     */
    @GetMapping("/shopping/cart/list")
    @Operation(summary = "购物车优惠劵信息列表")
    public Result<List<ApiCouponVo>> getApiShoppingCartCouponList() {
        List<ApiCouponVo> apiCouponVos = apiCouponService.getApiShoppingCartCouponList();
        return Result.ok(apiCouponVos);
    }

    /**
     * 积分优惠劵信息列表
     */
    @GetMapping("/integral/list")
    @Operation(summary = "积分优惠劵信息列表")
    public Result<List<ApiCouponVo>> getApiIntegralCouponList() {
        List<ApiCouponVo> apiCouponVos = apiCouponService.getApiIntegralCouponList();
        return Result.ok(apiCouponVos);
    }
}
