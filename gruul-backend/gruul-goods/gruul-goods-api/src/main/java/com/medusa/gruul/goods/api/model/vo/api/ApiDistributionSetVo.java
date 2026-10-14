package com.medusa.gruul.goods.api.model.vo.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 商品分销设置信息
 * </p>
 *
 * @author lcysike
 * @since 2024-11-08
 */
@Data
@Schema(title = "ApiDistributionSetVo对象", description = "商品分销设置信息")
public class ApiDistributionSetVo implements Serializable {

    private Long id;

    @Schema(title = "产品id")
    private Long productId;

    @Schema(title = "产品sku-id")
    private Long skuId;

    @Schema(title = "一级佣金")
    private BigDecimal firstCommission;

    @Schema(title = "二级佣金")
    private BigDecimal secondCommission;

}