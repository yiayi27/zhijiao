package com.medusa.gruul.order.controller.mini;


import cn.hutool.core.util.ObjectUtil;
import com.medusa.gruul.account.api.feign.RemoteMiniAccountService;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.goods.api.entity.RebateSet;
import com.medusa.gruul.goods.api.feign.RemoteGoodsService;
import com.medusa.gruul.logistics.model.dto.manager.CountCostDto;
import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.api.entity.OrderSetting;
import com.medusa.gruul.order.api.model.CreateOrderDto;
import com.medusa.gruul.order.api.model.OrderOverviewVo;
import com.medusa.gruul.order.api.model.OrderVo;
import com.medusa.gruul.order.model.*;
import com.medusa.gruul.order.service.IMiniOrderService;
import com.medusa.gruul.order.service.IOrderDeliveryService;
import com.medusa.gruul.order.service.IOrderProductEvaluateService;
import com.medusa.gruul.order.service.IOrderSettingService;
import com.medusa.gruul.payment.api.model.dto.PayResultDto;
import com.medusa.gruul.platform.api.feign.RemoteMiniInfoService;
import com.medusa.gruul.platform.api.model.PlatformConstant;
import com.medusa.gruul.platform.api.model.dto.ShopInfoDto;
import com.medusa.gruul.platform.api.model.vo.MiniMsgVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户小程序端
 * </p>
 *
 * @author alan
 * @since 2023-09-02
 */
@Slf4j
@RestController
@RequestMapping("/mini")
@Tag(name = "用户小程序端订单接口")
public class MiniOrderController {
    @Resource
    private IMiniOrderService miniOrderService;
    @Resource
    private IOrderSettingService orderSettingService;
    @Resource
    private RemoteGoodsService remoteGoodsService;
    @Resource
    private IOrderDeliveryService orderDeliveryService;
    @Resource
    private IOrderProductEvaluateService orderEvaluateService;
    @Resource
    private RemoteMiniInfoService remoteMiniInfoService;
    @Resource
    private RemoteMiniAccountService remoteMiniAccountService;

    /**
     * 获取结算页信息
     *
     * @param dto the dto
     * @return Result<ConfirmOrderVo>
     * @Author alan
     * @Date 2023/10/6 12:57
     */
    @Operation(summary = "获取结算页信息")
    @PostMapping("/confirm_order")
    public Result<ConfirmOrderVo> getConfirmOrder(@RequestBody @Valid ConfirmOrderDto dto) {
        ConfirmOrderVo confirmOrderVo = miniOrderService.getConfirmOrder(dto);
        return Result.ok(confirmOrderVo);
    }

    /**
     * 重新获取运费
     *
     * @param dto the dto
     * @return Result<CountCostDto>
     * @author alan
     * @date 2024/1/4 21:16
     */
    @Operation(summary = "获取运费")
    @PostMapping("/freightAmount")
    public Result<CountCostDto> getFreightAmount(@RequestBody GetCostDto dto) {
        CountCostDto result = miniOrderService.getFreightAmount(dto);
        return Result.ok(result);
    }

    /**
     * 创建订单
     *
     * @param createOrderDto the create order dto
     * @return Result<String>
     * @author alan
     * @date 2023/10/24 22:04
     */
    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public Result<String> createOrder(@RequestBody @Valid CreateOrderDto createOrderDto) {
        String orderId = miniOrderService.preCheckOrder(createOrderDto);
        return Result.ok(orderId);
    }

    /**
     * 查询订单创建情况
     *
     * @param dto the dto
     * @return Result<Object>
     * @author alan
     * @date 2023/11/23 11:04
     */
    @Operation(summary = "查询订单创建情况", description = "-1->已售罄;0->排队中;order->成功;")
    @PostMapping("/result")
    public Result<Object> getOrderResult(@RequestBody GroupOrderResultDto dto) {
        Order order = miniOrderService.getById(dto.getOrderId());
        if (order != null) {
            return Result.ok(order);
        } else {
            Integer result = miniOrderService.checkOrder(dto);
            return Result.ok(result);
        }
    }

    /**
     * 用户订单概况
     *
     * @param
     * @return Result<OrderOverviewVo>
     * @author alan
     * @date 2024/2/19 21:53
     */
    @Operation(summary = "用户订单概况", description = "用户订单概况")
    @GetMapping("/overview")
    public Result<OrderOverviewVo> orderOverview() {
        OrderOverviewVo orderOverviewVo = miniOrderService.orderOverview();
        return Result.ok(orderOverviewVo);
    }

