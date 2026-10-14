package com.medusa.gruul.discount.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 优惠劵适用商品
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Data
@Schema(title = "CouponProductVo", description = "优惠劵适用商品查询返回信息")
public class CouponProductVo {

    private Long id;

    @Schema(title = "优惠劵id")
    private Long couponId;

    @Schema(title = "商品id")
    private Long productId;


}
