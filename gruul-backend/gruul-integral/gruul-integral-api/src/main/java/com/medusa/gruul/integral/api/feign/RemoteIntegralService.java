package com.medusa.gruul.integral.api.feign;

import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.integral.api.model.*;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


/**
 * @Description: 提供积分商家设置的积分折扣规则
 * @Author: xiaoq
 * @Date: 2023/12/11 10:16
 */
@FeignClient(value = "integral")
@Hidden
public interface RemoteIntegralService {

    /**
     * 获取商家设置的抵扣规则
     *
     * @param shopId 商家店铺id
     * @return com.medusa.gruul.integral.api.model.DiscountInfoDto
     */
    @GetMapping(value = "discountInfo")
    @Operation(summary = "获取商家积分抵扣规则")
    DiscountInfoDto discountInfo(
            @RequestParam(value = "shopId", required = true) @NotNull(message = "shopId不能为空") String shopId);


    /**
     * 积分归还 增加 部分订单取消可进行积分归还 如:超时未支付
     *
     * @param userId        用户Id
     * @param integralValue 归还的积分值
     * @return
     */
    @GetMapping(value = "giveBack")
    @Operation(summary = "归还")
    Boolean integralReturn(
            @RequestParam(value = "userId", required = true) @NotNull(message = "用户id不能为null") String userId,
            @RequestParam(value = "integralValue", required = true) @NotNull(message = "积分值不能为空") BigDecimal integralValue);


    /**
     * 生成明细(购买商品)
     *
     * @param userId        用户Id
     * @param integralValue 扣除积分值
     * @param shopId       店铺id
     * @return 是否成功
     */
    @GetMapping(value = "deduction")
    @Operation(summary = "积分扣减")
    Boolean integralDeduction(
            @RequestParam(value = "userId") @NotNull(message = "用户id不能为null") String userId,
            @RequestParam(value = "integralValue") @NotNull(message = "积分值不能为空") BigDecimal integralValue,
            @RequestParam(value = "shopId") @NotNull(message = "shopId不能为空") String shopId);
    /**
     * 兑换积分优惠券
     *
     * @param integralCouponDto 生成订单所需值
     * @return 是否成功
     */
    @PutMapping(value = "conversion")
    @Operation(summary = "兑换积分优惠劵")
    Result<Integer> conversionIntegralCoupon(@RequestBody IntegralCouponDto integralCouponDto);


    /**
     * 生成积分商城所需 默认值
     *
     * @param shopId   商铺id
     * @return 是否成功
     */
    @PostMapping(value = "generate/default/values")
    @Operation(summary = "生成积分默认值")
    Boolean integralDefault(
            @RequestParam(value = "shopId", required = true) @NotNull(message = "shopId不能为空") String shopId);
    /**
     * 获取商城信息 积分商城状态 签到状态 步数状态
     *
     * @param shopId 商铺id
     * @return com.medusa.gruul.integral.api.model.ShopFunctionInfoVo
     */
    @GetMapping("functionInfo")
    @Operation(summary = "获取积分默认值")
    ShopFunctionInfoVo getShoppingFunctionInfo(
            @RequestParam(value = "shopId", required = true) @NotNull(message = "shopId不能为空") String shopId);

    /**
     * 获取用户信息
     *
     * @param shopUserId 用户id
     * @return
     */
    @PostMapping("get/userInfo/{shopUserId}")
    @Operation(summary = "用户信息")
    UserInfoVo getUserInfo(@PathVariable(value = "shopUserId") String shopUserId);

    /**
     * 积分订单打印并发货
     *
     * @param integralOrderDelivery
     * @return
     */
    @Operation(summary = "积分订单打印并发货")
    @GetMapping("print/delivery")
    int doLogisticsIntegralOrderDelivery(@RequestParam(value = "integralOrderDelivery") String integralOrderDelivery);

    /**
     * 根据积分订单id获取订单信息 vo
     *
     * @param integralOrderId
     * @return
     */
    @Operation(summary = "根据积分订单id获取订单信息")
    @GetMapping("get/integralOrderInfo")
    IntegralOrderVo integralOrderInfo(
            @RequestParam(value = "integralOrderId", required = true) @NotNull Long integralOrderId);


    /**
     * 使用积分 兑换会员卡
     *
     * @param shopUserId    用户id
     * @param integralValue 积分值
     * @return
     */
    @Operation(summary = "使用积分,兑换会员卡")
    @PostMapping("conversion/clubCard")
    Result conversionClubCard(
            @RequestParam(value = "shopUserId", required = true) @NotNull(message = "用户id不能为null") String shopUserId,
            @RequestParam(value = "integralValue", required = true) @NotNull(message = "积分值不能为空") BigDecimal integralValue);


}
