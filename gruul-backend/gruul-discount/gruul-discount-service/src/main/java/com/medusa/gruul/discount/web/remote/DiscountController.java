package com.medusa.gruul.discount.web.remote;



import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.discount.api.model.vo.api.ApiCouponVo;
import com.medusa.gruul.discount.api.model.vo.manager.FullScaleVo;
import com.medusa.gruul.discount.api.model.vo.manager.MemberCouponVo;
import com.medusa.gruul.discount.service.api.IApiCouponService;
import com.medusa.gruul.discount.service.manager.IFullScaleService;
import com.medusa.gruul.discount.service.manager.IMemberCouponService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 优惠劵、满减 前端控制器
 * </p>
 *
 * @author lcysike
 * @since 2023-12-24
 */
@RestController(value = "remoteDiscountController")
@RequestMapping("/")
public class DiscountController {

    @Autowired
    private IApiCouponService apiCouponService;

    @Autowired
    private IMemberCouponService memberCouponService;

    @Autowired
    private IFullScaleService fullScaleService;


    //=============================================订单部分===================================================

    /**
     * 查询单个客户的可使用优惠劵
     *
     * @param userId
     * @return com.medusa.gruul.common.core.util.Result<java.util.List < com.medusa.gruul.discount.api.model.vo.manager.MemberCouponVo>>
     */
    @GetMapping("get/member/coupon/single")
    @PreAuthorize("permitAll()")
    public List<MemberCouponVo> findMemberCouponListForOrder(@RequestParam(value = "userId", required = true) String userId, @RequestParam(value = "productIds", required = true) List<Long> productIds) {
        return memberCouponService.findMemberCouponListForOrder(userId, productIds);
    }

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
    @PreAuthorize("permitAll()")
    public MemberCouponVo findMemberCouponForOrder(@RequestParam(value = "userId", required = true) String userId, @RequestParam(value = "couponId", required = true) Long couponId) {
        return memberCouponService.findMemberCouponForOrder(userId, couponId);
    }

    /**
     * 查询可使用的满减活动
     *
     * @param productIds
     * @return com.medusa.gruul.common.core.util.Result<java.util.List < com.medusa.gruul.discount.api.model.vo.manager.FullScaleVo>>
     */
    @GetMapping("get/full/scale/single")
    @PreAuthorize("permitAll()")
    public List<FullScaleVo> findFullScaleListForOrder(@RequestParam(value = "productIds", required = true) Collection<Long> productIds) {
        return fullScaleService.findFullScaleListForOrder(productIds);
    }

    /**
     * 获取单张满减活动信息
     *
     * @param id
     * @return com.medusa.gruul.discount.api.model.vo.manager.FullScaleVo
     * @author alan
     * @date 2023/12/29 15:44
     */
    @GetMapping("/get/full/scale/{id}")
    @PreAuthorize("permitAll()")
    public FullScaleVo findFullScaleForOrder(@Parameter(description = "满减活动id", required = true) @PathVariable("id") Long id) {
        return fullScaleService.getFullScaleById(id);
    }

    /**
     * 使用优惠劵
     *
     * @param id
     * @return com.medusa.gruul.common.core.util.Result<java.lang.Boolean>
     */
    @PutMapping("use/member/coupon")
    @PreAuthorize("permitAll()")
    public Boolean useMemberCouponForOrder(@RequestBody Long id) {
        return memberCouponService.useMemberCouponForOrder(id);
    }

    /**
     * 归还优惠劵
     *
     * @param id
     * @return com.medusa.gruul.common.core.util.Result<java.lang.Boolean>
     */
    @PutMapping("revert/member/coupon")
    @PreAuthorize("permitAll()")
    public Boolean revertMemberCouponForOrder(@RequestBody Long id) {
        return memberCouponService.revertMemberCouponForOrder(id);
    }


    /**
     * 领取回收券
     *
     * @param userId   用户id
     * @param couponId 优惠券id
     * @param number   优惠券数量
     * @return
     */
    @GetMapping("grant/discount/coupon")
    @PreAuthorize("permitAll()")
    public Result<String> grantDiscountCoupon(@RequestParam(value = "userId", required = true) String userId,
                                      @RequestParam(value = "couponId", required = true) Long couponId,
                                      @RequestParam(value = "number", required = true) Integer number) {
        return memberCouponService.grantDiscountCoupon(userId, couponId, number);
    }

    //=============================================商品部分===================================================

    /**
     * 查询单个商品可使用优惠劵信息
     *
     * @param userId
     * @return List<ApiCouponVo>
     */
    @GetMapping("get/coupon/product")
    @PreAuthorize("permitAll()")
    public List<ApiCouponVo> findMemberCouponListForProduct(@RequestParam(value = "userId", required = true) String userId, @RequestParam(value = "productId", required = true) Long productId) {
        return apiCouponService.findCouponListForProduct(userId, productId);
    }

    /**
     * 查询单个商品可使用满减信息
     *
     * @param productId
     * @return List<FullScaleVo>
     */
    @GetMapping("get/full/scale/product")
    @PreAuthorize("permitAll()")
    public List<FullScaleVo> findFullScaleListForProduct(@RequestParam(value = "productId", required = true) Long productId) {
        return fullScaleService.findFullScaleListForProduct(productId);
    }
}
