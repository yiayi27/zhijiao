package com.medusa.gruul.goods.api.model.vo.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品规格信息
 *
 * @author kyl
 * @since 2023-10-06
 */
@Data
@Schema(title = "ApiSkuStockVo对象", description = "小程序商品Sku查询返回信息")
public class ApiSkuStockVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 版本号
     */
    @Schema(title = "版本号")
    private Long version;

    /**
     * 产品id
     */
    @Schema(title = "产品id")
    private Long productId;

    /**
     * sku编码
     */
    @Schema(title = "sku编码")
    private String skuCode;

    /**
     * 商品规格
     */
    @Schema(title = "商品规格")
    private String specs;

    /**
     * 商品sku重量
     */
    @Schema(title = "商品sku重量")
    private BigDecimal weight;

    /**
     * 展示图片
     */
    @Schema(title = "展示图片")
    private String pic;

    /**
     * 实售价
     */
    @Schema(title = "实售价")
    private BigDecimal price;

    /**
     * 指导价（划线价）
     */
    @Schema(title = "指导价（划线价）")
    private BigDecimal originalPrice;

    /**
     * 库存
     */
    @Schema(title = "库存", example = "100")
    private Integer stock;

    /**
     * 预警库存
     */
    @Schema(title = "预警库存", example = "10")
    private Integer lowStock;

    /**
     * 销量
     */
    @Schema(title = "销量", example = "50")
    private Integer sale;

    /**
     * 限购数量
     */
    @Schema(title = "限购数量", example = "10")
    private Integer perLimit;

    /**
     * 返利价格类型 0-不使用 1-价格 2-比例 3-使用全局返利设置
     */
    @Schema(title = "返利价格类型 0-不使用 1-价格 2-比例 3-使用全局返利设置")
    private Integer rebateType;

    /**
     * 会员价格类型 0-不使用 1-价格 2-比例 3-使用全局会员权益设置
     */
    @Schema(title = "会员价格类型 0-不使用 1-价格 2-比例 3-使用全局会员权益设置")
    private Integer memberType;

    /**
     * z
     */
    @Schema(title = "商品返利信息")
    private List<ApiSkuRebateVo> skuRebates;

    /**
     * 商品会员信息
     */
    @Schema(title = "商品会员信息")
    private List<ApiMemberPriceVo> memberPrices;

    @Schema(title = "商品规格id列表")
    private List<Long> productSpecIds;

    @Schema(title = "商品规格名称列表")
    private List<String> productSpecNames;
}