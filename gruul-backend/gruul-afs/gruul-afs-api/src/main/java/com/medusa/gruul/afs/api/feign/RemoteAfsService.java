package com.medusa.gruul.afs.api.feign;

import com.medusa.gruul.afs.api.model.AfsSimpleVo;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * The interface Remote afs service.
 *
 * @author alan
 * @description: RemoteAfsService.java
 * @date 2024/9/24 21:58
 */
@FeignClient(value = "afs")
@Hidden
public interface RemoteAfsService {

    /**
     * 根据发货单ID查询相关的售后单信息,该方法使用Feign默认店铺信息，不包含默认查询全部
     *
     * @param receiptBillId the receipt bill id
     * @return the afs order by receipt bill id
     */
    @GetMapping("/remote/{receiptBillId}")
    List<AfsSimpleVo> getAfsOrderByReceiptBillId(@PathVariable(value = "receiptBillId") @NotNull Long receiptBillId);

    /**
     * 更新补货单的相关售后状态
     *
     * @param orderIds the order ids
     */
    @PutMapping("/remote/replenish")
    void updateReplenish(@RequestParam(value = "orderIds") List<Long> orderIds);

    /**
     * 获取用户订单数量
     *
     * @param userId
     * @return
     */
    @GetMapping("/remote/afsOrderNumber")
    Long getAfsOrderNumber(@RequestParam(value = "userId") String userId);
}
