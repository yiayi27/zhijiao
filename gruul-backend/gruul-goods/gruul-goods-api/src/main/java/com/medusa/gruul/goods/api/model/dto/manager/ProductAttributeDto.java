package com.medusa.gruul.goods.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.goods.api.entity.ProductAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>
 * 商品属性
 * </p>
 *
 * @author lcysike
 * @since 2023-10-03
 */
@Data
@Schema(title = "新增或修改商品属性DTO")
public class ProductAttributeDto {

    private Long id;

    @Schema(title = "产品id")
    private Long productId;

    @NotBlank
    @Schema(title = "属性名称")
    private String name;

    @Schema(title = "属性值")
    private String value;

    public ProductAttribute coverProductAttribute() {
        ProductAttribute productAttribute = new ProductAttribute();
        BeanUtil.copyProperties(this, productAttribute);
        return productAttribute;
    }
}