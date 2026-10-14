package com.medusa.gruul.afs.controller.remote;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medusa.gruul.afs.api.entity.AfsOrder;
import com.medusa.gruul.afs.api.model.AfsSimpleVo;
import com.medusa.gruul.afs.service.IAfsOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The type Remote afs controller.
 *
 * @author alan
 * @description: RemoteAfsController.java
 * @date 2024/10/5 09:59
 */
@RestController
@RequestMapping("/remote")
@Tag(name = "Feign接口")
public class RemoteAfsController {
    @Resource
    private IAfsOrderService afsOrderService;

    /**
     * 售后单详情,该方法使用Feign默认店铺信息，不包含默认查询全部
     *
     * @param receiptBillId the receipt bill id
     * @return the afs order by receipt bill id
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "售后单详情")
    @GetMapping("/{receiptBillId}")
    public List<AfsSimpleVo> getAfsOrderByReceiptBillId(@PathVariable(value = "receiptBillId") @NotNull Long receiptBillId) {
        return afsOrderService.getAfsOrderByReceiptBillId(receiptBillId);
    }

    /**
     * 更新补货单的相关售后状态
     *
     * @param orderIds the order ids
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "更新补货单的相关售后状态")
    @PutMapping("/replenish")
    public void updateReplenish(@RequestParam(value = "orderIds") List<Long> orderIds) {
        afsOrderService.updateReplenish(orderIds);

    }


    /**
     * 获取用户订单数量
     *
     * @param userId
     * @return
     */
    @GetMapping("/remote/afsOrderNumber")
    Long getAfsOrderNumber(@RequestParam(value = "userId") String userId) {
        List<AfsOrder> afsOrders = afsOrderService.getBaseMapper().selectList(new LambdaQueryWrapper<AfsOrder>()
                .eq(AfsOrder::getUserId, userId));
        long size = afsOrders.size();
        return size;
    }

}
