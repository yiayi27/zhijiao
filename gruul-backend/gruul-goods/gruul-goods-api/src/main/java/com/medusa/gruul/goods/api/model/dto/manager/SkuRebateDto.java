package com.medusa.gruul.goods.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.goods.api.entity.SkuRebate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 商品返利信息
 * </p>
 *
 * @author lcysike
 * @since 2024-05-21
 */
@Data
@Schema(title = "新增或修改返利信息DTO")
public class SkuRebateDto implements Serializable {

    private Long id;

    @Schema(title = "产品id")
    private Long productId;

    @NotNull
    @Schema(title = "产品sku-id")
    private Long skuId;

    @Schema(title = "返利金额")
    private BigDecimal rebatePrice;

    public SkuRebate coverSkuRebate() {
        SkuRebate skuRebate = new SkuRebate();
        BeanUtil.copyProperties(this, skuRebate);
        return skuRebate;
    }
}