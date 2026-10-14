package com.medusa.gruul.integral.web.remote;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.medusa.gruul.account.api.model.IntegraChangelDto;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.integral.api.entity.IntegralShop;
import com.medusa.gruul.integral.api.model.*;
import com.medusa.gruul.integral.mq.Sender;
import com.medusa.gruul.integral.service.*;
import com.medusa.gruul.integral.web.enums.IntegralOperationTypeEnum;
import com.medusa.gruul.integral.web.model.dto.manager.IntegralShopDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: 提供积分商家设置的积分折扣规则
 * @Author: xiaoq
 * @Date: 2023/12/7 16:11
 */
@RestController(value = "remoteIntegralService")
@RequestMapping("/")
@Slf4j
public class RemoteIntegralService {
    @Autowired
    private IIntegralShopService integralShopService;

    @Autowired
    private IIntegralDetailService integralDetailService;

    @Autowired
    private IIntegralCouponOrderService integralCouponOrderService;

    @Autowired
    private IIntegralRuleService integralRuleService;

    @Autowired
    private IIntegralOrderService integralOrderService;

    @Autowired
    private Sender sender;

    /**
     * 获取商家设置的抵扣规则
     *
     * @param shopId 商家店铺id
     * @return com.medusa.gruul.integral.api.model.DiscountInfoDto
     */
    @GetMapping(value = "discountInfo")
    @Operation(summary = "获取商家积分抵扣规则,已交由finance模块维护")
    @PreAuthorize("permitAll()")
    public DiscountInfoDto discountInfo(
            @RequestParam(value = "shopId", required = true) @NotNull(message = "shopId不能为空") String shopId) {
        return integralShopService.getDiscountInfo(shopId);
        //该数据已交由财务模块通用设置维护
    }


    /**
     * 积分归还 部分订单取消可进行积分归还 如:超时未支付
     *
     * @param userId        用户Id
     * @param integralValue 归还的积分值
     * @return
     */
    @GetMapping(value = "giveBack")
    @Operation(summary = "归还")
    @PreAuthorize("permitAll()")
    public Boolean integralReturn(
            @RequestParam(value = "userId") @NotNull(message = "用户id不能为null") String userId,
            @RequestParam(value = "integralValue") @NotNull(message = "积分值不能为空") BigDecimal integralValue) {

        String shopId = ISystem.shopId();
        log.info("input {}用户id: + {}积分值: ", userId, integralValue);
        //jdk 1.8以后 该方法过时
        integralValue = integralValue.setScale(2, RoundingMode.HALF_UP);

        //生成积分明细记录
        if (integralValue.compareTo(BigDecimal.ZERO) != 0) {
            Boolean flag = integralDetailService.addIntegralDetailRecord(userId, integralValue);
            if (!flag) {
                return Boolean.FALSE;
            }
            //用户扣除积分dto 封装
            IntegraChangelDto integraChangelDto = new IntegraChangelDto();
            integraChangelDto.setOption(IntegralOperationTypeEnum.INCREASE.getStatus());
            integraChangelDto.setUserId(userId);
            integraChangelDto.setValue(integralValue);
            integraChangelDto.setShopId(shopId);
            sender.sendIntegralDeductionMessage(integraChangelDto);
            return Boolean.TRUE;
        }
        return Boolean.TRUE;
    }


    /**
     * 积分扣减 使用积分 进行扣减
     *
     * @param userId
     * @param integralValue
     */
    @GetMapping(value = "deduction")
    @Operation(summary = "积分扣减 -- 扣除积分是强事物性的 交由用户模块进行操作(扣除积分为用户模块,该处只生成扣减积分明细)")
    @PreAuthorize("permitAll()")
    public Boolean integralDeduction(
            @RequestParam(value = "userId") @NotNull(message = "用户id不能为null") String userId,
            @RequestParam(value = "integralValue") @NotNull(message = "积分值不能为空") BigDecimal integralValue,
            @RequestParam(value = "shopId") @NotNull(message = "shopId不能为空") String shopId) {

        if (integralValue.compareTo(BigDecimal.ZERO) == 1) {
            //生成积分明细记录
            Boolean flag = integralDetailService.subtractIntegralDetailRecord(userId, integralValue,shopId);
            if (!flag) {
                return flag;
            }
            //扣除积分是强事物性的 交由用户模块进行操作
        }
        return Boolean.TRUE;
    }


