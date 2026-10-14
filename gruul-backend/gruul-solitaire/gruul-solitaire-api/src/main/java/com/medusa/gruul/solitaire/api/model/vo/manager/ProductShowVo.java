package com.medusa.gruul.solitaire.api.model.vo.manager;

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
public class ProductShowVo implements Serializable {

    private Long id;

    @Schema(title = "商品名称")
    private String name;

    @Schema(title = "展示图片")
    private String pic;

    @Schema(title = "货号")
    private Long productSn;

    @Schema(title = "规格类型")
    private Integer limitType;

    @Schema(title = "最小价格")
    private BigDecimal minPrice;

    @Schema(title = "最大价格")
    private BigDecimal maxPrice;
}