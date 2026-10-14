package com.medusa.gruul.order.model;

import com.medusa.gruul.afs.api.entity.AfsOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The type Simple order item vo.
 * <p>
 * 简单的单个商品返回结果
 *
 * @author alan
 * @date 2023/11/5 21:29
 */
@Data
@Schema(title = "简单的单个商品返回结果")
public class SimpleOrderItemVo implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 订单详情id
     */
    @Schema(title = "订单详情id", example = "1")
    private Long id;


    /**
     * 商品id
     */
    @Schema(title = "商品id", example = "1")
    private Long productId;

    /**
     * 商品sku编号
     */
    @Schema(title = "商品sku编号", example = "1")
    private Long productSkuId;


    /**
     * 商品图片
     */
    @Schema(title = "商品图片")
    private String productPic;

    /**
     * 商品名
     */
    @Schema(title = "商品名")
    private String productName;

    /**
     * 销售价格
     */
    @Schema(title = "销售价格")
    private BigDecimal productPrice;

    /**
     * 实际价格
     */
    @Schema(title = "实际价格")
    private BigDecimal realAmount;

    /**
     * 指导价（划线价）
     */
    @Schema(title = "指导价（划线价）")
    private BigDecimal productOriginalPrice;

    /**
     * 购买数量
     */
    @Schema(title = "购买数量")
    private Integer productQuantity;


    /**
     * 商品规格
     */
    @Schema(title = "商品规格")
    private String specs;


    /**
     * 订单ID
     */
    @Schema(title = "订单ID")
    private Long orderId;

    /**
     * 售后信息
     */
    @Schema(title = "售后信息")
    private AfsOrder afs;

}
