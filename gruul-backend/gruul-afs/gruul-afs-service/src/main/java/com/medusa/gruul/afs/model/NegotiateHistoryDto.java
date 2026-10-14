package com.medusa.gruul.afs.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * The type Negotiate history dto.
 *
 * @author alan
 * @description: 获取订单售后协商历史
 * @date 2024/8/5 21:54
 */
@Data
@Schema(title = "获取订单售后协商历史")
public class NegotiateHistoryDto {

    @Schema(title = "需要查询的订单")
    private Long orderId;

    @Schema(title = "用户类型 1.用户，不传查询所有")
    private String type;
}
