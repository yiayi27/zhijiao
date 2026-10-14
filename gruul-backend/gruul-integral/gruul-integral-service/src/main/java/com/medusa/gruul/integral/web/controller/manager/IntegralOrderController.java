package com.medusa.gruul.integral.web.controller.manager;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.account.api.feign.RemoteMiniAccountService;
import com.medusa.gruul.account.api.model.AccountInfoDto;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.core.util.SystemCode;

import com.medusa.gruul.integral.service.IIntegralOrderService;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralOrderDto;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralReceivingInfoDto;
import com.medusa.gruul.integral.web.model.param.manager.IntegralDeliveryFiltrateParam;
import com.medusa.gruul.integral.web.model.param.manager.IntegralGoodsOrderParam;
import com.medusa.gruul.integral.web.model.vo.IntegralGoodsOrderVo;
import com.medusa.gruul.logistics.api.feign.RemoteLogisticsFeginService;
import com.medusa.gruul.logistics.model.vo.LogisticsTemplateVo;
import com.medusa.gruul.payment.api.model.dto.PayResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 积分订单表 前端控制器
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@RestController
@RequestMapping("/manager/integralOrder")
@Tag(name = "积分订单相关接口")
public class IntegralOrderController {
    @Autowired
    private IIntegralOrderService integralOrderService;

    @Resource
    private RemoteLogisticsFeginService remoteLogisticsFeginService;

    @Resource
    private RemoteMiniAccountService remoteMiniAccountService;


    /**
     * 积分商品订单获取
     *
     * @param integralGoodsOrderParam 查询条件
     * @return 符合查询条件的积分商品订单(分页)
     */
    @Operation(summary = "积分商品订单")
    @GetMapping("get/integralOrder")
    public Result<PageUtils<IntegralGoodsOrderVo>> searchIntegralOrder(IntegralGoodsOrderParam integralGoodsOrderParam) {
        PageUtils<IntegralGoodsOrderVo> page = new PageUtils<>(integralOrderService.searchOrders(integralGoodsOrderParam));
        return Result.ok(page);
    }


    /**
     * 积分商品订单信息
     *
     * @param orderId 积分商品订单id
     * @return 积分商品订单信息
     */
    @Operation(summary = "单个订单详细信息")
    @GetMapping("get/integralOrderInfo")
    public Result<IntegralGoodsOrderVo> getIntegralOrderInfo(@Parameter(description = "订单id", required = true) Long orderId) {
        IntegralGoodsOrderVo integralOrderInfo = integralOrderService.getIntegralOrderInfo(orderId);
        return Result.ok(integralOrderInfo);
    }


    /**
     * 商家批量确认收货功能
     *
     * @param orderIds 订单ids
     * @return
     */
    @Operation(summary = "批量商家确定收货")
    @PutMapping("receive")
    public Result<Void> goodsOrderReceive(@RequestBody Long[] orderIds) {
        integralOrderService.receive(Arrays.asList(orderIds));
        return Result.ok();
    }


    /**
     * 对订单进行批量备注
     *
     * @param integralOrders 订单ids
     * @param backRemark     备注内容
     * @param isCoverage     是否覆盖
     * @return
     */
    @Operation(summary = "订单批量备注功能")
    @PutMapping("update/orderRemark")
    public Result<Void> updateRemark(@RequestBody Long[] integralOrders,
                               @RequestParam(value = "backRemark") String backRemark,
                               @RequestParam(value = "isCoverage") Boolean isCoverage) {
        integralOrderService.remarkIntegralOrder(Arrays.asList(integralOrders), backRemark, isCoverage);
        return Result.ok();
    }


    /**
     * 创建积分订单 (小程序端提交积分商品订单)
     *
     * @param integralOrderDto 积分商品提交订单数据
     * @return 积分商品订单id 用于对后续是否支付成功进行操作
     */
    @Operation(summary = "创建积分商品订单")
    @PostMapping("save")
    public Result<String> saveIntegralOrder(@RequestBody @Validated IntegralOrderDto integralOrderDto) {
        SecureUser feignCurUser = ISecurity.userMust();
        String userId = feignCurUser.getId();
        String integralOrderId = "";
        if (StrUtil.isNotEmpty(userId)) {
            integralOrderDto.setUserId(userId);
        }
        if (!integralOrderDto.getOrderType()) {
            integralOrderId = integralOrderService.addIntegralOrder(integralOrderDto);
        }
        return Result.ok(integralOrderId);
    }


    /**
     * 用户支付订单
     *
     * @param prizeOrderId 积分订单id
     * @param request      request
     * @return
     */
    @Operation(summary = "用户支付积分订单")
    @PutMapping("/payIntegralOrder/{prizeOrderId}")
    public Result<PayResultDto> payIntegralOrder(@PathVariable(value = "prizeOrderId") @NotNull Long prizeOrderId,
                                                 HttpServletRequest request) {

        return Result.ok(integralOrderService.payIntegralOrder(prizeOrderId, request));
    }

    @Operation(summary = "用户支付积分订单/h5")
    @PutMapping("/payIntegralOrder/page/{prizeOrderId}")
    public Result<?> payIntegralOrderPage(@PathVariable(value = "prizeOrderId") @NotNull Long prizeOrderId) {
        return Result.ok(
                integralOrderService.payIntegralOrderPage(prizeOrderId)
        );
    }

