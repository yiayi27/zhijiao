package com.medusa.gruul.goods.api.model.vo.manager;

import com.medusa.gruul.goods.api.model.vo.api.ApiSkuRebateVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @description: ItemVo.java
 * @author: alan
 * @date: 2023/11/5 21:27
 */
@Data
@Schema(title = "所选商品详情")
public class ItemVo implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 商品id
     */
    @Schema(title = "商品id", example = "1")
    private Long productId;

    /**
     * 商品图片
     */
    @Schema(title = "商品图片")
    private String productPic;

    /**
     * 商品名
     */
    @Schema(title = "商品名")
    private String productName;

    /**
     * 商品编号
     */
    @Schema(title = "商品编号")
    private String productSn;

    /**
     * 销售价格
     */
    @Schema(title = "销售价格")
    private BigDecimal productPrice;

    /**
     * 指导价（划线价）
     */
    @Schema(title = "指导价（划线价）")
    private BigDecimal productOriginalPrice;

    /**
     * 购买数量
     */
    @Schema(title = "购买数量")
    private Integer productQuantity;

    /**
     * 商品sku编号
     */
    @Schema(title = "商品sku编号", example = "1")
    private Long productSkuId;

    /**
     * 商品sku条码
     */
    @Schema(title = "商品sku条码")
    private String productSkuCode;


    /**
     * 商品图片
     */
    @Schema(title = "商品图片")
    private String productSkuPic;


    /**
     * 商品的销售属性
     */
    @Schema(title = "商品的销售属性")
    private String specs;

    /**
     * 配送方式(0--商家配送，1--快递配送 2--同城配送)
     */
    @Schema(title = "配送方式(0--商家配送，1--快递配送 2--同城配送)")
    private Integer distributionMode;

    /**
     * 商品的运费模版id
     */
    @Schema(title = "运费模板ID")
    private Long freightTemplateId;

    /**
     * 库存状态
     */
    @Schema(title = "库存状态：0->有库存；1->无库存")
    private Integer status;

    /**
     * 限购类型(默认统一规格，0--统一规格，1--统一限购，2--规格限购)
     */
    @Schema(title = "限购类型(默认统一规格，0--统一规格，1--统一限购，2--规格限购)")
    private Integer limitType;

    /**
     * 限购数量
     */
    @Schema(title = "限购数量")
    private Integer perLimit;

    /**
     * 赠送的积分
     */
    @Schema(title = "赠送的积分")
    private Integer giftIntegration;

    /**
     * 佣金类型(0--金额，1--比例)
     */
    @Schema(title = "佣金类型(0--金额，1--比例)")
    private Integer commissionType;


    /**
     * 商品重量，默认为克
     */
    @Schema(title = "商品重量，默认为克")
    private BigDecimal weight;

    /**
     * 供应商id
     */
    @Schema(title = "供应商id")
    private Long providerId;

    /**
     * 返利价格类型 0-不使用 1-价格 2-比例
     */
    @Schema(title = "返利价格类型 0-不使用 1-价格 2-比例")
    private Integer rebateType;

    /**
     * 是否使用分销(0--不使用, 1--固定金额, 2--固定百分比)
     */
    @Schema(title = "是否使用分销(0--不使用, 1--固定金额, 2--固定百分比)")
    private Integer useDistribution;

    /**
     * 分销佣金参数
     */
    @Schema(title = "分销佣金参数")
    private BigDecimal distributionAmount;

    /**
     * 会员价格类型 0-不使用 1-价格 2-比例
     */
    @Schema(title = "会员价格类型 0-不使用 1-价格 2-比例")
    private Integer memberType;

    /**
     * 商品返利信息
     */
    @Schema(title = "商品返利信息")
    private List<ApiSkuRebateVo> skuRebates;

//    /**
//     * 商品分销信息
//     */
//    @Schema(title = "商品分销信息")
//    private List<ApiDistributionSetVo> distributionSets;

    /**
     * 商品会员信息
     */
    @Schema(title = "商品会员信息")
    private List<MemberPriceVo> memberPrices;


}
