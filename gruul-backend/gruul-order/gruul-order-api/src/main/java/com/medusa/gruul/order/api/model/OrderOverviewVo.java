package com.medusa.gruul.order.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 用户订单概况
 *
 * @author alan
 * @date 2023/11/13 20:20
 */
@Data
@Accessors(chain = true)
public class OrderOverviewVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 待付款订单
     */
    @Schema(title = "待付款订单")
    private Long waitForPay;

    /**
     * 配送中订单
     */
    @Schema(title = "配送中订单")
    private Long shipped;

    /**
     * 待提货订单
     */
    @Schema(title = "待提货订单")
    private Long waitForPickup;

    /**
     * 待发货订单
     */
    @Schema(title = "待发货订单")
    private Long withDelivery;

    /**
     *
     */
    @Schema(title = "售后订单")
    private Long afsOrder;
}
