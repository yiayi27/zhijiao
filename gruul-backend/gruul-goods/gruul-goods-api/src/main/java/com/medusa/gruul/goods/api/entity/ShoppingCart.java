package com.medusa.gruul.goods.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 购物车信息
 * </p>
 *
 * @author lcysike
 * @since 2023-11-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_shopping_cart")
@Schema(title = "ShoppingCart对象", description = "购物车信息")
public class ShoppingCart extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 本店店铺id
     */
    @Schema(title = "本店店铺id")
    @TableField("shop_id")
    private String shopId;

    /**
     * 产品id
     */
    @NotNull
    @Schema(title = "产品id")
    @TableField("product_id")
    private Long productId;

    /**
     * 用户id
     */
    @NotNull
    @Schema(title = "用户id")
    @TableField("user_id")
    private String userId;

    /**
     * sku_id
     */
    @NotNull
    @Schema(title = "sku_id")
    @TableField("sku_id")
    private Long skuId;

    /**
     * 商品数量
     */
    @NotNull
    @Schema(title = "商品数量")
    @TableField("goods_number")
    private Integer goodsNumber;

    /**
     * 活动id
     */
    @NotNull
    @Schema(title = "活动id")
    @TableField("activity_id")
    private Long activityId;

    /**
     * 活动开始时间
     */
    @Schema(title = "活动开始时间")
    @TableField("activity_start_time")
    private Date activityStartTime;

    /**
     * 活动结束时间
     */
    @Schema(title = "活动结束时间")
    @TableField("activity_end_time")
    private Date activityEndTime;
}
