package com.medusa.gruul.order.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * 订单中所包含的商品
 * </p>
 *
 * @author alan
 * @since 2023-11-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order_item")
@Schema(title = "OrderItem对象", description = "订单中所包含的商品")
public class OrderItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商铺ID
     */
    @Schema(title = "商铺ID")
    @TableField("shop_id")
    private String shopId;

    /**
     * 订单id
     */
    @Schema(title = "订单id")
    @TableField("order_id")
    private Long orderId;

    /**
     * 商品id
     */
    @Schema(title = "商品id")
    @TableField("product_id")
    private Long productId;

    /**
     * 商品图片
     */
    @Schema(title = "商品图片")
    @TableField("product_pic")
    private String productPic;

    /**
     * 商品名
     */
    @Schema(title = "商品名")
    @TableField("product_name")
    private String productName;

    /**
     * 商品编号
     */
    @Schema(title = "商品编号")
    @TableField("product_sn")
    private String productSn;

    /**
     * 销售价格 = 商品销售价格
     */
    @Schema(title = "销售价格,商品的原价")
    @TableField("product_price")
    private BigDecimal productPrice;

    /**
     * 团长佣金 = 单个商品的团长佣金
     */
    @Schema(title = "团长佣金")
    @TableField("brokerage_amount")
    private BigDecimal brokerageAmount;

    /**
     * 指导价（划线价）= 单个商品的指导价
     */
    @Schema(title = "指导价（划线价），商品的指导价")
    @TableField("product_original_price")
    private BigDecimal productOriginalPrice;

    /**
     * 购买数量
     */
    @Schema(title = "购买数量")
    @TableField("product_quantity")
    private Integer productQuantity;

    /**
     * 商品sku编号
     */
    @Schema(title = "商品sku编号")
    @TableField("product_sku_id")
    private Long productSkuId;

    /**
     * 商品sku条码
     */
    @Schema(title = "商品sku条码")
    @TableField("product_sku_code")
    private String productSkuCode;

    /**
     * 商品促销分解金额 = 满减活动优惠的金额
     */
    @Schema(title = "商品促销分解金额")
    @TableField("promotion_amount")
    private BigDecimal promotionAmount;

    /**
     * 优惠券优惠分解金额 = 多个商品的优惠券优惠金额
     */
    @Schema(title = "优惠券优惠分解金额")
    @TableField("coupon_amount")
    private BigDecimal couponAmount;

    /**
     * 积分优惠金额 = 多个商品的积分优惠金额
     */
    @Schema(title = "积分优惠分解金额")
    @TableField("integration_amount")
    private BigDecimal integrationAmount;

    /**
     * 会员优惠分解金额  = 多个商品的会员优惠金额
     */
    @Schema(title = "会员优惠分解金额")
    @TableField("member_amount")
    private BigDecimal memberAmount;

    /**
     * 多个商品经过优惠后的最终金额
     */
    @Schema(title = "多个商品经过优惠后的最终金额")
    @TableField("real_amount")
    private BigDecimal realAmount;

    /**
     * 赠送的积分
     */
    @Schema(title = "赠送的积分")
    @TableField("gift_integration")
    private BigDecimal giftIntegration;

    /**
     * 商品的销售属性
     */
    @Schema(title = "商品的销售属性")
    @TableField("specs")
    private String specs;


    /**
     * 所属活动id
     */
    @Schema(title = "所属活动id")
    @TableField("activity_id")
    private Long activityId;

    /**
     * 所属活动商品id
     */
    @Schema(title = "所属活动商品id")
    @TableField("activity_product_id")
    private Long activityProductId;

    /**
     * 供应商id
     */
    @Schema(title = "供应商id")
    @TableField("provider_id")
    private Long providerId;

    /**
     * 返利金额
     */
    @Schema(title = "返利金额")
    @TableField("rebate_amount")
    private BigDecimal rebateAmount;

    /**
     * 一级分销金额
     */
    @Schema(title = "一级分销金额")
    @TableField("first_distribution_amount")
    private BigDecimal firstDistributionAmount;

    /**
     * 二级分销金额
     */
    @Schema(title = "二级分销金额")
    @TableField("second_distribution_amount")
    private BigDecimal secondDistributionAmount;

    /**
     * 退款金额
     */
    @Schema(title = "退款金额")
    @TableField("refund_amount")
    private BigDecimal refundAmount;
}
