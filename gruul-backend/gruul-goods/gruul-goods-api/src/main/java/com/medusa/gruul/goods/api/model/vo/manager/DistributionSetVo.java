package com.medusa.gruul.goods.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 商品分销设置表
 * </p>
 *
 * @author lcysike
 * @since 2023-09-30
 */
@Data
@Schema(title = "DistributionSet对象", description = "商品分销查询返回信息")
public class DistributionSetVo {

    private static final long serialVersionUID = 1L;

    private Long id;

    @Schema(title = "产品id")
    private Long productId;

    @Schema(title = "产品sku-id")
    private Long skuId;

    @Schema(title = "一级佣金")
    private BigDecimal firstCommission;

    @Schema(title = "二级佣金")
    private BigDecimal secondCommission;

    @Schema(title = "三级佣金")
    private BigDecimal thirdCommission;
}