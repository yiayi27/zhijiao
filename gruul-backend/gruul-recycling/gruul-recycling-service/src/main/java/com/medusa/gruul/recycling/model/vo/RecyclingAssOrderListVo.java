package com.medusa.gruul.recycling.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
/**
 * @author whh
 * @description
 * @data: 2024/5/5
 */
public class RecyclingAssOrderListVo {

    @Schema(title = "用户昵称")
    private String nickName;

    @Schema(title = "头像url")
    private String avatarUrl;

    @Schema(title = "交易总额")
    private BigDecimal totalAmount;

    @Schema(title = "订单数")
    private Integer orderNumber;

    @Schema(title = "最后交易时间")
    private LocalDateTime lastPayTime;

    @Schema(title = "总基金数")
    private BigDecimal totalFundQuota;
}