    /**
     * 用户订单列表
     *
     * @param dto the dto
     * @return Result<PageUtils<ApiOrderVo>>
     * @author alan
     * @date 2023/11/25 21:42
     */
    @Operation(summary = "用户查询订单", description = "用户查询订单")
    @GetMapping("/search")
    public Result<PageUtils<ApiOrderVo>> searchOrder(ApiSearchOrderDto dto) {
        PageUtils<ApiOrderVo> page = miniOrderService.searchOrder(dto);
        return Result.ok(page);
    }

    /**
     * 用户查询订单详情
     *
     * @param orderId the order id
     * @return Result<OrderVo>
     * @author alan
     * @date 2023/11/25 22:32
     */
    @Operation(summary = "用户查询订单详情", description = "用户查询订单详情")
    @GetMapping("/info/{orderId}")
    public Result<OrderVo> orderInfo(@PathVariable(value = "orderId") @NotNull Long orderId) {
        OrderVo orderVo = miniOrderService.orderInfo(orderId);
        OrderSetting orderSetting = orderSettingService.getOne(null);
        orderVo.setOpenEvaluate(orderSetting.getOpenEvaluate());
        return Result.ok(orderVo);
    }

    /**
     * 小程序查询晒单内容
     *
     * @param orderId the order id
     * @return Result<OrderShareInfo>
     */
    @Operation(summary = "小程序查询晒单内容", description = "晒单设置")
    @GetMapping("/share/{orderId}")
    public Result<OrderShareInfo> orderShareInfo(@PathVariable(value = "orderId") @NotNull Long orderId) {
        OrderShareInfo orderShareInfo = miniOrderService.orderShareInfo(orderId);
        return Result.ok(orderShareInfo);
    }

    /**
     * 用户取消订单
     *
     * @param orderId the order id
     * @return Result<Void>
     * @author alan
     * @date 2023/11/20 20:32
     */
    @Operation(summary = "用户取消订单", description = "用户取消订单")
    @PutMapping("/cancel/{orderId}")
    public Result<Void> cancelOrder(@PathVariable(value = "orderId") @NotNull Long orderId) {
        miniOrderService.cancelOrder(orderId);
        return Result.ok();

    }

    /**
     * 用户支付订单
     *
     * @param orderId     the order id
     * @param userBalance
     * @param request     the request
     * @return Result<PayResultDto>
     * @author alan
     * @date 2023/11/20 20:39
     */
    @Operation(summary = "用户支付订单", description = "用户支付订单")
    @PutMapping("/pay/{orderId}")
    public Result<PayResultDto> payOrder(@PathVariable(value = "orderId") @NotNull Long orderId,
                                         @RequestParam(value = "userBalance") Boolean userBalance, HttpServletRequest request) {
        return Result.ok(miniOrderService.payOrder(orderId, userBalance, request));

    }

    /**
     * 修改收货人地址
     *
     * @param dto the dto
     * @return Result<Void>
     * @author alan
     * @date 2024/7/3 20:30
     */
    @Operation(summary = "小程序修改收货地址", description = "小程序修改收货地址")
    @PutMapping("/receiver/address")
    public Result<Void> updateReceiverAddress(@RequestBody @Validated ReceiverAddressDto dto) {
        orderDeliveryService.updateReceiverAddress(dto);
        return Result.ok();
    }

    /**
     * 用户确认收货
     *
     * @param orderId  the order id
     * @return Result<Void>
     * @author alan
     * @date 2023/11/20 22:07
     */
    @Operation(summary = "用户确认收货", description = "用户确认收货")
    @PutMapping("/receipt/{orderId}")
    public Result<Void> receiptOrder(@PathVariable(value = "orderId") @NotNull Long orderId) {
        miniOrderService.receiptOrder(orderId, false);
        return Result.ok();
    }

    /**
     * 是否开启评论
     *
     * @return Result<Boolean>
     * @author alan
     * @date 2023/11/20 22:07
     */
    @PreAuthorize("permitAll()")
    @Deprecated
    @Operation(summary = "是否开启评论", description = "是否开启评论")
    @GetMapping("/evaluate/setting")
    public Result<Boolean> evaluateSetting() {
        OrderSetting orderSetting = orderSettingService.getOne(null);
        return Result.ok(orderSetting.getOpenEvaluate());
    }

