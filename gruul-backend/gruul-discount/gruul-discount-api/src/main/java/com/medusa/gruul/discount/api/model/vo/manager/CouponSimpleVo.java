package com.medusa.gruul.discount.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 优惠劵
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Data
@Schema(title = "CouponSimpleVo", description = "优惠劵查询返回信息")
public class CouponSimpleVo {

    @Schema(title = "优惠劵信息")
    private CouponVo couponVo;

    @Schema(title = "优惠劵关联商品详细信息")
    private List<DiscountProductShowVo> discountProductVos;
}
