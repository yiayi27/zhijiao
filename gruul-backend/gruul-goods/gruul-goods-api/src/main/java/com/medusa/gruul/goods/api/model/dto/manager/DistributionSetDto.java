package com.medusa.gruul.goods.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.goods.api.entity.DistributionSet;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
@Schema(title = "新增或修改分销设置DTO")
public class DistributionSetDto {

    private Long id;

    @Schema(title = "产品id")
    private Long productId;

    @Schema(title = "产品sku-id")
    private Long skuId;

    @NotNull
    @Schema(title = "一级佣金")
    private BigDecimal firstCommission;

    @NotNull
    @Schema(title = "二级佣金")
    private BigDecimal secondCommission;

    public DistributionSet coverDistributionSet() {
        DistributionSet distributionSet = new DistributionSet();
        BeanUtil.copyProperties(this, distributionSet);
        return distributionSet;
    }
}