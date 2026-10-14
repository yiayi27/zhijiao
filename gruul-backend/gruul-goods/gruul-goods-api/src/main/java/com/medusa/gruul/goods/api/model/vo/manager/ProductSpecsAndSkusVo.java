package com.medusa.gruul.goods.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张治保
 * date 2024/11/13
 */
@Getter
@Setter
@Accessors(chain = true)
@Schema(title = "商品规格列表与sku列表")
public class ProductSpecsAndSkusVo implements Serializable {
    /**
     * 规格列表
     */
    @Schema(title = "商品规格列表")
    private List<ProductSpecVo> specs;

    /**
     * sku列表
     */
    @Schema(title = "商品sku列表")
    private List<SkuStockVo> skus;

}
