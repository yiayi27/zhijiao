package com.medusa.gruul.discount.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.discount.api.entity.CouponProduct;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 优惠劵适用商品
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Data
@Schema(title = "新增或修改优惠劵适用商品信息DTO")
public class CouponProductDto {

    private Long id;

    @Schema(title = "优惠劵id")
    private Long couponId;

    @Schema(title = "商品id")
    @NotBlank(message = "商品id不能为空")
    private Long productId;

    public CouponProduct coverCouponProduct() {
        CouponProduct couponProduct = new CouponProduct();
        BeanUtil.copyProperties(this, couponProduct);
        return couponProduct;
    }


}
