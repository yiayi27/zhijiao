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

import java.math.BigDecimal;

/**
 * <p>
 * 商品分销设置表
 * </p>
 *
 * @author lcysike
 * @since 2023-09-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_distribution_set")
@Schema(title = "DistributionSet对象", description = "商品分销设置表")
public class DistributionSet extends BaseEntity {

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
     * 产品sku-id
     */
    @NotNull
    @Schema(title = "产品sku-id")
    @TableField("sku_id")
    private Long skuId;

    /**
     * 一级佣金
     */
    @NotNull
    @Schema(title = "一级佣金")
    @TableField("first_commission")
    private BigDecimal firstCommission;

    /**
     * 二级佣金
     */
    @NotNull
    @Schema(title = "二级佣金")
    @TableField("second_commission")
    private BigDecimal secondCommission;

}