    /**
     * 积分订单完成  完成修改订单状态为未发货  失败 归还积分 生成明细 删除订单
     *
     * @param isSucceed    是否成功
     * @param prizeOrderId 订单id
     * @return
     */
    @Operation(summary = "积分订单完成")
    @GetMapping("done")
    public Result<Void> integralOrderDone(@Parameter(description = "是否成功", required = true) Boolean isSucceed,
                                    @Parameter(description = "订单id", required = true) Long prizeOrderId) {
        if (isSucceed) {
            //完成订单
            integralOrderService.integralOrderDone(prizeOrderId);
        } else {
            //删除订单 并归还用户积分
            integralOrderService.delIntegralOrder(prizeOrderId);
        }
        return Result.ok();
    }

    /**
     * 获取用户收货地址
     *
     * @return 用户收货地址
     */
    @Operation(summary = "用户收货地址")
    @GetMapping("get/address")
    public Result<AccountInfoDto> shippingAddress() {
        SecureUser secureUser = ISecurity.userMust();
        if (ObjectUtil.isNull(secureUser)) {
            throw new ServiceException("用户信息已经过期,请登陆后重试", SystemCode.FAILURE_CODE);
        }
        AccountInfoDto accountInfoDto = remoteMiniAccountService.accountInfo(secureUser.getId(), List.of(3));
        return Result.ok(accountInfoDto);
    }

    /**
     * 获取运费(获取运费模板 判断运费计算方式
     * 按件计费(该地区首件金额) or 不满足
     * 按重量计费(商品总量 * 该区域每公斤重量金额) or 不满足
     * 包邮(0.01) or 不满足)
     *
     * @param regionCode        用户收货地址code
     * @param freightTemplateId 运费模板id
     * @param weight            商品重量
     * @return 运费金额
     */
    @Operation(summary = "获取运费金额")
    @GetMapping("get/FreightAmount")
    public Result<BigDecimal> getFreightAmount(@RequestParam(value = "regionCode") String regionCode,
                                   @RequestParam(value = "freightTemplateId") Long freightTemplateId,
                                   @RequestParam(value = "weight") BigDecimal weight) {
        SecureUser secureUser = ISecurity.userMust();
        if (ObjectUtil.isNull(secureUser)) {
            throw new ServiceException("用户信息已经过期,请登陆后重试", SystemCode.FAILURE_CODE);
        }
        //获取用户id
        String userId = secureUser.getId();
        //判断用户是否是会有并且拥有免运费权益
        Boolean flag = integralOrderService.memberInfo(userId);
        BigDecimal freight = BigDecimal.ZERO;
        //用户不是会员或者没有包邮权益时 邮费走正常流程
        if (!flag) {
            freight = integralOrderService.freight(regionCode, freightTemplateId, weight);
        }
        return Result.ok(freight);
    }

    /**
     * pc端 积分商品订单进行发货
     *
     * @return
     */
    @Operation(summary = "积分商品订单发货")
    @PutMapping("orderDelivery")
    public Result<Void> goodsOrderDelivery(@RequestBody List<IntegralGoodsOrderVo> integralGoodsOrderVos) {
        integralOrderService.shipments(integralGoodsOrderVos);
        return Result.ok();
    }


    /**
     * 根据运单号查询物流轨迹
     *
     * @param waybillNo   运单号
     * @param shipperType 快递公司
     * @return 物流轨迹
     */
    @Operation(summary = "根据运单号查询物流轨迹", description = "根据运单号查询物流轨迹")
    @GetMapping("/traces/info")
    public Result<Object> getGoodsOrderLogisticsDetails(@RequestParam(value = "waybillNo") @NotNull String waybillNo,
                                                                     @RequestParam(value = "shipperType") @NotNull String shipperType,
                                                                     @RequestParam(value = "mobile") @NotNull String mobile) {
        return Result.ok(remoteLogisticsFeginService.getLogisticsExpressInfo(waybillNo, shipperType, mobile).getData());
    }


    /**
     * 根据查询条件筛选出符合条件的订单
     *
     * @param integralDeliveryFiltrateParam 查询条件
     * @return
     */
    @Operation(summary = "对批量发货订单进行筛选", description = "对批量发货订单进行筛选")
    @GetMapping("/goodsOrder/filtrate")
    public Result<List<IntegralGoodsOrderVo>> deliveryFiltrateList(IntegralDeliveryFiltrateParam integralDeliveryFiltrateParam) {
        List<IntegralGoodsOrderVo> filtrate = integralOrderService.filtrate(integralDeliveryFiltrateParam);
        return Result.ok(filtrate);
    }


    /**
     * 积分订单无需物流发货 只改变订单状态
     *
     * @param integralOrders 积分订单 ids
     * @return
     */
    @Operation(summary = "无需物流发货", description = "无需物流发货")
    @PutMapping("/noLogistics")
    public Result<Void> noLogistics(@RequestBody Long[] integralOrders) {
        integralOrderService.updateIntegralOrderStatus(Arrays.asList(integralOrders));
        return Result.ok();
    }

    /**
     * 获取全部待发货的订单
     *
     * @return
     */
    @Operation(summary = "获取所有待发货订单", description = "获取所有待发货订单")
    @GetMapping("get/allWaitForSend")
    public Result<List<IntegralGoodsOrderVo>> allWaitForSend() {
        List<IntegralGoodsOrderVo> allWaitForSendIntegralOrder = integralOrderService.getAllWaitForSendIntegralOrder();
        return Result.ok(allWaitForSendIntegralOrder);
    }

    /**
     * 修改订单收货人信息
     *
     * @param integralReceivingInfoDto
     * @return
     */
    @PutMapping("/update/ReceivingInfo")
    @Operation(summary = "积分订单收货人信息修改")
    public Result<Void> updateReceivingInfo(@RequestBody IntegralReceivingInfoDto integralReceivingInfoDto) {
        integralOrderService.updateReceivingInfo(integralReceivingInfoDto);
        return Result.ok();
    }


}


