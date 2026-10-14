package com.medusa.gruul.goods.api.model.vo.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品会员信息
 *
 * @author kyl
 * @since 2023-10-06
 */
@Data
@Schema(title = "ApiMemberPrice对象", description = "小程序商品会员价查询返回信息")
public class ApiMemberPriceVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 产品id
     */
    @Schema(title = "产品id")
    private Long productId;

    /**
     * 产品sku-id
     */
    @Schema(title = "产品sku-id")
    private Long skuId;

    /**
     * 会员等级id
     */
    @Schema(title = "会员等级id")
    private Long memberLevelId;

    /**
     * 会员价格
     */
    @Schema(title = "会员价格/比例")
    private BigDecimal memberPrice;

    /**
     * 会员等级名称
     */
    @Schema(title = "会员等级名称")
    private String memberLevelName;
}