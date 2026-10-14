package com.medusa.gruul.order.controller.manage;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.core.constant.Keys;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.logistics.api.feign.RemoteLogisticsFeginService;
import com.medusa.gruul.order.api.entity.OrderSetting;
import com.medusa.gruul.order.api.entity.OrderShareSetting;
import com.medusa.gruul.order.api.model.OrderVo;
import com.medusa.gruul.order.model.*;
import com.medusa.gruul.order.service.IManageOrderService;
import com.medusa.gruul.order.service.IOrderDeliveryService;
import com.medusa.gruul.order.service.IOrderSettingService;
import com.medusa.gruul.order.service.IOrderShareSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * PC端
 *
 * @author alan
 * @date 2023/11/13 20:20
 */
@Slf4j
@RestController
@RequestMapping("/manage")
@Tag(name = "PC端订单接口")
public class ManageOrderController {
    private static final String S_NAME = "{sname}";
    @Resource
    private IManageOrderService orderService;
    @Resource
    private IOrderSettingService orderSettingService;
    @Resource
    private IOrderShareSettingService orderShareSettingService;
    @Resource
    private IOrderDeliveryService orderDeliveryService;
    @Resource
    private RemoteLogisticsFeginService remoteLogisticsFeginService;

    /**
     * PC管理端订单列表
     *
     * @param dto the dto
     * @return com.medusa.gruul.common.core.util.PageUtils page utils
     * @author alan
     * @date 2023/11/20 20:04
     */
    @Operation(summary = "商家查询订单", description = "商家查询订单")
    @GetMapping("/search")
    public Result<PageUtils<ManageOrderVo>> searchOrder(@Validated ManageSearchOrderDto dto) {
        PageUtils<ManageOrderVo> page = orderService.searchOrder(dto);
        return Result.ok(page);
    }

    /**
     * 查询激励佣金详情
     *
     * @param dto the dto
     * @return com.medusa.gruul.common.core.util.PageUtils page utils
     * @author alan
     * @date 2024/6/19 21:23
     */
    @Operation(summary = "查询激励佣金详情", description = "查询激励佣金详情")
    @GetMapping("/incentive/search")
    public Result<PageUtils<ManageOrderVo>> searchIncentiveDetail(@Validated IncentiveDetailDto dto) {
        PageUtils<ManageOrderVo> page = orderService.searchIncentiveDetail(dto);
        return Result.ok(page);
    }

    /**
     * 控制台订单数据概况
     *
     * @param
     * @return com.medusa.gruul.order.model.ManageOrderOverviewVo overview
     * @author alan
     * @date 2024/5/6 19:10
     */
    @Operation(summary = "商家查询订单", description = "商家查询订单")
    @GetMapping("/overview")
    public Result<ManageOrderOverviewVo> getOverview() {
        ManageOrderOverviewVo vo = orderService.getOverview();
        return Result.ok(vo);
    }

    /**
     * 是否开启评论
     *
     * @return Result
     */
    @Operation(summary = "是否开启评论", description = "是否开启评论")
    @GetMapping("/evaluate/setting")
    public Result<Boolean> getSetting() {
        OrderSetting orderSetting = orderSettingService.getOne(null);
        return Result.ok(orderSetting.getOpenEvaluate());
    }

    /**
     * 设置是否开启评论
     *
     * @return Result<Void>
     */
    @Operation(summary = "设置是否开启评论", description = "设置是否开启评论")
    @PutMapping("/evaluate/setting")
    public Result<Void> setting(@RequestParam(value = "openEvaluate") Boolean openEvaluate) {
        OrderSetting orderSetting = orderSettingService.getOne(null);
        orderSetting.setOpenEvaluate(openEvaluate);
        orderSettingService.updateById(orderSetting);
        return Result.ok();
    }

    /**
     * 查询订单设置
     *
     * @return Result<OrderSetting>
     */
    @Operation(summary = "查询订单设置")
    @GetMapping("/setting")
    public Result<OrderSetting> getOrderSetting() {
        log.info(ISecurity.userMust().getShopId() + "shopId");
        OrderSetting orderSetting = orderSettingService.getOne(null);
        if (BeanUtil.isEmpty(orderSetting)) {
            return Result.ok(new OrderSetting());
        }
        return Result.ok(orderSetting);
    }

    /**
     * 更新订单设置
     *
     * @param setting the setting
     * @return Result<OrderSetting>
     */
    @Operation(summary = "更新订单设置")
    @PostMapping("/setting")
    public Result<OrderSetting> setOrderSetting(@RequestBody @NotNull @Validated OrderSetting setting) {
        OrderSetting orderSetting = orderSettingService.update(setting);
        return Result.ok(orderSetting);
    }

    /**
     * 获取当前分享晒单设置
     *
     * @return Result<OrderShareSetting>
     */
    @Operation(summary = "获取当前分享晒单设置", description = "晒单设置")
    @GetMapping("/share/setting")
    public Result<OrderShareSetting> getShareSetting() {
        OrderShareSetting orderShareSetting = orderShareSettingService.getOne(null);
        return Result.ok(orderShareSetting);
    }

