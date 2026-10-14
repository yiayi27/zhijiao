package com.medusa.gruul.goods.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 商品返利金额信息
 * </p>
 *
 * @author lcysike
 * @since 2024-05-20
 */
@Data
@Schema(title = "SkuRebateVo对象", description = "商品返利金额信息")
public class SkuRebateVo implements Serializable {

    private Long id;

    @Schema(title = "产品id")
    private Long productId;

    @Schema(title = "产品sku-id")
    private Long skuId;

    @Schema(title = "返利金额")
    private BigDecimal rebatePrice;

}