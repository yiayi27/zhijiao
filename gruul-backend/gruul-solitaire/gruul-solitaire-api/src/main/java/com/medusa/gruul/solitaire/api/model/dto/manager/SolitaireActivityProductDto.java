package com.medusa.gruul.solitaire.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.solitaire.api.entity.SolitaireActivityProduct;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 接龙活动商品传输对象
 * </p>
 *
 * @author lcysike
 * @since 2024-06-05
 */
@Data
@Schema(title = "SolitaireActivityProductDto", description = "接龙活动商品传输对象")
public class SolitaireActivityProductDto {

    private Long id;

    @Schema(title = "接龙活动id")
    private Long activityId;

    @Schema(title = "商品id")
    private Long productId;

    public SolitaireActivityProduct coverActivityProduct() {
        SolitaireActivityProduct solitaireActivityProduct = new SolitaireActivityProduct();
        BeanUtil.copyProperties(this, solitaireActivityProduct);
        return solitaireActivityProduct;
    }
}
