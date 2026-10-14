package com.medusa.gruul.goods.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 商品会员价格表
 * </p>
 *
 * @author alan
 * @since 2023-09-03
 */
@Data
@Schema(title = "MemberPrice对象", description = "商品会员价查询返回信息")
public class MemberPriceVo implements Serializable {

    private Long id;

    @Schema(title = "产品id")
    private Long productId;

    @Schema(title = "产品sku-id")
    private Long skuId;

    @Schema(title = "会员等级id")
    private Long memberLevelId;

    @Schema(title = "会员价格/比例")
    private BigDecimal memberPrice;

    @Schema(title = "会员等级名称")
    private String memberLevelName;
}