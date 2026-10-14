package com.medusa.gruul.integral.web.controller.manager;

import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.integral.service.IIntegralCouponOrderService;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralCouponOrderDto;
import com.medusa.gruul.integral.web.model.param.manager.IntegralCouponOrderParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * @Description: 积分优惠券订单
 * @Author: xiaoq
 * @Date: 2024/1/9 10:25
 */

@RestController
@RequestMapping("/manager/integralCouponOrder")
@Tag(name = "积分优惠券订单相关接口")
public class IntegralCouponOrderController {

    @Autowired
    private IIntegralCouponOrderService integralCouponOrderService;


    /**
     * pc端 积分优惠券订单分页展示
     *
     * @param integralCouponOrderParam 查询条件
     * @return 符合条件的 优惠券订单
     */
    @Operation(summary = "积分优惠券订单")
    @GetMapping("get/integralCouponOrder")
    public Result<PageUtils<IntegralCouponOrderDto>> searchIntegralCouponOrder(IntegralCouponOrderParam integralCouponOrderParam) {
        PageUtils<IntegralCouponOrderDto> page = new PageUtils<>(integralCouponOrderService.searchOrders(integralCouponOrderParam));
        return Result.ok(page);
    }


    /**
     * 批量备注 积分优惠券订单
     *
     * @param ids        积分优惠券订单ids
     * @param backRemark 备注内容
     * @param isCoverage 是否覆盖
     */
    @Operation(summary = "订单批量备注功能")
    @PutMapping("update/orderRemark")
    public Result<Void> updateRemark(@RequestBody Long[] ids, @RequestParam(value = "backRemark") String backRemark,
                               @RequestParam(value = "isCoverage") Boolean isCoverage) {
        integralCouponOrderService.remarkIntegralCouponOrder(Arrays.asList(ids), backRemark, isCoverage);
        return Result.ok();
    }
}
