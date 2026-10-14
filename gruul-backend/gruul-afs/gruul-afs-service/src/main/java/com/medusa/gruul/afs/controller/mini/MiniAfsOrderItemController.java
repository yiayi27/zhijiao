package com.medusa.gruul.afs.controller.mini;

import com.medusa.gruul.afs.service.IAfsOrderItemService;
import com.medusa.gruul.common.core.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @Description:
 * @Author: xiaoq
 * @Date : 2024-07-21 20:09
 */
@RestController
@RequestMapping("/mini/user")
public class MiniAfsOrderItemController {
    @Resource
    IAfsOrderItemService afsOrderItemService;

    /**
     * 获取用户退款次数 -- 针对商品
     * @param orderId 订单id
     */
    @Operation(summary = "获取当前订单的售后次数")
    @GetMapping("/apply/goods/number")
    @PreAuthorize("permitAll()")
    public Result<HashMap<String, Object>> getOrderGoodsApplyNumber(@RequestParam(value = "orderId") @NotNull Long orderId) {
        HashMap<String, Object> stringObjectHashMap = afsOrderItemService.userApply(orderId);
        return Result.ok(stringObjectHashMap);
    }
}
