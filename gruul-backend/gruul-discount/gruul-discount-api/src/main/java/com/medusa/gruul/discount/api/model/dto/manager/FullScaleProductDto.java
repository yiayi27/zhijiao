package com.medusa.gruul.discount.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.discount.api.entity.FullScaleProduct;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 满减活动适用商品
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Data
@Schema(title = "新增或修改满减活动适用商品信息DTO")
public class FullScaleProductDto {

    private Long id;

    @Schema(title = "满减活动id")
    private Long fullScaleId;

    @Schema(title = "商品id")
    @NotBlank(message = "商品id不能为空")
    private Long productId;

    public FullScaleProduct coverFullScaleProduct() {
        FullScaleProduct fullScaleProduct = new FullScaleProduct();
        BeanUtil.copyProperties(this, fullScaleProduct);
        return fullScaleProduct;
    }
}
