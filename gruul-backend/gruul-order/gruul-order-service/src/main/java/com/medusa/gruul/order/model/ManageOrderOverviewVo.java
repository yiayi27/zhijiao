package com.medusa.gruul.order.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * The type Manage order overview vo.
 * <p>
 * 订单概况
 *
 * @author alan
 * @date 2023/11/13 20:20
 */
@Data
@Schema(title = "订单概况")
public class ManageOrderOverviewVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 待付款订单
     */
    @Schema(title = "待付款订单")
    private Long waitForPay;

    /**
     * 待发货订单
     */
    @Schema(title = "待发货订单")
    private Long waitForSend;

    /**
     * 待签收订单
     */
    @Schema(title = "待签收订单")
    private Long shipped;

    /**
     * 待提货订单
     */
    @Schema(title = "待提货订单")
    private Long waitForPickup;
}
