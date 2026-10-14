package com.medusa.gruul.recycling.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 基金所得订单关联表
 * </p>
 *
 * @author whh
 * @since 2024-04-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_recycling_ass_order")
@Schema(title = "RecyclingAssOrder对象", description = "基金所得订单关联表")
public class RecyclingAssOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(title = "主键")
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
     * 店铺用户id
     */
    @Schema(title = "店铺用户id")
    @TableField("shop_user_id")
    private Long shopUserId;

    /**
     * 获得基金额度
     */
    @Schema(title = "获得基金额度")
    @TableField("fund_quota")
    private BigDecimal fundQuota;


    /**
     * 订单实付金额
     */
    @Schema(title = "订单实付金额")
    private BigDecimal payAmount;


    /**
     * 状态  1-有效  2-订单撤销
     */
    @Schema(title = "状态  1-有效  2-订单撤销")
    @TableField("status")
    private Integer status;


    /**
     * 用户名称
     */
    @Schema(title = "用户名称")
    @TableField("nick_name")
    private String nickName;

    /**
     * 头像url
     */
    @Schema(title = "头像url")
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 订单商品数量
     */
    @Schema(title = "订单商品数量")
    @TableField("product_total_quantity")
    private Integer productTotalQuantity;

}
