package com.medusa.gruul.solitaire.api.model.vo.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author lcysike
 */
@Data
public class ApiSolitaireRecordVo {

    /**
     * 接龙记录id
     */
    @Schema(title = "接龙记录id")
    private Long id;

    /**
     * 创建时间
     */
    @Schema(title = "创建时间")
    private LocalDateTime createTime;

    /**
     * 订单id
     */
    @Schema(title = "订单id")
    private String orderId;

    /**
     * 订单支付金额
     */
    private BigDecimal payAmount;

    /**
     * 买家id
     */
    @Schema(title = "买家id")
    private String buyerId;

    /**
     * 买家名称
     */
    @Schema(title = "买家名称")
    private String buyerName;

    /**
     * 买家头像
     */
    @Schema(title = "买家头像")
    private String buyerIcon;

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