    /**
     * 兑换积分优惠券
     *
     * @param integralCouponDto 生成订单所需值
     * @return 是否成功
     */
    @PutMapping(value = "conversion")
    @Operation(summary = "兑换积分优惠劵,数值由discount 模块提供")
    @PreAuthorize("permitAll()")
    public Result<Integer> conversionIntegralCoupon(@RequestBody IntegralCouponDto integralCouponDto) {
        //.生成优惠券订单 并通知用户扣积分
        return integralCouponOrderService.addIntegralCouponOrder(integralCouponDto);
    }


    /**
     * 生成积分商城所需 默认值
     *
     * @param shopId 商铺id
     * @return 是否成功
     */
    @PostMapping(value = "generate/default/values")
    @Operation(summary = "生成积分默认值,创建店铺时使用")
    @PreAuthorize("permitAll()")
    @Transactional(rollbackFor = Exception.class)
    Boolean integralDefault(
            @RequestParam(value = "shopId", required = true) @NotNull(message = "shopId不能为空") String shopId) {

        BigDecimal ceiling = new BigDecimal(3);
        BigDecimal ratio = new BigDecimal(2);
        IntegralShopDto integralShopDto = new IntegralShopDto();
        integralShopDto.setCeiling(ceiling);
        integralShopDto.setRatio(ratio);
        integralShopDto.setShopId(shopId);
        integralShopDto.setType(false);
        IntegralShop integralShop = integralShopDto.coverIntegralShop();
        integralShop.setCreateTime(LocalDateTime.now());
        integralShop.setUpdateTime(LocalDateTime.now());
        integralShop.setDeleted(false);
        integralShopService.save(integralShop);

        return ISystem.switchTenant(shopId, () -> integralRuleService.saveIntegralDefaultRule(integralShop.getId(), shopId));
    }

    /**
     * 获取商城信息 积分商城状态 签到状态 步数状态
     *
     * @param
     * @return map<" 名称 ", " 开启状态 ">
     */
    @GetMapping("functionInfo")
    @Operation(summary = "获取积分默认值")
    @PreAuthorize("permitAll()")
    public ShopFunctionInfoVo getShoppingFunctionInfo(String shopId) {
        return integralShopService.getShopFunctionInfo(shopId);
    }


    /**
     * 获取用户信息
     *
     * @return
     */
    @PostMapping("get/userInfo/{shopUserId}")
    @Operation(summary = "用户信息 ,提供数据供聚合接口使用")
    @PreAuthorize("permitAll()")
    public UserInfoVo getUserInfo(@PathVariable(value = "shopUserId") String shopUserId) {
        return integralDetailService.getUserInfo(shopUserId);
    }

    /**
     * 积分订单打印并发货
     *
     * @param integralOrderDelivery
     * @return
     */
    @Operation(summary = "积分订单打印并发货")
    @GetMapping("print/delivery")
    @PreAuthorize("permitAll()")
    public int doLogisticsIntegralOrderDelivery(
            @RequestParam(value = "integralOrderDelivery") String integralOrderDelivery) {
        List<IntegralOrderDeliveryDto> integralOrderDeliveryDtos = JSONUtil
                .toList(integralOrderDelivery, IntegralOrderDeliveryDto.class);
        if (CollectionUtil.isEmpty(integralOrderDeliveryDtos)) {
            return 0;
        }
        return integralOrderService.doLogisticsIntegralOrderDelivery(integralOrderDeliveryDtos);
    }

    /**
     * 根据积分订单id获取订单信息 vo
     *
     * @param integralOrderId
     * @return
     */
    @GetMapping("get/integralOrderInfo")
    @Operation(summary = "根据积分订单id获取订单信息")
    @PreAuthorize("permitAll()")
    public IntegralOrderVo integralOrderInfo(
            @RequestParam(value = "integralOrderId", required = true) @NotNull Long integralOrderId) {
        return integralOrderService.integralOrderInfo(integralOrderId);
    }


    /**
     * 使用积分 兑换会员卡
     *
     * @param shopUserId    用户id
     * @param integralValue 积分值
     * @return
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "使用积分,兑换会员卡")
    @PostMapping("conversion/clubCard")
    public Result<Integer> conversionClubCard(
            @RequestParam(value = "shopUserId", required = true) @NotNull(message = "用户id不能为null") String shopUserId,
            @RequestParam(value = "integralValue", required = true) @NotNull(message = "积分值不能为空") BigDecimal integralValue) {
        return integralDetailService.useIntegralConversionClubCard(shopUserId, integralValue);
    }
}


