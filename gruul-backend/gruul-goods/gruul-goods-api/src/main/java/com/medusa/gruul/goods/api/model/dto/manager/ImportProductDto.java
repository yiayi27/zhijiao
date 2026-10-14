package com.medusa.gruul.goods.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.goods.api.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


/**
 * <p>
 * 导入商品信息
 * </p>
 *
 * @author lcysike
 * @since 2024-05-07
 */
@Data
@Schema(title = "导入商品信息DTO")
public class ImportProductDto {

    @Schema(title = "供应商id")
    private Long providerId;

    @NotNull
    @Schema(title = "配送方式(0--商家配送，1--快递配送)")
    private Integer distributionMode;

    @Schema(title = "运费模板ID")
    private Long freightTemplateId;


    @Schema(title = "佣金类型(0--金额，1--比例)")
    private Integer commissionType;


    @Schema(title = "属性模版ID")
    private Long attributeId;

    @Schema(title = "属性模板名称")
    private String attributeName;

    @NotNull
    @Schema(title = "限购类型(默认统一规格，0--统一规格，1--统一限购，2--规格限购)")
    private Integer limitType;

    @NotNull
    @Schema(title = "销售专区(默认商超系统，0--商超系统，2--限时秒杀)")
    private Long saleMode;

    @NotBlank
    @Schema(title = "商品名称")
    private String name;

    @NotBlank
    @Schema(title = "展示图片")
    private String pic;

    @Schema(title = "宽屏展示图片")
    private String widePic;

    @NotBlank
    @Schema(title = "画册图片，连产品图片限制为6张，以逗号分割")
    private String albumPics;

    @Schema(title = "视频url")
    private String videoUrl;

    @Schema(title = "货号")
    private String productSn;

    @Schema(title = "状态(默认上架，0--下架（仓库中），1--上架，2--已售完)")
    private Integer status;

    @Schema(title = "接龙状态(0--未参加，1--已参加)")
    private Integer dominoState;

    @Schema(title = "销量")
    private Integer sale;

    @Schema(title = "赠送的积分")
    private Integer giftPoint;

    @Schema(title = "单位")
    private String unit;

    @NotNull
    @Schema(title = "商品重量，默认为克")
    private BigDecimal weight;

    @Schema(title = "以逗号分割的产品服务：1->无忧退货；2->快速退款；3->免费包邮")
    private String serviceIds;

    @Schema(title = "商品详情")
    private String detail;

    @NotNull
    @Schema(title = "规格是否展开")
    private Boolean openSpecs;

    @Schema(title = "销售属性")
    private String attribute;

    @Schema(title = "评分")
    private BigDecimal score;

    @Schema(title = "商品sku信息")
    private List<SkuStockDto> skuStocks;

    @Schema(title = "商品属性信息")
    private List<ProductAttributeDto> productAttributes;

    @Schema(title = "商品分销设置信息")
    private List<DistributionSetDto> distributionSets;

    @Schema(title = "商品展示分类信息")
    private List<ProductShowCategoryDto> productShowCategorys;

    public Product coverProduct() {
        Product product = new Product();
        BeanUtil.copyProperties(this, product);
        return product;
    }
}