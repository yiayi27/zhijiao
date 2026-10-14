package com.medusa.gruul.goods.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>
 * 商品信息Vo
 * </p>
 *
 * @author lcysike
 * @since 2023-10-06
 */
@Data
@Schema(title = "ProductVo对象", description = "商品查询返回信息")
public class ProductVo implements Serializable {

    private Long id;

    @Schema(title = "创建时间")
    private LocalDateTime createTime;

    @Schema(title = "商品分拣分类名称")
    private String sortingCategoryName;

    @Schema(title = "供应商id")
    private Long providerId;

    @Schema(title = "供应商名称")
    private String providerName;

    @Schema(title = "配送方式(0--商家配送，1--快递配送 2--同城配送)")
    private Integer distributionMode;

    @Schema(title = "运费模板ID")
    private Long freightTemplateId;


    @Schema(title = "佣金类型(0--金额，1--比例)")
    private Integer commissionType;


    @Schema(title = "属性模版ID")
    private Long attributeId;

    @Schema(title = "属性模板名称")
    private String attributeName;

    @Schema(title = "限购类型(默认统一规格，0--统一规格，1--统一限购，2--规格限购)")
    private Integer limitType;

    @Schema(title = "销售专区(默认商超系统，0--商超系统，2--限时秒杀)")
    private Long saleMode;

    @Schema(title = "商品名称")
    private String name;

    @Schema(title = "展示图片")
    private String pic;

    @Schema(title = "宽屏展示图片")
    private String widePic;

    @Schema(title = "画册图片，连产品图片限制为6张，以逗号分割")
    private String albumPics;

    @Schema(title = "视频url")
    private String videoUrl;

    @Schema(title = "货号")
    private String productSn;

    @Schema(title = "状态(默认上架，0--下架，1--上架)")
    private Integer status;

    @Schema(title = "接龙状态(0--未参加，1--已参加)")
    private Integer dominoState;

    @Schema(title = "排序")
    private Integer sort;

    @Schema(title = "销量")
    private Integer sale;

    @Schema(title = "赠送的积分")
    private Integer giftPoint;

    @Schema(title = "单位")
    private String unit;

    @Schema(title = "商品重量，默认为克")
    private BigDecimal weight;

    @Schema(title = "以逗号分割的产品服务：1->无忧退货；2->快速退款；3->免费包邮")
    private String serviceIds;

    @Schema(title = "商品详情")
    private String detail;

    @Schema(title = "规格是否展开")
    private Boolean openSpecs;

    @Schema(title = "销售属性")
    private String attribute;

    @Schema(title = "卖点描述")
    private String saleDescribe;

    @Schema(title = "评分")
    private BigDecimal score;

    @Schema(title = "是否使用会员价(0--不使用, 1--固定金额, 2--固定百分比)")
    private Integer useMemberPrice;

    @Schema(title = "是否使用返利(0--不使用, 1--固定金额, 2--固定百分比)")
    private Integer useRebate;

    @Schema(title = "是否使用分销(0--不使用, 1--固定金额, 2--固定百分比)")
    private Integer useDistribution;

    @Schema(title = "分销佣金参数")
    private BigDecimal distributionAmount;

    @Schema(title = "商品多规格信息")
    private List<ProductSpecVo> productSpecs;

    @Schema(title = "商品sku信息")
    private List<SkuStockVo> skuStocks;

    @Schema(title = "商品属性信息")
    private List<ProductAttributeVo> productAttributes;

    @Schema(title = "商品展示分类信息")
    private List<ProductShowCategoryVo> productShowCategorys;

}