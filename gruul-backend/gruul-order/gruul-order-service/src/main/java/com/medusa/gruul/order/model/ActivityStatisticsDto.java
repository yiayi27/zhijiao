package com.medusa.gruul.order.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * The type Activity statistics dto.
 * @author alan
 */
@Data
public class ActivityStatisticsDto {
    /**
     * 订单id
     */
    @Schema(title = "订单id")
    private Long id;


    /**
     * 用户id
     */
    @Schema(title = "用户id")
    private String userId;


    /**
     * 订单总金额
     */
    @Schema(title = "订单总金额")
    private BigDecimal totalAmount;


    /**
     * 所属活动id
     */
    @Schema(title = "所属活动id")
    private Long activityId;

}
