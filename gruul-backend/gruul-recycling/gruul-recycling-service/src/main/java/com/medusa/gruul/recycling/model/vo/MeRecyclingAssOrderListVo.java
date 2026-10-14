package com.medusa.gruul.recycling.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
/**
 * @author whh
 * @description
 * @data: 2024/5/9
 */
public class MeRecyclingAssOrderListVo {
    @Schema(title = "订单id")
    private Long orderId;
    @Schema(title = "订单支付时间")
    private LocalDateTime orderPayTime;
    @Schema(title = "应付金额（实际支付金额）")
    private BigDecimal payAmount;
}
