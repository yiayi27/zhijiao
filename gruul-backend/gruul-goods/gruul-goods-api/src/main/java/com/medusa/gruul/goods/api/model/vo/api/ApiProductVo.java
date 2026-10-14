package com.medusa.gruul.goods.api.model.vo.api;

import com.medusa.gruul.goods.api.model.vo.manager.ProductSpecVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品信息
 *
 * @author kyl
 * @since 2023-10-06
 */
@Data
@Schema(title = "ApiProductVo对象", description = "小程序商品查询返回信息")
public class ApiProductVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 供应商id
     */
    @Schema(title = "供应商id")
    private Long providerId;

    /**
     * 供应商名称
     */
    @Schema(title = "供应商名称")
    private String providerName;

    /**
     * 配送方式(0--商家配送，1--快递配送 2--同城配送)
     */
    @Schema(title = "配送方式(0--商家配送，1--快递配送 2--同城配送)")
    private Integer distributionMode;

    /**
     * 运费模板ID
     */
    @Schema(title = "运费模板ID")
    private Long freightTemplateId;


    /**
     * 属性模版ID
     */
    @Schema(title = "属性模版ID")
    private Long attributeId;

    /**
     * 属性模板名称
     */
    @Schema(title = "属性模板名称")
    private String attributeName;

    /**
     * 限购类型(默认统一规格，0--统一规格，1--统一限购，2--规格限购)
     */
    @Schema(title = "限购类型(默认统一规格，0--统一规格，1--统一限购，2--规格限购)")
    private Integer limitType;

    /**
     * 销售专区(默认商超系统，0--商超系统，2--限时秒杀)
     */
    @Schema(title = "销售专区(默认商超系统，0--商超系统，2--限时秒杀)")
    private Long saleMode;

    /**
     * 商品名称
     */
    @Schema(title = "商品名称")
    private String name;

    /**
     * 展示图片
     */
    @Schema(title = "展示图片")
    private String pic;

    /**
     * 宽屏展示图片
     */
    @Schema(title = "宽屏展示图片")
    private String widePic;

    /**
     * 画册图片，连产品图片限制为6张，以逗号分割
     */
    @Schema(title = "画册图片，连产品图片限制为6张，以逗号分割")
    private String albumPics;

    /**
     * 视频url
     */
    @Schema(title = "视频url")
    private String videoUrl;

    /**
     * 货号
     */
    @Schema(title = "货号")
    private String productSn;

    /**
     * 状态(默认上架，0--下架，1--上架)
     */
    @Schema(title = "状态(默认上架，0--下架，1--上架)", example = "1")
    private Integer status;

    /**
     * 排序
     */
    @Schema(title = "排序", example = "1")
    private Integer sort;

    /**
     * 销量
     */
    @Schema(title = "销量", example = "100")
    private Integer sale;

    /**
     * 赠送的积分
     */
    @Schema(title = "赠送的积分")
    private Integer giftPoint;

    /**
     * 单位
     */
    @Schema(title = "单位")
    private String unit;

    /**
     * 商品重量，默认为克
     */
    @Schema(title = "商品重量，默认为克")
    private BigDecimal weight;

    /**
     * 以逗号分割的产品服务：1->无忧退货；2->快速退款；3->免费包邮
     */
    @Schema(title = "以逗号分割的产品服务：1->无忧退货；2->快速退款；3->免费包邮")
    private String serviceIds;

    /**
     * 商品详情
     */
    @Schema(title = "商品详情")
    private String detail;

    /**
     * 规格是否展开
     */
    @Schema(title = "规格是否展开")
    private Boolean openSpecs;

    /**
     * 销售属性
     */
    @Schema(title = "销售属性")
    private String attribute;

    /**
     * 卖点描述
     */
    @Schema(title = "卖点描述")
    private String saleDescribe;

    /**
     * 评分
     */
    @Schema(title = "评分")
    private BigDecimal score;


    /**
     * 商品规格
     */
    @Schema(title = "商品规格")
    private List<ApiSkuStockVo> skuStocks;

    /**
     * 佣金类型(0--金额，1--比例)
     */
    @Schema(title = "佣金类型(0--金额，1--比例)")
    private Integer commissionType;

    /**
     * 是否使用会员价
     */
    @Schema(title = "是否使用会员价(0--不使用, 1--固定金额, 2--固定百分比)")
    private Integer useMemberPrice;

    /**
     * 是否使用返利
     */
    @Schema(title = "是否使用返利(0--不使用, 1--固定金额, 2--固定百分比)")
    private Integer useRebate;

    /**
     * 商品属性
     */
    @Schema(title = "商品属性")
    private List<ApiProductAttributeVo> productAttributes;

    /**
     * 商品展示分类信息
     */
    @Schema(title = "商品展示分类信息")
    private List<ApiProductShowCategoryVo> productShowCategorys;

    /**
     * 商品规格信息
     */
    @Schema(title = "商品多规格信息")
    private List<ProductSpecVo> productSpecs;

}