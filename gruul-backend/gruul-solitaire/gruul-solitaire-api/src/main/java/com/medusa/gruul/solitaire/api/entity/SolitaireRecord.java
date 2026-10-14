package com.medusa.gruul.solitaire.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;


/**
 * <p>
 * 接龙活动记录表
 * </p>
 *
 * @author lcysike
 * @since 2024-07-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_solitaire_record")
@Schema(title = "t_solitaire_record对象", description = "接龙记录表")
public class SolitaireRecord extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    @TableField(value = "order_id")
    private String orderId;

    /**
     * 订单支付金额
     */
    @TableField(value = "pay_amount")
    private BigDecimal payAmount;

    /**
     * 接龙活动id
     */
    @TableField(value = "activity_id")
    private String activityId;

    /**
     * 买家id
     */
    @TableField(value = "buyer_id")
    private String buyerId;

    /**
     * 买家名称
     */
    @TableField(value = "buyer_name")
    private String buyerName;

    /**
     * 买家头像
     */
    @TableField(value = "buyer_icon")
    private String buyerIcon;

    /**
     * 商品名
     */
    @Schema(title = "商品名")
    @TableField("product_name")
    private String productName;

    /**
     * 购买数量
     */
    @Schema(title = "购买数量")
    @TableField("product_quantity")
    private Integer productQuantity;

    @Schema(title = "商铺ID")
    @TableField("shop_id")
    private String shopId;


}