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

import java.math.BigDecimal;

/**
 * <p>
 * 商品返利表
 * </p>
 *
 * @author lcysike
 * @since 2024-05-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_sku_rebate")
@Schema(title = "SkuRebate对象", description = "商品返利信息")
public class SkuRebate extends BaseEntity {

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
     * 返利金额
     */
    @NotNull
    @Schema(title = "返利金额")
    @TableField("rebate_price")
    private BigDecimal rebatePrice;

}