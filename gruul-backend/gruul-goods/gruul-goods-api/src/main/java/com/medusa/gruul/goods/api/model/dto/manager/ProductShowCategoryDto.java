package com.medusa.gruul.goods.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.goods.api.entity.ProductShowCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;


/**
 * <p>
 * 展示分类
 * </p>
 *
 * @author lcysike
 * @since 2023-10-03
 */
@Data
@Schema(title = "新增产品展示分类大类DTO")
public class ProductShowCategoryDto {

    private Long id;

    @Schema(title = "产品id")
    private Long productId;

    @NotNull
    @Schema(title = "上级分类的编号：0表示一级分类")
    private Long parentId;

    @Schema(title = "展示分类id")
    private Long showCategoryId;

    @Schema(title = "下级分类")
    private List<ProductShowCategorySecondDto> productShowCategorySeconds;

    public ProductShowCategory coverProductShowCategory() {
        ProductShowCategory productShowCategory = new ProductShowCategory();
        BeanUtil.copyProperties(this, productShowCategory);
        return productShowCategory;
    }
}