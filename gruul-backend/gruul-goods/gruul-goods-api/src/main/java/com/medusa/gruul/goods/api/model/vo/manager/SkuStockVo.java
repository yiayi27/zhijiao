package com.medusa.gruul.goods.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author lcysike
 * @Description: 商品Sku查询
 * @since 2023-10-06
 */
@Data
@Schema(title = "SkuStockVo对象", description = "商品Sku查询返回信息")
public class SkuStockVo implements Serializable {

    private Long id;

    @Schema(title = "版本号", example = "1")
    private Long version;

    @Schema(title = "产品id", example = "1")
    private Long productId;

    @Schema(title = "sku编码")
    private String skuCode;

    @Schema(title = "商品规格")
    private String specs;

    @Schema(title = "商品sku重量")
    private BigDecimal weight;

    @Schema(title = "展示图片")
    private String pic;

    @Schema(title = "实售价")
    private BigDecimal price;

    @Schema(title = "指导价（划线价）")
    private BigDecimal originalPrice;

    @Schema(title = "库存", example = "100")
    private Integer stock;

    @Schema(title = "预警库存", example = "10")
    private Integer lowStock;

    @Schema(title = "销量", example = "50")
    private Integer sale;

    @Schema(title = "限购数量", example = "10")
    private Integer perLimit;

    @Schema(title = "返利价格类型 0-不使用 1-价格 2-比例 3-使用全局返利设置")
    private Integer rebateType;

    @Schema(title = "会员价格类型 0-不使用 1-价格 2-比例 3-使用全局会员权益设置")
    private Integer memberType;

    @Schema(title = "商品会员信息")
    private List<MemberPriceVo> memberPrices;

    @Schema(title = "商品返利信息")
    private List<SkuRebateVo> skuRebates;

    @Schema(title = "商品规格id列表")
    private List<Long> productSpecIds;

    @Schema(title = "商品规格名称列表")
    private List<String> productSpecNames;
}