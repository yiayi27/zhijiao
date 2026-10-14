package com.medusa.gruul.order.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单
 *
 * @author alan
 * @date 2023/11/5 21:29
 */
@Data
@Schema(title = "订单")
public class OrderDto {
    private static final long serialVersionUID = 1L;


    /**
     * 订单id
     */
    @Schema(title = "订单id")
    private Long id;

    /**
     * 订单状态
     */
    @Schema(title = "订单状态")
    private Integer status;

    /**
     * 用户ID
     */
    @Schema(title = "用户ID")
    private String userId;

    /**
     * 订单更新时间
     */
    @Schema(title = "更新时间")
    private LocalDateTime updateTime;


}
