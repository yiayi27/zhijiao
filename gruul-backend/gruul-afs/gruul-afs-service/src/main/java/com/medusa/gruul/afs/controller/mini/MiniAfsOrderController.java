package com.medusa.gruul.afs.controller.mini;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medusa.gruul.afs.api.entity.AfsOrder;
import com.medusa.gruul.afs.api.entity.AfsReason;
import com.medusa.gruul.afs.model.*;
import com.medusa.gruul.afs.service.IAfsOrderService;
import com.medusa.gruul.afs.service.IAfsReasonService;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.common.core.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 售后工单 前端控制器
 * </p>
 *
 * @author alan
 * @since 2024-08-05
 */
@Tag(name = "小程序端接口")
@RestController
@RequestMapping("/mini/user")
public class MiniAfsOrderController {
    @Resource
    private IAfsOrderService afsOrderService;
    @Resource
    private IAfsReasonService afsReasonsService;

    /**
     * 用户查询售后订单信息
     *
     * @param dto 售后订单信息
     * @return Result<PageUtils<ApiAfsOrderVo>>
     */
    @Operation(summary = "用户查询订单", description = "查询售后订单")
    @GetMapping("/search")
    public Result<PageUtils<ApiAfsOrderVo>> searchOrder(SearchOrderDto dto) {
        PageUtils<ApiAfsOrderVo> page = afsOrderService.searchOrder(dto);
        return Result.ok(page);
    }

    /**
     * 申请售后的原因
     *
     * @return Result<List<String>>
     */
    @Operation(summary = "申请售后的原因")
    @GetMapping("/reasons")
    public Result<List<String>> reasons() {
        List<String> reasonsList =
                afsReasonsService.list(new LambdaQueryWrapper<AfsReason>()
                                .select(AfsReason::getName)).parallelStream()
                        .map(AfsReason::getName)
                        .toList();
        return Result.ok(reasonsList);
    }

    /**
     * 用户申请售后
     *
     * @param dto 申请售后信息
     * @return the result
     */
    @Operation(summary = "用户申请售后")
    @PostMapping("/apply")
    public Result<Long> userApply(@RequestBody @Validated UserApplyDto dto) {
        AfsOrder afsOrder = afsOrderService.userApply(dto);
        return Result.ok(afsOrder.getId());
    }

    /**
     * 获取当前订单的售后次数
     *
     * @param orderId 订单id
     * @return Result<HashMap<String, Object>>
     */
    @Operation(summary = "获取当前订单的售后次数")
    @GetMapping("/apply/number")
    public Result<HashMap<String, Object>> getOrderApplyNumber(@RequestParam(value = "orderId") @NotNull Long orderId) {
        Integer number = afsOrderService.getUserApplyNumber(orderId);
        Boolean expire = afsOrderService.getAfsExpire(orderId);
        HashMap<String, Object> res = new HashMap<>(CommonConstants.NUMBER_TWO);
        res.put("number", number);
        res.put("expire", expire);
        return Result.ok(res);
    }

    /**
     * 获取当前售后的退货地址
     *
     * @param orderId 订单id
     * @return Result<ReturnAddressVo>
     */
    @Operation(summary = "获取当前售后的退货地址")
    @GetMapping("/return/address")
    public Result<ReturnAddressVo> getReturnAddress(@RequestParam(value = "orderId") @NotNull Long orderId) {
        ReturnAddressVo returnAddressVo = afsOrderService.getReturnAddress(orderId);
        return Result.ok(returnAddressVo);
    }

    /**
     * 根据售后单ID获取售后详情
     *
     * @param afsId 售后订单id
     * @return Result<AfsOrderVo>
     */
    @Operation(summary = "根据售后单ID获取售后详情")
    @GetMapping("/info")
    public Result<AfsOrderVo> getAfsOrderInfo(@RequestParam(value = "afsId") @NotNull Long afsId) {
        AfsOrderVo vo = afsOrderService.getAfsOrderInfo(afsId);
        return Result.ok(vo);
    }

    /**
     * 用户取消申请
     *
     * @param afsId 售后订单id
     * @return Result<Void>
     */
    @Operation(summary = "用户取消申请")
    @PutMapping("/cancel")
    public Result<Void> userCancel(@RequestParam(value = "afsId") @NotNull Long afsId) {
        afsOrderService.userCancel(afsId, false);
        return Result.ok();
    }

    /**
     * 用户确认已退货
     *
     * @param afsId           售后订单id
     * @param deliveryCode    快递公司编码
     * @param deliveryCompany 快递公司名称
     * @param deliverySn      快递单号
     * @param phone           收件人电话
     * @param reason          原因
     * @return Result<Void>
     */
    @Operation(summary = "用户确认已退货")
    @PutMapping("/return")
    public Result<Void> userReturn(@RequestParam(value = "afsId") @NotNull Long afsId,
                             @RequestParam(value = "deliveryCode", required = false) String deliveryCode,
                             @RequestParam(value = "deliveryCompany", required = false) String deliveryCompany,
                             @RequestParam(value = "deliverySn", required = false) String deliverySn,
                             @RequestParam(value = "phone", required = false) String phone,
                             @RequestParam(value = "reason", required = false) String reason
    ) {
        afsOrderService.userReturn(afsId, deliveryCode, deliveryCompany, deliverySn, phone, reason);
        return Result.ok();
    }

}
