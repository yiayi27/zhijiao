package com.medusa.gruul.order.controller.remote;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.medusa.gruul.afs.api.entity.AfsOrder;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.order.api.entity.OrderSetting;
import com.medusa.gruul.order.api.enums.OrderStatusEnum;
import com.medusa.gruul.order.api.model.*;
import com.medusa.gruul.order.service.IMiniOrderService;
import com.medusa.gruul.order.service.IRemoteOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Feign接口
 *
 * @author alan
 * @date 2023/12/16 19:41
 */
@RestController
@RequestMapping("/remote")
@Tag(name = "Feign接口")
public class RemoteOrderController {
    @Resource
    private IRemoteOrderService remoteOrderService;
    @Resource
    private IMiniOrderService miniOrderService;

    /**
     * 订单详情
     *
     * @param orderId
     * @return com.medusa.gruul.common.core.util.Result<com.medusa.gruul.order.api.entity.Order>
     * @Author alan
     * @Date 2023/9/9 23:06
     */
    @Operation(summary = "订单详情")
    @PreAuthorize("permitAll()")
    @GetMapping("/info")
    public OrderVo orderInfo(@RequestParam Long orderId) {
        return remoteOrderService.orderInfo(orderId);
    }

    /**
     * getOrderSetting
     *
     * @param
     * @return com.medusa.gruul.order.api.entity.OrderSetting
     * @author alan
     * @date 2024/8/17 22:46
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取订单设置信息")
    @GetMapping("/setting")
    public OrderSetting getOrderSetting() {
        return remoteOrderService.getOrderSetting();
    }

    /**
     * 获取商品评价
     *
     * @author alan
     * @date 2024/2/7 20:12
     */

    @PreAuthorize("permitAll()")
    @Operation(summary = "获取商品评价")
    @GetMapping("/product/rate")
    List<ProductRateVo> productRate(@RequestParam(value = "productIds") @NotNull List<Long> productIds) {
        if (productIds.isEmpty()) {
            return Collections.emptyList();
        }
        return remoteOrderService.productRate(productIds);
    }

    /**
     * 获取未生成发货单的订单
     *
     * @param start    开始时间
     * @param end      结束时间
     * @param shopId   店铺id
     * @return java.util.List<com.medusa.gruul.order.api.model.GetOrderListDto>
     * @author alan，zhaozheng
     * @date 2023/12/12 20:51
     */

    @PreAuthorize("permitAll()")
    @Operation(summary = "获取未生成发货单的订单")
    @GetMapping(value = "/get/not-shipped/orders")
    public List<GetOrderListDto> getNotShippedOrder(@RequestParam String start, @RequestParam String end,
                                                     @RequestParam String shopId) {
        GetOrderListDtoByTimeScope param = new GetOrderListDtoByTimeScope(start, end, shopId);
        return remoteOrderService.getNotShippedOrder(param);
    }


