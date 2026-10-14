package com.medusa.gruul.goods.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.goods.api.entity.SkuStock;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 商品sku
 * </p>
 *
 * @author lcysike
 * @since 2023-10-06
 */
@Data
@Schema(title = "新增或修改商品sku DTO")
public class SkuStockDto implements Serializable {

    private Long id;

//    @Schema(title = "版本号")
//    private Long version;

    @Schema(title = "产品id")
    private Long productId;

    @NotNull
    @Schema(title = "sku编码")
    private String skuCode;

    @NotBlank
    @Schema(title = "商品规格")
    private String specs;

    @NotBlank
    @Schema(title = "商品规格名称列表")
    private List<String> productSpecNames;

    @Schema(title = "商品sku重量")
    private BigDecimal weight;

    @NotBlank
    @Schema(title = "展示图片")
    private String pic;

    @NotNull
    @Schema(title = "实售价")
    private BigDecimal price;

    @NotNull
    @Schema(title = "指导价（划线价）")
    private BigDecimal originalPrice;

    @NotNull
    @Schema(title = "库存")
    private Integer stock;

    @NotNull
    @Schema(title = "预警库存")
    private Integer lowStock;

    @Schema(title = "销量")
    private Integer sale;

    @Schema(title = "限购数量")
    private Integer perLimit;

    @Schema(title = "返利价格类型 0-不使用 1-价格 2-比例 3-使用全局返利设置")
    private Integer rebateType;

    @Schema(title = "会员价格类型 0-不使用 1-价格 2-比例 3-使用全局会员权益设置")
    private Integer memberType;

    @Schema(title = "商品会员价信息")
    private List<MemberPriceDto> memberPrices;

    @Schema(title = "商品返利信息")
    private List<SkuRebateDto> skuRebates;

    public SkuStock coverSkuStock() {
        SkuStock skuStock = new SkuStock();
        BeanUtil.copyProperties(this, skuStock, "productSpecNames");
        skuStock.setProductSpecNames(this.getProductSpecNames());
        return skuStock;
    }
}