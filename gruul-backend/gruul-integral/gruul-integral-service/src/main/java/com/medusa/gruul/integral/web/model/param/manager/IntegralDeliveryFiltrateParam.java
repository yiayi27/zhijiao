package com.medusa.gruul.integral.web.model.param.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Description: 积分订单批量发货筛选
 * @Author: xiaoq
 * @Date: 2024/4/8 16:48
 */
@Data
@Schema(title = "积分订单批量发货筛选")
public class IntegralDeliveryFiltrateParam {

    @Schema(title = "用户微信名")
    private String wxuserName;

    @Schema(title = "积分商品名称")
    private String integralGoodName;

    @Schema(title = "收货地址")
    private String address;

    @Schema(title = "积分订单id")
    private Long prizeOrderId;

    /**
     * 订单ids ,分隔
     */
    @NotNull
    private String integralOrders;
}