    /**
     * 设置分享晒单设置
     *
     * @param vo 
     * @return Result<OrderShareSetting>
     */
    @Operation(summary = "设置分享晒单设置", description = "晒单设置")
    @PutMapping("/share/setting")
    public Result<OrderShareSetting> setShareSetting(@RequestBody @Validated OrderShareSettingVo vo) {

        if (StrUtil.count(vo.getTitle(), S_NAME) != 1) {
            throw new ServiceException("标题必须且只能包含一个收货人信息");
        }
        OrderShareSetting orderShareSetting = orderShareSettingService.lambdaQuery()
                .one();
        boolean notExists = orderShareSetting == null;
        if (notExists) {
            orderShareSetting = new OrderShareSetting();
            orderShareSetting.setBackground(vo.getBackground());
            orderShareSetting.setTitle(vo.getTitle());
        }
        if (notExists) {
            orderShareSettingService.save(orderShareSetting);
        } else {
            orderShareSettingService.lambdaUpdate()
                    .set(OrderShareSetting::getBackground, vo.getBackground())
                    .set(OrderShareSetting::getTitle, vo.getTitle())
                    .update();
        }
        RedisUtil.delete(RedisUtil.key(Keys.ORDER_SHARE_SETTING, ISystem.shopId()));
        return Result.ok(orderShareSetting);
    }

    /**
     * 根据快递公司代码与运单号获取快递物流信息
     *
     * @param tracesId   快递公司运单号
     * @param deliveryCode 快递公司code
     * @param mobile 收件人电话
     * @return Result
     */
    @Operation(summary = "根据运单号查询物流轨迹", description = "根据运单号查询物流轨迹")
    @GetMapping("/traces")
    public Result<Object> tracesInfo(@RequestParam(value = "tracesId") @NotNull String tracesId,
                             @RequestParam(value = "deliveryCode") @NotNull String deliveryCode,
                             @RequestParam(value = "mobile") @NotNull String mobile) {
        return Result.ok(remoteLogisticsFeginService.getLogisticsExpressInfo(tracesId, deliveryCode, mobile).getData());
    }

    /**
     * 商家查询订单详情
     *
     * @param orderId the order id
     * @return com.medusa.gruul.order.model.OrderVo order vo
     * @author alan
     * @date 2023/11/26 20:41
     */
    @Operation(summary = "商家查询订单详情", description = "用户查询订单详情")
    @GetMapping("/info/{orderId}")
    public Result<OrderVo> orderInfo(@PathVariable(value = "orderId") @NotNull Long orderId) {
        OrderVo orderVo = orderService.orderInfo(orderId);
        return Result.ok(orderVo);
    }

    /**
     * 商家批量关闭订单
     *
     * @param orderIds the order ids
     * @return void
     * @author alan
     * @date 2023/11/23 19:03
     */
    @Operation(summary = "商家批量关闭订单", description = "商家批量关闭订单")
    @PutMapping("/close")
    public Result<Void> closeOrder(@RequestBody Long[] orderIds) {
        orderService.closeOrder(Arrays.asList(orderIds));
        return Result.ok();
    }

    /**
     * 商家批量备注订单
     *
     * @param orderNoteVo
     * @return void
     * @author alan
     * @date 2023/11/23 19:03
     */
    @Operation(summary = "商家批量备注订单", description = "商家批量备注订单")
    @PutMapping("/note")
    public Result<Void> noteOrder(@RequestBody OrderNoteVo orderNoteVo) {
        // 字符串转Long数组
        List<Long> orderIdList = Arrays.stream(orderNoteVo.getOrderIds().split(","))
                .map(Long::parseLong)
                .toList();
        orderService.noteOrder(orderIdList, orderNoteVo.getNote(), orderNoteVo.getOver());
        return Result.ok();
    }

    /**
     * 修改收货人地址
     *
     * @param dto the dto
     * @return void
     * @author alan
     * @date 2024/7/3 20:30
     */
    @Operation(summary = "商家后台修改收货地址", description = "商家后台修改收货地址")
    @PutMapping("/receiver/address")
    public Result<Void> updateReceiverAddress(@RequestBody ReceiverAddressDto dto) {
        orderDeliveryService.updateReceiverAddress(dto);
        return Result.ok();
    }

    /**
     * 商家移出发货单
     *
     * @param orderIds the order ids
     * @return void
     * @author alan
     * @date 2023/11/23 19:03
     */
    @Operation(summary = "商家移出发货单", description = "商家移出发货单")
    @PutMapping("/remove/send_bill")
    public Result<Void> removeSendBill(@RequestBody Long[] orderIds) {
        orderService.removeSendBill(Arrays.asList(orderIds));
        return Result.ok();
    }

