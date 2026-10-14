package com.medusa.gruul.discount.api.feign;

import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.discount.api.model.vo.api.ApiCouponVo;
import com.medusa.gruul.discount.api.model.vo.manager.FullScaleVo;
import com.medusa.gruul.discount.api.model.vo.manager.MemberCouponVo;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * @author lcysike
 * @Description: RemoteDiscountService.java
 * @Date: 2023/12/09
 */
@FeignClient(value = "discount")
@Hidden
public interface RemoteDiscountService {

    //=============================================订单接口部分===================================================

    /**
     * 查询单个客户优惠劵信息
     *
     * @param userId
     * @param productIds
     * @return List<ApiMemberCouponVo>
     * @Author lcysike
     * @Date 2023/12/19
     */
    @GetMapping("get/member/coupon/single")
    List<MemberCouponVo> findMemberCouponListForOrder(@RequestParam(value = "userId", required = true) String userId, @RequestParam(value = "productIds", required = true) List<Long> productIds);

    /**
     * 使用优惠劵
     *
     * @param id
     * @return Boolean
     * @Author lcysike
     * @Date 2023/12/19
     */
    @PutMapping("use/member/coupon")
    Boolean useMemberCoupon(@RequestBody Long id);

    /**
     * 归还优惠劵
     *
     * @param id
     * @return Boolean
     * @Author lcysike
     * @Date 2023/12/19
     */
    @PutMapping("revert/member/coupon")
    Boolean revertMemberCoupon(@RequestBody Long id);

    /**
     * 查询满减活动信息
     *
     * @param productIds
     * @return List<FullScaleVo>
     * @Author lcysike
     * @Date 2023/12/19
     */
    @GetMapping("get/full/scale/single")
    List<FullScaleVo> findFullScaleListForOrder(@RequestParam(value = "productIds", required = true) Collection<Long> productIds);

    /**
     * 获取单张满减活动信息
     *
     * @param id
     * @return com.medusa.gruul.discount.api.model.vo.manager.FullScaleVo
     * @author alan
     * @date 2023/12/29 15:44
     */
    @GetMapping("/get/full/scale/{id}")
    FullScaleVo findFullScaleForOrder(@Parameter(description = "满减活动id", required = true) @PathVariable("id") Long id);

    /**
     * 查询单个优惠劵信息
     *
     * @param userId
     * @param couponId
     * @return MemberCouponVo
     * @Author lcysike
     * @Date 2023/12/19
     */
    @GetMapping("get/coupon")
    MemberCouponVo findMemberCouponForOrder(@RequestParam(value = "userId", required = true) String userId, @RequestParam(value = "couponId", required = true) Long couponId);

    /**
     * 领取回收券
     *
     * @param userId   用户id
     * @param couponId 优惠券id
     * @param number   优惠券数量
     * @return
     */
    @GetMapping("grant/discount/coupon")
    Result<String> grantDiscountCoupon(@RequestParam(value = "userId", required = true) String userId,
                               @RequestParam(value = "couponId", required = true) Long couponId,
                               @RequestParam(value = "number", required = true) Integer number);
    //=============================================商品接口部分===================================================

    /**
     * 查询单个商品可使用优惠劵信息
     *
     * @param userId
     * @param productId
     * @return List<ApiCouponVo>
     * @Author lcysike
     * @Date 2024/01/02
     */
    @GetMapping("get/coupon/product")
    List<ApiCouponVo> findCouponListForProduct(@RequestParam(value = "userId", required = true) String userId, @RequestParam(value = "productId", required = true) Long productId);

    /**
     * 查询单个商品可使用满减信息
     *
     * @param productId
     * @return List<FullScaleVo>
     * @Author lcysike
     * @Date 2024/01/02
     */
    @GetMapping("get/full/scale/product")
    List<FullScaleVo> findFullScaleListForProduct(@RequestParam(value = "productId", required = true) Long productId);

}
