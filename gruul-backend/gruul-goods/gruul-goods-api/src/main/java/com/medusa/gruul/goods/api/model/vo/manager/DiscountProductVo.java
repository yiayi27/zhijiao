package com.medusa.gruul.goods.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * <p>
 * 商品信息Vo
 * </p>
 *
 * @author lcysike
 * @since 2023-10-06
 */
@Data
@Schema(title = "DiscountProductVo对象", description = "商品查询返回信息")
public class DiscountProductVo implements Serializable {

    private Long id;

    @Schema(title = "商品名称")
    private String name;

    @Schema(title = "展示图片")
    private String pic;

    @Schema(title = "宽屏展示图片")
    private String widePic;

    @Schema(title = "画册图片，连产品图片限制为6张，以逗号分割")
    private String albumPics;

    @Schema(title = "货号")
    private Long productSn;

    @Schema(title = "规格类型")
    private Integer limitType;

    @Schema(title = "最小价格")
    private BigDecimal minPrice;

    @Schema(title = "最大价格")
    private BigDecimal maxPrice;

    @Schema(title = "销售专区")
    private Long saleMode;
}