    /**
     * 是否开启评论
     *
     * @return Result<SettingVo>
     * @author alan
     * @date 2023/11/20 22:07
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "是否开启评论", description = "是否开启评论")
    @GetMapping("/setting")
    public Result<SettingVo> setting() {
        SettingVo settingVo = new SettingVo();
        //获取当前线程中的店铺id的店铺信息;getTemplateCodeEnum 获取当前店铺使用模板类型
        OrderSetting orderSetting = orderSettingService.getOne(null);
        settingVo.setOpenEvaluate(orderSetting.getOpenEvaluate());
        settingVo.setSetting(orderSetting);
        //获取当前店铺可使用的小程序订阅模板
        List<MiniMsgVo> miniMsgVoList = remoteMiniInfoService.getCurrentMiniMsg();
        settingVo.setMiniMsgVoList(miniMsgVoList);
        //查询返利设置
        String shopId = ISystem.shopId();
        RebateSet rebateSet = remoteGoodsService.getRebateSet(shopId);
        if (ObjectUtil.isNotNull(rebateSet)) {
            settingVo.setRebateSet(rebateSet);
        }
        //查询会员是否开启
        Integer openMember = remoteMiniAccountService.memberSwitch(shopId);
        settingVo.setOpenMember(openMember);
        settingVo.setCurShopInfoDto(
                RedisUtil.getCacheMap(RedisUtil.key(PlatformConstant.SHOP_INFO_CACHE_PREFIX, shopId), ShopInfoDto.class)
        );
        return Result.ok(settingVo);
    }

    /**
     * 用户评价订单
     *
     * @param dto the dto
     * @return Result<Void>
     * @author alan
     * @date 2023/11/25 21:41
     */
    @Operation(summary = "用户评价订单", description = "用户评价订单")
    @PostMapping("/evaluate")
    public Result<Void> evaluateOrder(@RequestBody @Validated ApiOrderEvaluateDto dto) {
        miniOrderService.evaluateOrder(dto);
        return Result.ok();
    }

    /**
     * 用户查看评价列表
     *
     * @param dto the dto
     * @return Result<PageUtils<ApiOrderEvaluateVo>>
     * @author alan
     * @date 2023/12/4 20:20
     */
    @Operation(summary = "用户查看评价列表", description = "用户查看评价列表")
    @GetMapping("/evaluate")
    public Result<PageUtils<ManageEvaluateVo>> evaluateOrder(ApiSearchEvaluateDto dto) {
        PageUtils<ManageEvaluateVo> page = miniOrderService.searchOrderEvaluate(dto);
        return Result.ok(page);
    }

    /**
     * 商品详情页评价概况
     *
     * @param productId the product id
     * @return Result<UserEvaluateVo>
     * @author alan
     * @date 2024/1/18 21:49
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "商品详情页评价概况", description = "商品详情页评价概况")
    @GetMapping("/product/evaluate/overview")
    public Result<UserEvaluateVo> userEvaluateOverview(@RequestParam(value = "productId") @NotBlank String productId) {
        UserEvaluateVo vo = orderEvaluateService.userEvaluateOverview(productId);
        return Result.ok(vo);
    }

    /**
     * 商品详情页评价
     *
     * @param dto the dto
     * @return Result<PageUtils<ProductEvaluateVo>>
     * @author alan
     * @date 2024/1/18 22:14
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "商品详情页评价", description = "商品详情页评价")
    @GetMapping("/product/evaluate")
    public Result<PageUtils<ProductEvaluateVo>> productEvaluate(ApiSearchProductEvaluateDto dto) {
        PageUtils<ProductEvaluateVo> page = orderEvaluateService.productEvaluate(dto);
        return Result.ok(page);
    }

    /**
     * 获取接龙活动最近的买家
     *
     * @param solitaireActivityId the solitaire activity id
     * @return the list
     */
    @Operation(summary = "获取接龙活动最近的买家")
    @PreAuthorize("permitAll()")
    @GetMapping("/solitaire/lately/buyer")
    public Result<List<LatelyBuyerVo>> solitaireLatelyBuyer(@RequestParam(value = "solitaireActivityId") @NotNull Integer solitaireActivityId) {
        List<LatelyBuyerVo> vos = miniOrderService.solitaireLatelyBuyer(solitaireActivityId);
        return Result.ok(vos);
    }

    /**
     * 获取接龙活动买家
     *
     * @param dto the dto
     * @return Result<PageUtils<SolitaireBuyerVo>>
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取接龙活动买家")
    @GetMapping("/solitaire/buyer")
    public Result<PageUtils<SolitaireBuyerVo>> solitaireBuyerPage(SolitaireBuyerDto dto) {
        PageUtils<SolitaireBuyerVo> pageUtils = miniOrderService.solitaireBuyerPage(dto);
        return Result.ok(pageUtils);
    }

    /**
     * 获取我的接龙活动订单
     *
     * @param solitaireActivityId the solitaire activity id
     * @return Result<List<SolitaireBuyerVo>>
     */
    @Operation(summary = "获取我的接龙活动订单")
    @GetMapping("/solitaire/my")
    public Result<List<SolitaireBuyerVo>> mySolitaireOrder(@RequestParam(value = "solitaireActivityId") @NotNull Integer solitaireActivityId) {
        List<SolitaireBuyerVo> vo = miniOrderService.mySolitaireOrder(solitaireActivityId);
        return Result.ok(vo);
    }
}
