package com.medusa.gruul.goods.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * sku的库存
 * </p>
 *
 * @author alan
 * @since 2023-09-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "t_sku_stock", autoResultMap = true)
@Schema(title = "SkuStock对象", description = "sku的库存")
public class SkuStock extends BaseEntity {

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
     * 版本号
     */
//    @Version
//    @Schema(title = "版本号")
//    @TableField("version")
//    private Integer version;
    /**
     * 产品id
     */
    @Schema(title = "产品id")
    @TableField("product_id")
    private Long productId;

    /**
     * sku编码
     */
    @Schema(title = "sku编码")
    @TableField("sku_code")
    private String skuCode;

    /**
     * 商品规格
     */
    @NotBlank
    @Schema(title = "商品规格")
    @TableField("specs")
    private String specs;

    /**
     * 商品sku重量
     */
    @NotBlank
    @Schema(title = "商品sku重量")
    @TableField("weight")
    private BigDecimal weight;

    /**
     * 展示图片
     */
    @NotBlank
    @Size(max = 255)
    @Schema(title = "展示图片")
    @TableField("pic")
    private String pic;

    /**
     * 实售价
     */
    @NotNull
    @Schema(title = "实售价")
    @TableField("price")
    private BigDecimal price;

    /**
     * 指导价（划线价）
     */
    @NotNull
    @Schema(title = "指导价（划线价）")
    @TableField("original_price")
    private BigDecimal originalPrice;

    /**
     * 库存
     */
    @NotNull
    @Schema(title = "库存")
    @TableField("stock")
    private Integer stock;

    /**
     * 预警库存
     */
    @NotNull
    @Schema(title = "预警库存")
    @TableField("low_stock")
    private Integer lowStock;

    /**
     * 销量
     */
    @Schema(title = "销量")
    @TableField("sale")
    private Integer sale;

    /**
     * 限购数量
     */
    @Schema(title = "限购数量")
    @TableField("per_limit")
    private Integer perLimit;

    /**
     * 返利价格类型 0-不使用 1-价格 2-比例 3-使用全局返利设置
     */
    @NotNull
    @Schema(title = "返利价格类型 0-不使用 1-价格 2-比例 3-使用全局返利设置")
    @TableField("rebate_type")
    private Integer rebateType;

    /**
     * 会员价格类型 0-不使用 1-价格 2-比例 3-使用全局会员权益设置
     */
    @NotNull
    @Schema(title = "会员价格类型 0-不使用 1-价格 2-比例 3-使用全局会员权益设置")
    @TableField("member_type")
    private Integer memberType;

    /**
     * 商品规格id列表 如[10,11,12]
     */
    @TableField(value = "product_spec_ids", typeHandler = JacksonTypeHandler.class)
    private List<Long> productSpecIds;

    @TableField(value = "product_spec_names", typeHandler = JacksonTypeHandler.class)
    private List<String> productSpecNames;

}