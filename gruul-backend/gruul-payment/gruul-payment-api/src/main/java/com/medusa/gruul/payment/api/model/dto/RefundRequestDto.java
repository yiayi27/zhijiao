package com.medusa.gruul.payment.api.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author zq
 * @date 2023/11/06
 */
@Data
public class RefundRequestDto {

    /**
     * orderId
     */
    @Schema(title = "订单id")
    @NotBlank(message = "外部交易订单号不能为空")
    private String orderId;

    /**
     * routeKey
     */
    @Schema(title = "路由键,路由键和回调url必须选一个")
    @NotBlank(message = "路由键不能为空")
    private String routeKey;

    /**
     * totalFee
     */
    @Schema(title = "退款总金额")
    @NotNull(message = "退款金额不能为空")
    @Min(value = 1, message = "退款金额不能小于1分钱")
    private Integer totalFee;

    /**
     * 店铺id
     */
    private String shopId;
}
