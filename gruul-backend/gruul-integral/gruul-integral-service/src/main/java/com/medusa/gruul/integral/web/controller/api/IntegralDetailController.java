package com.medusa.gruul.integral.web.controller.api;


import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.integral.api.model.UserInfoVo;
import com.medusa.gruul.integral.service.IIntegralDetailService;
import com.medusa.gruul.integral.web.model.dto.IntegralDetailDto;
import com.medusa.gruul.integral.web.model.param.api.IntegralDetailParam;
import com.medusa.gruul.integral.web.model.vo.IntegralDetailVo;
import com.medusa.gruul.order.api.model.OrderVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * <p>
 * 积分明细表
 * 前端控制器
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@RestController
@RequestMapping("/detail")
@Tag(name = "积分明细接口")
public class IntegralDetailController {

    @Autowired
    private IIntegralDetailService integralDetailService;

    /**
     * 积分明细查看
     *
     * @param integralDetailParam 分页对象
     * @return 分页返回的数据
     */
    @GetMapping("/get")
    @Operation(summary = "积分明细获取")
    public Result<PageUtils<IntegralDetailVo>> getIntegralDetail(IntegralDetailParam integralDetailParam) {
        ISecurity.userOpt()
                .ifPresent(
                        user -> integralDetailParam.setUserId(user.getId())
                );
        PageUtils<IntegralDetailVo> pageUtils = new PageUtils<>(integralDetailService.selectIntegralDetail(integralDetailParam));
        return Result.ok(pageUtils);
    }


    /**
     * 删除积分明细记录
     *
     * @param integralDetailId 积分明细id
     * @return Result<Void>
     */
    @DeleteMapping("delete")
    @Operation(summary = "删除积分明细记录,未对接扣除积分")
    public Result<Void> deleteDetail(Long integralDetailId) {
        integralDetailService.delDetail(integralDetailId);
        return Result.ok();
    }

    /**
     * 用户每日首次分享 获得积分值
     *
     * @return Result<BigDecimal>
     */
    @PostMapping("save/share")
    @Operation(summary = "用户首次分享获得积分")
    @PreAuthorize("@S.consumer")
    public Result<BigDecimal> userShare() {
        SecureUser<Object> secureUser = ISecurity.userMust();
        BigDecimal bigDecimal = integralDetailService.addIntegralDetail(secureUser.getShopId(), secureUser.getId());
        return Result.ok(bigDecimal);
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @PostMapping("get/userInfo")
    @Operation(summary = "用户信息")
    public Result<UserInfoVo> userInfo() {
        SecureUser feignCurUser = ISecurity.userMust();
        String userId = feignCurUser.getId();
        if (userId == null) {
            throw new ServiceException("数据错误", SystemCode.DATA_NOT_EXIST_CODE);
        }
        UserInfoVo userInfo = integralDetailService.getUserInfo(userId);
        return Result.ok(userInfo);
    }

    //=====================pc端客户管理操作==============================

    /**
     * 积分明细获取
     *
     * @param integralDetailDto 查询条件
     * @return 符合条件的数据
     */
    @GetMapping("gain/detail")
    @Operation(summary = "pc端积分明细获取")
    public Result<PageUtils<IntegralDetailVo>> getUserIntegralDetail(IntegralDetailDto integralDetailDto) {
        PageUtils<IntegralDetailVo> pageUtils = new PageUtils<>(integralDetailService.getIntegralDetail(integralDetailDto));
        return Result.ok(pageUtils);
    }

    /**
     * 批量充值或扣除积分
     *
     * @param userIds  用戶ids
     * @param integral 变化的积分
     * @param isDeduct 是否扣除
     * @return
     */
    @PutMapping("change")
    @Operation(summary = "商家给用户充值或扣除积分")
    public Result<Void> integralChange(@RequestBody String[] userIds,
                                 @Parameter(description = "integral", required = true) BigDecimal integral,
                                 @RequestParam(value = "isDeduct") Boolean isDeduct) {
        if (isDeduct) {
            //批量扣除
            integralDetailService.deductIntegral(Arrays.asList(userIds), integral);
        } else {
            //批量充值
            integralDetailService.rechargeIntegral(Arrays.asList(userIds), integral);
        }
        return Result.ok();
    }

    /**
     * 订阅已完成的订单 生成明细 通知用户
     *
     * @param orderVo 订单详情
     * @return Result<Void>
     */
    @PutMapping("posta")
    @Operation(summary = "订单订阅测试  -- 未使用")
    public Result<Void> interg(OrderVo orderVo) {
        integralDetailService.orderFinish(orderVo);
        return Result.ok();
    }

    /**
     * 商家给用户充值积分
     *
     * @param integral 充值的积分
     * @return Result<Void>
     */
    @PutMapping("recharge")
    @Operation(summary = "商家给用户充值积分")
    public Result<Void> shopRecharge(@Parameter(description = "充值积分值", required = true) BigDecimal integral,
                               @Parameter(description = "用户id", required = true) String userId) {
        return Result.ok();
    }

    /**
     * 商家扣除用户积分
     *
     * @param integral 扣除积分值
     * @return Result<Void>
     */
    @PutMapping("deduct")
    @Operation(summary = "商家扣除用户积分")
    public Result<Void> shopDeduct(@Parameter(description = "扣除积分值", required = true) BigDecimal integral,
                             @Parameter(description = "用户id", required = true) String userId) {
        return Result.ok();
    }

}
