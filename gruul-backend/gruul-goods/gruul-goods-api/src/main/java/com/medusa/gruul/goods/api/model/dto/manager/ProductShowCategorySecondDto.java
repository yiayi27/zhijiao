package com.medusa.gruul.goods.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.goods.api.entity.ProductShowCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * <p>
 * 展示分类
 * </p>
 *
 * @author lcysike
 * @since 2023-10-03
 */
@Data
@Schema(title = "新增展示分类二级分类DTO")
public class ProductShowCategorySecondDto {

    private Long id;

    @Schema(title = "产品id")
    private Long productId;

    @Schema(title = "上机分类的编号：0表示一级分类")
    @NotNull
    private Long parentId;

    @Schema(title = "展示分类id")
    private String showCategoryId;


    public ProductShowCategory coverProductShowCategory() {
        ProductShowCategory productShowCategory = new ProductShowCategory();
        BeanUtil.copyProperties(this, productShowCategory);
        return productShowCategory;
    }
}