    /**
     * 根据订单Id获取指定订单详情
     *
     * @param shopId   店铺id
     * @param orderIds 订单集合
     * @return java.util.List<com.medusa.gruul.order.api.model.GetOrderListDto>
     * @author alan，zhaozheng
     * @date 2023/12/12 20:53
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "根据订单Id获取指定订单详情")
    @GetMapping(value = "/get/orders")
    public List<GetOrderListDto> getOrderListByIds(@RequestParam String shopId,
                                                   @RequestParam Long[] orderIds) {
        GetOrderListParam param = new GetOrderListParam(shopId, Arrays.asList(orderIds));
        return remoteOrderService.getOrderListByIds(param);
    }

    @PreAuthorize("permitAll()")
    @Operation(summary = "物流订单发货")
    @GetMapping("/items/delivery")
    public int doLogisticsOrderDelivery(@RequestParam(value = "orderDeliveryDtoList") String orderDeliveryDtoList) {
        List<OrderDeliveryDto> orderDeliveryDtos = JSONUtil.toList(orderDeliveryDtoList, OrderDeliveryDto.class);
        if (null == orderDeliveryDtos || 0 == orderDeliveryDtos.size()) {
            return 0;
        }
        return remoteOrderService.doLogisticsOrderDelivery(orderDeliveryDtos);
    }

    /**
     * 统计活动的销售额
     *
     * @param activityIds
     * @return int
     * @author alan
     * @date 2024/3/23 22:01
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "统计活动的销售额")
    @GetMapping("/activity/statistics")
    List<ActivityStatisticsVo> activityStatistics(@RequestParam(value = "activityIds") Long[] activityIds) {
        return remoteOrderService.activityStatisticsByActivityId(activityIds);

    }

    /**
     * 统计满减活动的销售额
     *
     * @param fullScaleIds
     * @return int
     * @author alan
     * @date 2024/3/23 22:01
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "统计满减活动的销售额")
    @GetMapping("/fullScale/statistics")
    List<ActivityStatisticsVo> fullScaleStatistics(@RequestParam(value = "fullScaleIds") Long[] fullScaleIds) {
        return remoteOrderService.fullScaleStatisticsByActivityId(fullScaleIds);
    }


    /**
     * 批量获取商品的最后购买人信息
     *
     * @param productIds
     * @return java.util.List<com.medusa.gruul.order.api.model.ProductBuyerVo>
     * @author alan
     * @date 2024/5/7 23:15
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "批量获取商品的最后购买人信息")
    @GetMapping("/product/last/buyers")
    List<ProductBuyerVo> getProductLastBuyers(@RequestParam(value = "productIds") String[] productIds) {
        return remoteOrderService.getProductLastBuyers(productIds);
    }


    /**
     * 用户订单概况，需要在header带上商户信息和用户信息
     *
     * @param
     * @return com.medusa.gruul.order.api.model.OrderOverviewVo
     * @author alan
     * @date 2024/5/13 20:54
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "用户订单概况", description = "用户订单概况")
    @GetMapping("/overview")
    public OrderOverviewVo orderOverview(@RequestParam(value = "userId") String userId) {
        OrderOverviewVo orderOverviewVo = miniOrderService.orderOverview(userId);
        return orderOverviewVo;
    }

    @PreAuthorize("permitAll()")
    @Operation(summary = "查询待发货订单包含的商品ID")
    @GetMapping("/product/wait/send")
    public List<Long> waitSendProduct(@RequestParam(value = "sendBillId") String sendBillId) {
        if (StrUtil.isBlank(sendBillId)) {
            return CollUtil.newArrayList();
        }
        List<Long> productIds = remoteOrderService.waitSendProductList(sendBillId);
        return productIds;
    }


    /**
     * 批量查询多个订单详情
     *
     * @param orderIds
     * @return com.medusa.gruul.common.core.util.Result<com.medusa.gruul.order.api.entity.Order>
     * @Author alan
     * @Date 2023/9/9 23:06
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "批量查询多个订单详情")
    @GetMapping("/info/list")
    public List<OrderVo> orderInfoList(@RequestParam(value = "orderIds") List<Long> orderIds) {
        return remoteOrderService.orderInfoList(orderIds);
    }

    /**
     * createExchangeOrder
     *
     * @param dto
     * @return java.lang.Long
     * @author alan
     * @date 2024/8/23 13:00
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "创建换货单订单")
    @PostMapping("/exchange/order")
    public Long createExchangeOrder(@RequestBody ExchangeOrderDto dto) {
        return remoteOrderService.createExchangeOrder(dto);
    }

    /**
     * 订单退款
     *
     * @param refundAmount
     * @param orderId
     * @return java.lang.Long
     * @author alan
     * @date 2024/8/23 12:56
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "订单退款")
    @PutMapping("/close")
    public void closeOrder(
            @RequestParam(value = "afsId") Long afsId,
            @RequestParam(value = "refundAmount") BigDecimal refundAmount,
            @RequestParam(value = "type") Integer type,
            @RequestParam(value = "orderId") Long orderId) {
        remoteOrderService.closeOrder(afsId, refundAmount, type, orderId);

    }

    /**
     * 签收订单
     *
     * @param orderId
     * @return java.lang.Long
     * @author alan
     * @date 2024/8/23 12:56
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "签收订单")
    @PutMapping("/receipt")
    public void receiptOrder(
            @RequestParam(value = "orderId") Long orderId) {
        miniOrderService.receiptOrder(orderId, true);
    }

    /**
     * 申请售后成功关闭之前的换货单
     *
     * @param orderIds
     * @return void
     * @author alan
     * @date 2024/9/1 20:29
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "申请售后成功关闭之前的换货单")
    @PutMapping("/close/exchange")
    public void closeExchangeOrder(@RequestBody List<Long> orderIds) {
        remoteOrderService.closeExchangeOrder(orderIds);

    }

    /**
     * 根据订单ID和SkuId查询指定商品有没有售后信息
     *
     * @param orderId
     * @param productSkuId
     * @return void
     * @author alan
     * @date 2024/10/5 10:02
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "根据订单ID和SkuId查询指定商品有没有售后信息")
    @GetMapping("/{orderId}/{productSkuId}/afsOrder/")
    public AfsOrder selectByOrderIdAndProductSkuId(@PathVariable(value = "orderId") Long orderId,
                                                   @PathVariable(value = "productSkuId") Long productSkuId) {
        return remoteOrderService.selectByOrderIdAndProductSkuId(orderId, productSkuId);
    }


    /**
     * 检查订单是否有真正进行中的售后
     *
     * @param orderIds the order ids
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "检查订单是否有真正进行中的售后")
    @GetMapping("/checkAfsOrder")
    public Boolean checkAfsOrder(@RequestParam(value = "orderIds") List<Long> orderIds) {
        return remoteOrderService.checkAfsOrder(orderIds);
    }

    /**
     * 更新订单到指定状态
     *
     * @param orderIds the order ids
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "更新订单到指定状态")
    @PutMapping("/order/status")
    public Boolean updateOrderStatus(@RequestParam(value = "orderIds") List<Long> orderIds,
                                     @RequestParam(value = "statusEnum") OrderStatusEnum statusEnum) {
        return remoteOrderService.updateOrderStatus(orderIds, statusEnum);
    }

    /**
     * 设置订单业务流水号
     */
    @PreAuthorize("permitAll()")
    @PutMapping("/order/{orderId}/transaction/{transactionId}")
    public Result<Void> setOrderTransactionId(@PathVariable Long orderId, @PathVariable String transactionId) {
        remoteOrderService.setOrderTransactionId(orderId, transactionId);
        return Result.ok();
    }
}
