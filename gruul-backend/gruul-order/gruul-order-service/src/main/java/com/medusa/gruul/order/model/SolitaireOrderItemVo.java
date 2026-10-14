package com.medusa.gruul.order.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * The type Solitaire order item vo.
 */
@Data
public class SolitaireOrderItemVo {
    /**
     * 商品名
     */
    @Schema(title = "商品名")
    private String productName;

    /**
     * 购买数量
     */
    @Schema(title = "购买数量")
    private Integer productQuantity;
}