    /**
     * 商家移出发货单
     *
     * @param productIds the product ids
     * @return void
     * @author alan
     * @date 2024/6/17 22:08
     */
    @Operation(summary = "商家移出发货单", description = "商家移出发货单")
    @PutMapping("/remove/send_bill/product")
    public Result<Void> removeSendBillByProductIds(@RequestBody Long[] productIds) {
        orderService.removeSendBillByProductIds(Arrays.asList(productIds));
        return Result.ok();
    }

    /**
     * 商家查看评价
     *
     * @param dto the dto
     * @return com.medusa.gruul.common.core.util.PageUtils page utils
     * @author alan
     * @date 2023/12/3 20:06
     */
    @Operation(summary = "商家查看评价列表", description = "商家查看评价列表")
    @GetMapping("/evaluate/search")
    public Result<PageUtils<ManageEvaluateVo>> searchOrderEvaluate(@Validated ManageSearchEvaluateDto dto) {
        PageUtils<ManageEvaluateVo> page = orderService.searchOrderEvaluate(dto);
        return Result.ok(page);
    }

    /**
     * 商家设置评价为精选
     *
     * @param ids the ids
     * @return void
     * @author alan
     * @date 2023/11/23 19:03
     */
    @Operation(summary = "商家设置评价为精选", description = "商家设置评价为精选")
    @PutMapping("/choice/evaluate")
    public Result<Void> choiceEvaluate(@RequestBody Long[] ids) {
        orderService.choiceEvaluate(Arrays.asList(ids));
        return Result.ok();
    }

    /**
     * 商家取消设置评价为精选
     *
     * @param ids the as list
     * @return void
     * @author alan
     * @date 2023/12/2 21:16
     */
    @Operation(summary = "商家取消设置评价为精选", description = "商家取消设置评价为精选")
    @PutMapping("/unChoice/evaluate")
    public Result<Void> unChoiceEvaluate(@RequestBody Long[] ids) {
        orderService.unChoiceEvaluate(Arrays.asList(ids));
        return Result.ok();
    }

    /**
     * 商家回复评价
     *
     * @param id    the id
     * @param reply the reply
     * @return void
     * @author alan
     * @date 2023/11/23 19:03
     */
    @Operation(summary = "商家回复评价", description = "商家回复评价")
    @PutMapping("/reply/evaluate")
    public Result<Void> replyEvaluate(@RequestParam(value = "id") Long id, @RequestParam(value = "reply") String reply) {
        orderService.replyEvaluate(id, reply);
        return Result.ok();
    }

    /**
     * Search logistics order list page utils.
     *
     * @param manageLogisticsOrderDto the manage logistics order dto
     * @return the page utils
     * 物流批量发货订单列表
     * @param:id
     * @return: com.medusa.gruul.order.model.ManageDeliveryOrderVo
     * @throws:
     * @author wangpeng
     * @version : v1.0
     * @date 2024/3/6 7:33 下午
     */
    @Deprecated
    @Operation(summary = "快递订单搜索", description = "快递订单搜索")
    @GetMapping("/reply/searchDeliveryOrders")
    public Result<PageUtils<ManageDeliveryOrderVo>> searchLogisticsOrderList(ManageLogisticsOrderDto manageLogisticsOrderDto) {
        PageUtils<ManageDeliveryOrderVo> pageUtils = orderService.searchLogisticsOrderList(manageLogisticsOrderDto);
        return Result.ok(pageUtils);
    }

    /**
     * 快递订单搜索
     *
     * @param dto the dto
     * @return java.util.List<com.medusa.gruul.order.model.ManageOrderVo> list
     * @author alan
     * @date 2024/6/17 22:02
     */
    @Operation(summary = "快递订单搜索", description = "快递订单")
    @GetMapping("/search/logistics")
    public Result<List<ManageOrderVo>> searchLogisticsOrder(ManageSearchLogisticsOrderDto dto) {
        List<ManageOrderVo> list = orderService.searchLogisticsOrder(dto);
        return Result.ok(list);
    }

    /**
     * 查询是否有待发货的快递订单
     *
     * @param
     * @return Result<Integer>
     * @author alan
     * @date 2024/6/17 22:02
     */
    @Operation(summary = "查询是否有待发货的快递订单", description = "快递订单")
    @GetMapping("/logistics/wait/send")
    public Result<Integer> hasWaitSend() {
        Integer hasWaitSend = orderService.countLogisticsWaitSend();
        return Result.ok(hasWaitSend);
    }

    /**
     * 无需快递的物流发货
     *
     * @param dto the dto
     * @return void
     * @author alan
     * @date 2024/6/17 22:02
     */
    @Operation(summary = "无需快递的物流发货", description = "快递订单")
    @PutMapping("/logistics/send")
    public Result<Void> logisticsSend(@RequestBody ManageSearchLogisticsOrderDto dto) {
        orderService.logisticsSend(dto);
        return Result.ok();
    }
}
