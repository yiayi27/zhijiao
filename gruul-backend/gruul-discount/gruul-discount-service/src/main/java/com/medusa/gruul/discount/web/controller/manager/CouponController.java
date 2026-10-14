package com.medusa.gruul.discount.web.controller.manager;


import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.discount.api.model.dto.manager.CouponDto;
import com.medusa.gruul.discount.api.model.param.manager.CouponParam;
import com.medusa.gruul.discount.api.model.vo.manager.CouponSimpleVo;
import com.medusa.gruul.discount.api.model.vo.manager.CouponVo;
import com.medusa.gruul.discount.api.model.vo.manager.RecycleCouponVo;
import com.medusa.gruul.discount.service.manager.ICouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 优惠劵 前端控制器
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
@RestController
@RequestMapping("/manager/coupon")
public class CouponController {

    @Autowired
    private ICouponService couponService;

    /**
     * 优惠劵信息列表
     */
    @GetMapping("/list")
    @Operation(summary = "优惠劵信息列表")
    public Result<PageUtils<CouponVo>> getCouponList(CouponParam couponParam) {
        PageUtils<CouponVo> pageUtils = new PageUtils<>(couponService.getCouponList(couponParam));
        return Result.ok(pageUtils);
    }

    /**
     * 回收券信息列表
     */
    @GetMapping("/recycle/list")
    @Operation(summary = "回收券列表")
    public Result<List<RecycleCouponVo>> getRecycleList() {
        List<RecycleCouponVo> recycleList = couponService.getRecycleList();
        return Result.ok(recycleList);
    }


    /**
     * 积分优惠劵信息列表
     */
    @GetMapping("/integral/list")
    @Operation(summary = "积分优惠劵信息列表")
    public Result<PageUtils<CouponVo>> getIntegralCouponList(CouponParam couponParam) {
        PageUtils<CouponVo> pageUtils = new PageUtils<>(couponService.getIntegralCouponList(couponParam));
        return Result.ok(pageUtils);
    }

    /**
     * 获取单张优惠劵信息
     */
    @GetMapping("/get/{id}")
    @Operation(summary = "单张优惠劵信息")
    public Result<CouponSimpleVo> getSimpleCoupon(@Parameter(description = "优惠劵id", required = true) @PathVariable("id") Long id) {
        CouponSimpleVo couponSimpleVo = couponService.getSimpleCoupon(id);
        return Result.ok(couponSimpleVo);
    }

    /**
     * 赠送优惠劵
     */
    @PostMapping("/give/{ids}")
    @Operation(summary = "赠送优惠劵")
    public Result<Void> giveCoupon(@Parameter(description = "优惠劵信息list", required = true) @RequestBody List<CouponDto> couponDtos,
                             @Parameter(description = "用户ids", required = true) @PathVariable(name = "ids") String[] ids) {
        couponService.giveCoupon(couponDtos, ids);
        return Result.ok();
    }

    /**
     * 优惠劵新增
     */
    @PostMapping("/add")
    @Operation(summary = "优惠劵新增")
    public Result<Void> addCoupon(@RequestBody @Validated CouponDto couponDto) {
        couponService.addCoupon(couponDto);
        return Result.ok();
    }

    /**
     * 优惠劵修改
     */
    @PutMapping("/update")
    @Operation(summary = "优惠劵修改")
    public Result<Void> updateCoupon(@RequestBody @Validated CouponDto couponDto) {
        couponService.updateCoupon(couponDto);
        return Result.ok();
    }

    /**
     * 关闭/启用优惠劵
     */
    @PutMapping("/changeStatus/{status}")
    @Operation(summary = "关闭/启用优惠劵")
    public Result<Void> changeCouponStatus(@Parameter(description = "优惠劵ids", required = true) @RequestBody Long[] ids,
                                     @Parameter(description = "优惠劵状态值", required = true) @PathVariable("status") Integer status) {
        couponService.changeCouponStatus(ids, status);
        return Result.ok();
    }

    /**
     * 优惠劵删除
     */
    @DeleteMapping("/delete/{ids}")
    @Operation(summary = "优惠劵删除(单个与批量共用)")
    public Result<Void> deleteCouponList(@Parameter(description = "优惠劵ids", required = true) @PathVariable(name = "ids") Long[] ids) {
        couponService.deleteCouponList(ids);
        return Result.ok();
    }
}
